package com.bumble.devicefarm.plugin.device.single

import com.android.build.api.instrumentation.ManagedDeviceTestRunner
import com.android.build.api.instrumentation.ManagedDeviceTestRunnerFactory
import org.gradle.api.Project
import org.gradle.workers.WorkerExecutor

internal abstract class MyDeviceImpl(
    private val name: String,
) : MyDevice, ManagedDeviceTestRunnerFactory {

    init {
        host.convention("localhost")
        port.convention(5555)
    }

    override fun getName(): String = name

    override fun createTestRunner(
        project: Project,
        workerExecutor: WorkerExecutor,
        useOrchestrator: Boolean,
        enableEmulatorDisplay: Boolean
    ): ManagedDeviceTestRunner {
        host.finalizeValue()
        port.finalizeValue()

        return MyDeviceTestRunner()
    }

}