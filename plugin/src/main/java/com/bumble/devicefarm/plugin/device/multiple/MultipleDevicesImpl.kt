package com.bumble.devicefarm.plugin.device.multiple

import com.android.build.api.instrumentation.ManagedDeviceTestRunner
import com.android.build.api.instrumentation.ManagedDeviceTestRunnerFactory
import org.gradle.api.Project
import org.gradle.workers.WorkerExecutor

internal abstract class MultipleDevicesImpl(
    private val name: String,
) : MultipleDevices, ManagedDeviceTestRunnerFactory {

    override fun getName(): String = name

    override fun createTestRunner(
        project: Project,
        workerExecutor: WorkerExecutor,
        useOrchestrator: Boolean,
        enableEmulatorDisplay: Boolean
    ): ManagedDeviceTestRunner {
        devices.finalizeValue()

        return MultipleDevicesTestRunner()
    }

}