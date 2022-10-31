package com.bumble.devicefarm.plugin.device.multiple

import com.android.build.api.dsl.Device
import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.Input

interface MultipleDevices : Device {

    @get:Input
    val devices: ListProperty<String>

}