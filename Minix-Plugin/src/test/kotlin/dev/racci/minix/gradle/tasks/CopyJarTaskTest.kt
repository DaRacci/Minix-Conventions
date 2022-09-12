package dev.racci.minix.gradle.tasks

import io.kotest.core.spec.style.FunSpec

class CopyJarTaskTest : FunSpec({
    // lateinit var task: CopyJarTask
    // lateinit var mockProject: Project
    // lateinit var mockLogger: Logger
    //
    // beforeEach {
    //     mockProject = mockk()
    //     mockLogger = mockk() {
    //         every { info(any()) } returns Unit
    //         every { error(any()) } returns Unit
    //     }
    //
    //     task = mockk() {
    //         every { run() } answers { callOriginal() }
    //         every { project } returns mockProject
    //         every { logger } returns mockLogger
    //     }
    // }
    //
    // afterEach { (testCase, result) ->
    //     unmockkAll()
    // }
    //
    // test("Verify CI Skip") {
    //     every { task.shouldSkipTask() } returns true
    //
    //     task.run()
    //
    //     verifyOrder {
    //         task.shouldSkipTask()
    //         mockLogger.info("Skipping copyJar because of CI environment.")
    //     }
    // }

    // test("Input file exists test") {
    //     every { task.inputFile } returns mockk {
    //         every { orNull } returns mockk {
    //             every { asFile } returns mockk {
    //                 every { exists() } returns true
    //             }
    //         }
    //     }
    //
    //     task.run()
    //
    //     verifyOrder {
    //         task.inputFile
    //         task.inputFile!!.orNull
    //         task.inputFile!!.orNull!!.asFile
    //         task.inputFile!!.orNull!!.asFile.exists()
    //         mockLogger.info("Using reobfJar output as input for copyJar: $file")
    //     }
    // }
})
