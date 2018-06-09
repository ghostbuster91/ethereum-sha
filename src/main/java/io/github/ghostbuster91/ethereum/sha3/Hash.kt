package io.github.ghostbuster91.ethereum.sha3

import org.bouncycastle.jcajce.provider.digest.Keccak
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import kotlin.experimental.and


/**
 * Keccak-256 hash function.
 *
 * @param hexInput hex encoded input data with optional 0x prefix
 * @return hash value as hex encoded string
 */
fun sha3(hexInput: String): String {
    val bytes = hexStringToByteArray(hexInput)
    val result = sha3(bytes)
    return toHexString(result)
}

/**
 * Keccak-256 hash function.
 *
 * @param input binary encoded input data
 * @param offset of start of data
 * @param length of data
 * @return hash value
 */
@JvmOverloads
fun sha3(input: ByteArray, offset: Int = 0, length: Int = input.size): ByteArray {
    val kecc = Keccak.Digest256()
    kecc.update(input, offset, length)
    return kecc.digest()
}

/**
 * Keccak-256 hash function that operates on a UTF-8 encoded String.
 *
 * @param utf8String UTF-8 encoded string
 * @return hash value as hex encoded string
 */
fun sha3String(utf8String: String): String {
    return toHexString(sha3(utf8String.toByteArray(StandardCharsets.UTF_8)))
}

/**
 * Generates SHA-256 digest for the given `input`.
 *
 * @param input The input to digest
 * @return The hash value for the given input
 * @throws RuntimeException If we couldn't find any SHA-256 provider
 */
fun sha256(input: ByteArray): ByteArray {
    try {
        val digest = MessageDigest.getInstance("SHA-256")
        return digest.digest(input)
    } catch (e: NoSuchAlgorithmException) {
        throw RuntimeException("Couldn't find a SHA-256 provider", e)
    }

}

fun toHexString(input: ByteArray, offset: Int, length: Int, withPrefix: Boolean): String {
    val stringBuilder = StringBuilder()
    if (withPrefix) {
        stringBuilder.append("0x")
    }
    for (i in offset until offset + length) {
        stringBuilder.append(String.format("%02x", input[i] and 0xFF.toByte()))
    }

    return stringBuilder.toString()
}

fun toHexString(input: ByteArray): String {
    return toHexString(input, 0, input.size, true)
}