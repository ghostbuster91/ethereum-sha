package io.github.ghostbuster91.ethereum.sha3

import io.github.ghostbuster91.ethereum.sha3.parser.SolidityLexer
import io.github.ghostbuster91.ethereum.sha3.parser.SolidityParser
import org.antlr.v4.runtime.ANTLRInputStream
import org.antlr.v4.runtime.CommonTokenStream

class ParserFacade {

    fun parse(code: String): SolidityParser.SourceUnitContext {
        val lexer = SolidityLexer(ANTLRInputStream(code))
        val tokens = CommonTokenStream(lexer)
        val parser = SolidityParser(tokens)
        return parser.sourceUnit()
    }
}