package com.bumble.devicefarm.plugin.device.single

import com.android.build.api.dsl.Device
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input

interface MyDevice : Device {

    @get:Input
    val host: Property<String>

    @get:Input
    val port: Property<Int>

}
