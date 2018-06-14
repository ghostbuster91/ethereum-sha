package io.ghostbuster91.ethereum.sha3

import io.github.ghostbuster91.ethereum.sha3.ContractFunction
import io.github.ghostbuster91.ethereum.sha3.Function
import io.github.ghostbuster91.ethereum.sha3.findCollisions
import org.junit.Assert
import org.junit.Test

class CollisionCheckerTest {

    @Test
    fun shouldFindSimpleCollision() {
        val code = readFile("SimpleCollision.sol")
        Assert.assertEquals(setOf(
                ContractFunction("test", Function(signature = "gsf()", identifier = "67e43e43")),
                ContractFunction("test", Function(signature = "tgeo()", identifier = "67e43e43"))), findCollisions(code))
    }

    @Test
    fun shouldNotFindFalseCollision() {
        val code = readFile("ZeroCollision.sol")
        Assert.assertEquals(emptySet<ContractFunction>(), findCollisions(code))
    }

    @Test
    fun shouldNotFindCollisionAcrossMultipleContractsWithinTheSameFile() {
        val code = readFile("MultipleContracts.sol")
        Assert.assertEquals(emptySet<ContractFunction>(), findCollisions(code))
    }

    @Test
    fun shouldFindCollisionWhenFunctionCollidesWithFunctionFromBaseClass() {
        val code = readFile("InheritanceContracts.sol")
        Assert.assertEquals(setOf(
                ContractFunction("otherTest", Function(signature = "gsf()", identifier = "67e43e43")),
                ContractFunction("otherTest", Function(signature = "tgeo()", identifier = "67e43e43"))), findCollisions(code))
    }

    private fun readFile(fileName: String) = javaClass.classLoader.getResource(fileName).readText()
}