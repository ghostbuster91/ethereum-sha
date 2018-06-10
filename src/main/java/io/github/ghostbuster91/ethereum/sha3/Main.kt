package io.github.ghostbuster91.ethereum.sha3

import com.xenomachina.argparser.ArgParser
import org.antlr.v4.runtime.RuleContext
import java.io.File



class ParsedArgs(parser: ArgParser) {
    val input by parser.positional("input file")
}

fun main(args: Array<String>) {
    val parserFacade = ParserFacade()
    val astPrinter = AstPrinter()
    astPrinter.print(parserFacade.parse(File("examples/MyContract.sol")))
//    ArgParser(args)
//            .parseInto(::ParsedArgs)
//            .run {
//
//            }
}