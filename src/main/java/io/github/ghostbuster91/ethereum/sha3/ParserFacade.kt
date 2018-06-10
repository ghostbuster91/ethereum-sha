package io.github.ghostbuster91.ethereum.sha3

import io.github.ghostbuster91.ethereum.sha3.parser.SolidityLexer
import io.github.ghostbuster91.ethereum.sha3.parser.SolidityParser
import org.antlr.v4.runtime.ANTLRInputStream
import org.antlr.v4.runtime.CommonTokenStream
import java.io.File
import java.nio.charset.Charset
import java.nio.file.Files

class ParserFacade {

    private fun readFile(file: File, encoding: Charset): String {
        val encoded = Files.readAllBytes(file.toPath())
        return String(encoded, encoding)
    }

    fun parse(file: File): SolidityParser.SourceUnitContext {
        val code = readFile(file, Charset.forName("UTF-8"))
        val lexer = SolidityLexer(ANTLRInputStream(code))
        val tokens = CommonTokenStream(lexer)
        val parser = SolidityParser(tokens)
        return parser.sourceUnit()
    }
}