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

fun findCollisions(code: String): Map<String, List<String>> {
    val parserFacade = ParserFacade()
    val sourceUnitContext = parserFacade.parse(code)
    val collector = FunctionIdentifierCollector()
    ParseTreeWalker.DEFAULT.walk(collector, sourceUnitContext)
    val hashesToFunctions = collector.functions
            .groupBy { it.hash }
            .mapValues { it.value.map { it.signature } }
    return hashesToFunctions.filter { it.value.size > 1 }
}

private fun printCollisions(functionsWithIds: Map<String, List<String>>) {
    functionsWithIds
            .forEach { (hash, signatures) ->
                printCollision(signatures, hash)
            }
}

private fun printCollision(signatures: List<String>, hash: String) {
    println("Collision detected!")
    println("Following functions have the same evm identifier:")
    signatures.forEach {
        println("$it => $hash")
    }
}

class FunctionIdentifierCollector : SolidityBaseListener() {

    val functions = mutableListOf<Function>()

    override fun enterFunctionDefinition(ctx: SolidityParser.FunctionDefinitionContext) {
        val functionName = ctx.identifier().Identifier().text
        val parameters = ctx.parameterList().parameter().joinToString(",") { it.typeName().text }
        val signature = "$functionName($parameters)"
        functions.add(signature to Hash.sha3String(signature).drop(2).take(8))
    }
}

typealias Function = Pair<String, String>

val Function.signature: String
    get() = this.first

val Function.hash: String
    get() = this.second
