package com.bumble.devicefarm.plugin.device.proto

import com.android.build.gradle.internal.testing.utp.TEST_RESULT_PB_FILE_NAME
import com.google.testing.platform.proto.api.core.TestStatusProto
import com.google.testing.platform.proto.api.core.TestSuiteResultProto
import com.google.testing.platform.proto.api.core.TestSuiteResultProto.TestSuiteResult
import java.io.File

/**
 * Generate unique TEST_RESULT_PB_FILE_NAME file
 * to make Android Gradle plugin to regenerate HTML output every time.
 */
internal fun generateTestSuitResult(
    success: Boolean,
    outputDirectory: File,
) {
    val proto = TestSuiteResult
        .newBuilder()
        .setTestStatus(
            if (success) {
                TestStatusProto.TestStatus.PASSED
            } else {
                TestStatusProto.TestStatus.FAILED
            }
        )
        .setTestSuiteMetaData(
            TestSuiteResultProto.TestSuiteMetaData.newBuilder()
                .setTestSuiteName(System.currentTimeMillis().toString())
        )
        .build()

    File(outputDirectory, TEST_RESULT_PB_FILE_NAME).outputStream().use {
        proto.writeTo(it)
    }
}