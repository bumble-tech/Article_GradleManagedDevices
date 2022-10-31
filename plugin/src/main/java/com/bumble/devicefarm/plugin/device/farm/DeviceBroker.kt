package com.bumble.devicefarm.plugin.device.farm

class DeviceBroker {

    fun lease(amount: Int): Collection<Device> {
        TODO("Make a network request to acquire devices, should wait if no available")
    }

    fun release(devices: Collection<Device>) {
        TODO("Make a network request to release release devices to make it available for others")
    }

    class Device(
        val host: String,
        val port: Int,
        val releaseToken: String,
    )

}