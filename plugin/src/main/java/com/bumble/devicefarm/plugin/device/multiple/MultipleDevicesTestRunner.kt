package com.bumble.devicefarm.plugin.device.multiple

import com.android.build.api.dsl.Device
import com.android.build.api.instrumentation.ManagedDeviceTestRunner
import com.android.build.api.instrumentation.StaticTestData
import com.bumble.devicefarm.plugin.device.adb.AdbRunner
import com.bumble.devicefarm.plugin.device.proto.generateTestSuitResult
import org.gradle.api.logging.Logger
import java.io.File
import java.util.concurrent.Callable
import java.util.concurrent.Executors

internal class MultipleDevicesTestRunner : ManagedDeviceTestRunner {

    override fun runTests(
        managedDevice: Device,
        runId: String,
        outputDirectory: File,
        coverageOutputDirectory: File,
        additionalTestOutputDir: File?,
        projectPath: String,
        variantName: String,
        testData: StaticTestData,
        additionalInstallOptions: List<String>,
        helperApks: Set<File>,
        logger: Logger,
    ): Boolean {
        managedDevice as MultipleDevicesImpl

        val devices = managedDevice.devices.get().map {
            val split = it.split(':')
            split[0] to split[1].toInt()
        }

        // Can't use WorkerExecutor here, StaticTestData is not serializable.
        val threadPool = Executors.newCachedThreadPool()

        val futures = devices.mapIndexed { index, (host, port) ->
            threadPool.submit(Callable {
                val runner = AdbRunner(
                    host = host,
                    port = port,
                    shardInfo = AdbRunner.ShardInfo(
                        index = index,
                        total = devices.size,
                    ),
                )
                runner.run(
                    name = "${managedDevice.name}-${host}-${port}",
                    runId = runId,
                    outputDirectory = outputDirectory,
                    projectPath = projectPath,
                    variantName = variantName,
                    testData = testData,
                    additionalInstallOptions = additionalInstallOptions,
                    logger = logger,
                )
            })
        }

        val success = futures.all { it.get() }

        threadPool.shutdown()

        generateTestSuitResult(success, outputDirectory)

        return success
    }

}