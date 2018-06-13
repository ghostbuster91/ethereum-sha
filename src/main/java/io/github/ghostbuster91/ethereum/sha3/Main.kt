package io.github.ghostbuster91.ethereum.sha3

import com.xenomachina.argparser.ArgParser
import io.github.ghostbuster91.ethereum.sha3.parser.SolidityBaseListener
import io.github.ghostbuster91.ethereum.sha3.parser.SolidityParser
import org.antlr.v4.runtime.tree.ParseTreeWalker
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
    val collector = FunctionIdentifierCollector()
    ParseTreeWalker.DEFAULT.walk(collector, sourceUnitContext)
    val hashesToFunctions = collector.contractToFunctions
            .groupBy { it.identifier  + it.parentContract }
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

class FunctionIdentifierCollector : SolidityBaseListener() {

    private var currentContract: String? = null
    val contractToFunctions = mutableListOf<Function>()

    override fun enterFunctionDefinition(ctx: SolidityParser.FunctionDefinitionContext) {
        val functionName = ctx.identifier().Identifier().text
        val parameters = ctx.parameterList().parameter().joinToString(",") { it.typeName().text }
        val signature = "$functionName($parameters)"
        val identifier = Hash.sha3String(signature).drop(2).take(8)
        contractToFunctions.add(Function(currentContract!!,signature,identifier))
    }

    override fun enterContractDefinition(ctx: SolidityParser.ContractDefinitionContext) {
        val contractName = ctx.identifier().Identifier().text
        currentContract = contractName
    }
}

data class Function(val parentContract: String, val signature: String, val identifier: String)