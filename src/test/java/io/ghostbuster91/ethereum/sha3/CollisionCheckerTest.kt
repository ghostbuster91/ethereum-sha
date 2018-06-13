package io.ghostbuster91.ethereum.sha3

import io.github.ghostbuster91.ethereum.sha3.Function
import io.github.ghostbuster91.ethereum.sha3.findCollisions
import org.junit.Assert
import org.junit.Test

class CollisionCheckerTest {

    @Test
    fun shouldFindSimpleCollision() {
        val code = readFile("SimpleCollision.sol")
        Assert.assertEquals(listOf(
                Function(parentContract = "test",signature = "gsf()", identifier = "67e43e43"),
                Function(parentContract = "test",signature = "tgeo()", identifier = "67e43e43")), findCollisions(code))
    }

    @Test
    fun shouldNotFindFalseCollision() {
        val code = readFile("ZeroCollision.sol")
        Assert.assertEquals(emptyList<Function>(), findCollisions(code))
    }

    @Test
    fun shouldNotFindCollisionAcrossMultipleContractsWithinTheSameFile() {
        val code = readFile("MultipleContracts.sol")
        Assert.assertEquals(emptyList<Function>(), findCollisions(code))
    }

    @Test
    fun name() {
        val code = readFile("InheritanceContracts.sol")
        Assert.assertEquals(false, findCollisions(code).isEmpty())
    }

    private fun readFile(fileName: String) = javaClass.classLoader.getResource(fileName).readText()
}