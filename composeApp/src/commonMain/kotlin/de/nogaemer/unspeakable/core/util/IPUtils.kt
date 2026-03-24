package de.nogaemer.unspeakable.core.util

const val ALPHABET = "0123456789ABCDEFGHIJKLMN"
const val BASE = 24

fun ipToInt(ip: String): Long {
    val (a, b, c, d) = ip.split(".").map { it.toLong() }
    return (a shl 24) or (b shl 16) or (c shl 8) or d
}

fun encodeBase24(n: Long): String {
    if (n == 0L) return ALPHABET[0].toString()
    var num = n
    val result = StringBuilder()
    while (num > 0) {
        result.append(ALPHABET[(num % BASE).toInt()])
        num /= BASE
    }
    return result.reverse().toString()
}

fun ipToCode(ip: String) = encodeBase24(ipToInt(ip))

fun decodeBase24(code: String): Long {
    var result = 0L
    for (ch in code) {
        result = result * BASE + ALPHABET.indexOf(ch)
    }
    return result
}

fun intToIp(n: Long): String {
    return "${(n shr 24) and 0xFF}.${(n shr 16) and 0xFF}" +
            ".${(n shr 8) and 0xFF}.${n and 0xFF}"
}

fun codeToIp(code: String) = intToIp(decodeBase24(code))

fun isValidCode(code: String) = code.all { it in ALPHABET } && code.length <= 7 && isValidIp(codeToIp(code))

fun isValidIp(ip: String) = ip.split(".").all { it.toIntOrNull() in 0..255 }


fun getRandomTestIp(): String {
    return (0..3).map { (0..255).random() }.joinToString(".")
}
