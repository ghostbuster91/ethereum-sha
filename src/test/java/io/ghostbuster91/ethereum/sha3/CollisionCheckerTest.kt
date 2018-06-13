package io.ghostbuster91.ethereum.sha3

import io.github.ghostbuster91.ethereum.sha3.findCollisions
import org.junit.Assert
import org.junit.Test

class CollisionCheckerTest {

    @Test
    fun shouldFindSimpleCollision() {
        val code = readFile("SimpleCollision.sol")
        Assert.assertEquals(mapOf("67e43e43" to listOf("gsf()", "tgeo()")), findCollisions(code))
    }

    private fun readFile(fileName: String) = javaClass.classLoader.getResource(fileName).readText()
}