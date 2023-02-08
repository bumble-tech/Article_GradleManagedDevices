package com.bumble.devicefarm.plugin.device.farm

import com.android.build.api.dsl.Device
import com.android.build.api.instrumentation.ManagedDeviceTestRunner
import com.android.build.api.instrumentation.StaticTestData
import com.bumble.devicefarm.plugin.device.adb.AdbRunner
import org.gradle.api.logging.Logger
import java.io.File
import java.util.concurrent.Callable
import java.util.concurrent.Executors

internal class DeviceFarmTestRunner : ManagedDeviceTestRunner {

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
        dependencyApks: Set<File>,
    ): Boolean {
        managedDevice as DeviceFarmImpl

        val broker = DeviceBroker()
        val devices = broker.lease(managedDevice.shards.get())

        // Can't use WorkerExecutor here, StaticTestData is not serializable.
        val threadPool = Executors.newCachedThreadPool()

        val futures = devices.mapIndexed { index, device ->
            threadPool.submit(Callable {
                val runner = AdbRunner(
                    host = device.host,
                    port = device.port,
                    shardInfo = AdbRunner.ShardInfo(
                        index = index,
                        total = devices.size,
                    ),
                )
                runner.run(
                    name = "${managedDevice.name}-${device.host}-${device.port}",
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
        broker.release(devices)

        return success
    }

}