package com.bumble.devicefarm.plugin.device.farm

import com.android.build.api.dsl.Device
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input

interface DeviceFarm : Device {

    @get:Input
    val shards: Property<Int>

}