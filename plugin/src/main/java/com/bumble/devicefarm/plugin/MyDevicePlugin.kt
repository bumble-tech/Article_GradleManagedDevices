package com.bumble.devicefarm.plugin

import com.android.build.api.dsl.CommonExtension
import com.android.build.gradle.api.AndroidBasePlugin
import com.bumble.devicefarm.plugin.device.farm.DeviceFarm
import com.bumble.devicefarm.plugin.device.farm.DeviceFarmImpl
import com.bumble.devicefarm.plugin.device.multiple.MultipleDevices
import com.bumble.devicefarm.plugin.device.multiple.MultipleDevicesImpl
import com.bumble.devicefarm.plugin.device.single.MyDevice
import com.bumble.devicefarm.plugin.device.single.MyDeviceImpl
import org.gradle.api.Plugin
import org.gradle.api.Project

class MyDevicePlugin : Plugin<Project> {

    override fun apply(target: Project) {
        target.plugins.withType(AndroidBasePlugin::class.java) {
            target.extensions.configure(CommonExtension::class.java) {
                it.testOptions.managedDevices.devices.apply {
                    registerBinding(
                        MyDevice::class.java,
                        MyDeviceImpl::class.java,
                    )
                    registerBinding(
                        MultipleDevices::class.java,
                        MultipleDevicesImpl::class.java,
                    )
                    registerBinding(
                        DeviceFarm::class.java,
                        DeviceFarmImpl::class.java,
                    )
                }
            }
        }
    }

}