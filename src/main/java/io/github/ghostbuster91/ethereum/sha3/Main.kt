package io.github.ghostbuster91.ethereum.sha3

import com.xenomachina.argparser.ArgParser
import io.github.ghostbuster91.ethereum.sha3.parser.SolidityBaseListener
import io.github.ghostbuster91.ethereum.sha3.parser.SolidityParser
import org.antlr.v4.runtime.tree.ParseTreeWalker
import org.web3j.crypto.Hash
import java.io.File

class ParsedArgs(parser: ArgParser) {
    val input by parser.positional("input file")
}

fun main(args: Array<String>) {
    ArgParser(args)
            .parseInto(::ParsedArgs)
            .run {
                val parserFacade = ParserFacade()
                val sourceUnitContext = parserFacade.parse(File(input))
                ParseTreeWalker.DEFAULT.walk(FunctionIdentifierCalculatorListener(), sourceUnitContext)
            }
}

class FunctionIdentifierCalculatorListener : SolidityBaseListener() {

    override fun enterFunctionDefinition(ctx: SolidityParser.FunctionDefinitionContext) {
        val functionName = ctx.identifier().Identifier().text
        val parameters = ctx.parameterList().parameter().joinToString(",") { it.typeName().text }
        val message = "$functionName($parameters)"
        println("$message ${Hash.sha3String(message).drop(2).take(8)}")
    }
}