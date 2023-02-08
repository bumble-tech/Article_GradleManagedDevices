package com.bumble.devicefarm.plugin.device.single

import com.android.build.api.dsl.Device
import com.android.build.api.instrumentation.ManagedDeviceTestRunner
import com.android.build.api.instrumentation.StaticTestData
import com.bumble.devicefarm.plugin.device.adb.AdbRunner
import com.bumble.devicefarm.plugin.device.proto.generateTestSuitResult
import org.gradle.api.logging.Logger
import java.io.File

internal class MyDeviceTestRunner : ManagedDeviceTestRunner {

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
        managedDevice as MyDeviceImpl
        val runner = AdbRunner(managedDevice.host.get(), managedDevice.port.get())
        val success = runner.run(
            name = managedDevice.name,
            runId = runId,
            outputDirectory = outputDirectory,
            projectPath = projectPath,
            variantName = variantName,
            testData = testData,
            additionalInstallOptions = additionalInstallOptions,
            logger = logger,
        )
        generateTestSuitResult(success, outputDirectory)
        return success
    }

}