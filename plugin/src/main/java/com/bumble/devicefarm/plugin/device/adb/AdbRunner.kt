package com.bumble.devicefarm.plugin.device.adb

import com.android.build.api.instrumentation.StaticTestData
import com.android.build.gradle.internal.LoggerWrapper
import com.android.build.gradle.internal.testing.CustomTestRunListener
import com.android.builder.testing.api.DeviceConfig
import com.android.builder.testing.api.DeviceConfigProvider
import com.android.ddmlib.IDevice
import com.android.ddmlib.testrunner.RemoteAndroidTestRunner
import dadb.AdbShellPacket
import dadb.Dadb
import org.gradle.api.logging.Logger
import java.io.File

@Suppress("UnstableApiUsage")
internal class AdbRunner(
    private val host: String,
    private val port: Int,
    private val shardInfo: ShardInfo? = null,
) {

    fun run(
        name: String,
        runId: String,
        outputDirectory: File,
        projectPath: String,
        variantName: String,
        testData: StaticTestData,
        additionalInstallOptions: Collection<String>,
        logger: Logger,
    ): Boolean {
        // Write an XML report to outputDirectory, so Android plugin can use it to generate a combined one.
        val xmlWriterListener = CustomTestRunListener(
            name,
            projectPath,
            variantName,
            LoggerWrapper(logger),
        )
        xmlWriterListener.setReportDir(outputDirectory)
        xmlWriterListener.setHostName("$host:$port")

        val mode = RemoteAndroidTestRunner.StatusReporterMode.PROTO_STD
        val parser = mode.createInstrumentationResultParser(runId, listOf(xmlWriterListener))

        Dadb.create(host, port).use { dadb ->
            val apks = testData.testedApkFinder.invoke(DadbDeviceConfigProvider(dadb))
            if (apks.isNotEmpty()) {
                // Empty in case of library module
                dadb.installMultiple(
                    apks = apks,
                    options = additionalInstallOptions.toTypedArray(),
                )
            }
            dadb.install(
                file = testData.testApk,
                options = additionalInstallOptions.toTypedArray(),
            )

            var arguments = ""
            if (shardInfo != null) {
                arguments += "-e numShards ${shardInfo.total} -e shardIndex ${shardInfo.index}"
            }

            dadb.openShell("am instrument -w ${mode.amInstrumentCommandArg} $arguments ${testData.applicationId}/${testData.instrumentationRunner}")
                .use { stream ->
                    while (true) {
                        val packet: AdbShellPacket = stream.read()
                        if (packet is AdbShellPacket.Exit) break
                        parser.addOutput(packet.payload, 0, packet.payload.size)
                    }
                    parser.flush()
                }
        }

        return !xmlWriterListener.runResult.hasFailedTests()
    }

    class ShardInfo(
        val index: Int,
        val total: Int,
    )

    private class DadbDeviceConfigProvider(
        private val dadb: Dadb,
    ) : DeviceConfigProvider {

        private val config by lazy {
            synchronized(dadb) {
                val result = dadb.shell("am get-config")
                DeviceConfig.Builder.parse(result.output.split("\n"))
            }
        }

        override fun getConfigFor(abi: String): String =
            config.getConfigFor(abi)

        override fun getDensity(): Int =
            synchronized(dadb) {
                dadb
                    .shell("getptop ${IDevice.PROP_DEVICE_DENSITY}")
                    .output
                    .toIntOrNull()
                    ?: dadb
                        .shell("getprop ${IDevice.PROP_DEVICE_EMULATOR_DENSITY}")
                        .output
                        .toIntOrNull()
                    ?: -1
            }

        override fun getLanguage(): String =
            synchronized(dadb) {
                dadb
                    .shell("getprop ${IDevice.PROP_DEVICE_LANGUAGE}")
                    .output
            }

        override fun getRegion(): String =
            synchronized(dadb) {
                dadb
                    .shell("getprop ${IDevice.PROP_DEVICE_REGION}")
                    .output
            }

        override fun getAbis(): List<String> =
            config.abis

    }

}