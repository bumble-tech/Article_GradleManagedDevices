package com.bumble.devicefarm.plugin.device.farm

import com.android.build.api.instrumentation.ManagedDeviceTestRunner
import com.android.build.api.instrumentation.ManagedDeviceTestRunnerFactory
import org.gradle.api.Project
import org.gradle.workers.WorkerExecutor

internal abstract class DeviceFarmImpl(
    private val name: String,
) : DeviceFarm, ManagedDeviceTestRunnerFactory {

    init {
        shards.convention(1)
    }

    override fun getName(): String = name

    override fun createTestRunner(
        project: Project,
        workerExecutor: WorkerExecutor,
        useOrchestrator: Boolean,
        enableEmulatorDisplay: Boolean
    ): ManagedDeviceTestRunner {
        shards.finalizeValue()

        return DeviceFarmTestRunner()
    }

}