package de.nogaemer.unspeakable.core.util

object FirewallSetup {

    private const val ruleName = "Unspeakable Game"

    fun ensurePort(port: Int) {
        if (!isWindows()) return
        if (ruleExists(port)) return
        addRule(port)
    }

    private fun isWindows() =
        System.getProperty("os.name").lowercase().contains("windows")

    private fun ruleExists(port: Int): Boolean {
        val result = ProcessBuilder(
            "netsh", "advfirewall", "firewall", "show", "rule",
            "name=$ruleName"
        ).start().inputStream.bufferedReader().readText()
        return result.contains(port.toString())
    }

    private fun addRule(port: Int) {
        // Write a temp .bat — avoids all PowerShell quote-escaping issues
        val bat = java.io.File.createTempFile("unspeakable_fw_", ".bat")
        bat.deleteOnExit()
        bat.writeText(
            "@echo off\r\n" +
                    "netsh advfirewall firewall add rule " +
                    "name=\"$ruleName\" " +
                    "dir=in action=allow protocol=TCP " +
                    "localport=$port profile=private,domain,public\r\n"
        )

        ProcessBuilder(
            "powershell", "-Command",
            "Start-Process '${bat.absolutePath}' -Verb RunAs -Wait"
        ).inheritIO().start().waitFor()

        bat.delete()
    }
}