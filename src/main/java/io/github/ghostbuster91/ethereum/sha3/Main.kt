package io.github.ghostbuster91.ethereum.sha3

import com.xenomachina.argparser.ArgParser
import io.github.ghostbuster91.ethereum.sha3.parser.SolidityParser
import org.web3j.crypto.Hash
import java.io.File
import kotlin.system.exitProcess

class ParsedArgs(parser: ArgParser) {
    val input by parser.positional("input file")
}

fun main(args: Array<String>) {
    ArgParser(args)
            .parseInto(::ParsedArgs)
            .run {
                val collisions = findCollisions(File(input).readText())
                printCollisions(collisions)
                if (collisions.isNotEmpty()) {
                    exitProcess(1)
                } else {
                    println("Zero collisions detected.")
                    exitProcess(0)
                }
            }
}

fun findCollisions(code: String): List<Function> {
    val parserFacade = ParserFacade()
    val sourceUnitContext = parserFacade.parse(code)
    val list = sourceUnitContext.contractDefinition().flatMap { contract ->
        contract.contractPart().map {
            createFunction(it.functionDefinition(), contract.identifier().text)
        }
    }
    val hashesToFunctions = list
            .groupBy { it.identifier + it.parentContract }
    return hashesToFunctions.filter { it.value.size > 1 }.flatMap { it.value }
}

private fun printCollisions(functionsWithIds: List<Function>) {
    functionsWithIds
            .forEach { (hash, signatures) ->
                printCollision(signatures, hash)
            }
}

private fun printCollision(signatures: String, hash: String) {
    println("Collision detected!")
    println("Following contractToFunctions have the same evm identifier:")
    signatures.forEach {
        println("$it => $hash")
    }
}


data class Function(val parentContract: String, val signature: String, val identifier: String)

private fun createFunction(ctx: SolidityParser.FunctionDefinitionContext, contractName: String): Function {
    val functionName = ctx.identifier().Identifier().text
    val parameters = ctx.parameterList().parameter().joinToString(",") { it.typeName().text }
    val signature = "$functionName($parameters)"
    val identifier = Hash.sha3String(signature).drop(2).take(8)
    val function = Function(contractName, signature, identifier)
    return function
}