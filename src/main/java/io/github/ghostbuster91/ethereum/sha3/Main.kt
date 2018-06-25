package io.github.ghostbuster91.ethereum.sha3

import com.xenomachina.argparser.ArgParser
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

fun findCollisions(code: String): Set<ContractFunction> {
    val parserFacade = ParserFacade()
    val sourceUnitContext = parserFacade.parse(code)
    val contracts = sourceUnitContext.contractDefinition()
    val functions = contracts
            .flatMap { contract ->
                extractAllFunctions(contract, contracts)
            }
    val hashesToFunctions = functions.groupBy { it.function.identifier + it.contractName }
    return hashesToFunctions.filter { it.value.size > 1 }.flatMap { it.value }.toSet()
}

private fun extractAllFunctions(contract: SolidityParser.ContractDefinitionContext, contracts: List<SolidityParser.ContractDefinitionContext>): List<ContractFunction> {
    val inheritedContracts = inheritedContracts(contract, contracts)
    val functions = inheritedContracts.flatMap(::extractContractFunctions) + extractContractFunctions(contract)
    return assignedFunctions(functions, contract)
}

private fun assignedFunctions(functions: List<Function>, contract: SolidityParser.ContractDefinitionContext) =
        functions.map { ContractFunction(contract.identifier().text, it) }

private fun inheritedContracts(contract: SolidityParser.ContractDefinitionContext, contracts: List<SolidityParser.ContractDefinitionContext>): List<SolidityParser.ContractDefinitionContext> {
    return contract.inheritanceSpecifier()
            .flatMap { inheritanceSpecifier ->
                findContract(contracts, inheritanceSpecifier.userDefinedTypeName())
            }
}

private fun findContract(contracts: List<SolidityParser.ContractDefinitionContext>, userDefinedTypeNameContext: SolidityParser.UserDefinedTypeNameContext): List<SolidityParser.ContractDefinitionContext> {
    return userDefinedTypeNameContext.identifier()
            .map { derivedContract ->
                derivedContract.Identifier().text
            }.map { derivedContractName ->
                contracts.first { it.identifier().Identifier().text == derivedContractName }
            }
}

private fun extractContractFunctions(contract: SolidityParser.ContractDefinitionContext): List<Function> {
    return contract.contractPart().map {
        createFunction(it.functionDefinition())
    }
}

private fun printCollisions(functions: Set<ContractFunction>) {
    functions.groupBy { it.contractName }
            .mapValues { it.value.map { it.function } }
            .forEach { (contract, functions)->
                println("Collision detected!")
                println("For contract: $contract")
                println("Following functions have the same evm identifier:")
                functions
                        .forEach { it ->
                            println("${it.signature} => ${it.identifier}")
                        }
                println()
            }

}

data class Function(val signature: String, val identifier: String)

private fun createFunction(ctx: SolidityParser.FunctionDefinitionContext): Function {
    val functionName = ctx.identifier().Identifier().text
    val parameters = ctx.parameterList().parameter().joinToString(",") { it.typeName().text }
    val signature = "$functionName($parameters)"
    val identifier = Hash.sha3String(signature).drop(2).take(8)
    return Function(signature, identifier)
}

data class ContractFunction(val contractName: String, val function: Function)