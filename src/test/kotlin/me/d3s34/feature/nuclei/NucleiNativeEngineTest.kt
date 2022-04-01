package me.d3s34.feature.nuclei

import kotlinx.coroutines.Dispatchers
import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

internal class NucleiNativeEngineTest {

    @Test
    fun scan() {
        val home = System.getProperty("user.home")
        val nucleiEngine = NucleiNativeEngine(
            "${home}/go/bin/nuclei",
            Dispatchers.Default
        )

        val result = nucleiEngine.scan(
            "d3s34.me",
            NucleiTemplateDir("${home}/nuclei-templates/dns/")
        )

        assertNotEquals(0, result.size)
        assertEquals(4, result.size)
    }

    @Test
    fun updateTemplate()  {
        val home = System.getProperty("user.home")
        val nucleiEngine = NucleiNativeEngine(
            "${home}/go/bin/nuclei",
            Dispatchers.Default
        )

        val tempDir = File("/tmp/nucleiTemp").apply {
            if (exists()) { delete() }
            mkdirs()
        }


        nucleiEngine.updateTemplate(
            NucleiTemplateDir(tempDir.path)
        )

        val templates = tempDir.walk()
            .maxDepth(1)
            .toList()

        assertNotEquals(1, templates.size)

        tempDir.apply {
            if (exists()) { delete() }
        }

    }
}