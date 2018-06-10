package io.github.ghostbuster91.ethereum.sha3

import io.github.ghostbuster91.ethereum.sha3.parser.SolidityParser.ruleNames
import org.antlr.v4.runtime.RuleContext

class AstPrinter {

    fun print(ctx: RuleContext) {
        explore(ctx, 0)
    }

    private fun explore(ctx: RuleContext, indentation: Int) {
        val ruleName = ruleNames[ctx.ruleIndex]
        for (i in 0 until indentation) {
            print("  ")
        }
        println(ruleName)
        for (i in 0 until ctx.childCount) {
            val element = ctx.getChild(i)
            if (element is RuleContext) {
                explore(element, indentation + 1)
            }
        }
    }
}