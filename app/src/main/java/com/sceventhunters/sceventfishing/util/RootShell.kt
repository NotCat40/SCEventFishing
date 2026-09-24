package com.sceventhunters.sceventfishing.util

import java.io.BufferedReader
import java.io.InputStreamReader

object RootShell {
    private var process: Process? = null

    private fun start(): Boolean {
        val isAlive = try {
            process?.exitValue()
            false
        } catch (e: IllegalThreadStateException) {
            true
        }
        if (process != null && isAlive) return true
        return try { process = Runtime.getRuntime().exec("su"); true } catch (e: Exception) { process = null; false }
    }
    fun stop() {
        try {
            process?.outputStream?.let { it.write("exit\n".toByteArray()); it.flush() }
            process?.waitFor()
        } catch (e: Exception) {} finally { process?.destroy(); process = null }
    }
    private var cachedRoot: Boolean? = null

    fun isRootAvailable(): Boolean {
        cachedRoot?.let { return it }
        val available = start()
        cachedRoot = available
        return available
    }
    fun runCommand(command: String): List<String> {
        if (!start()) return emptyList()
        return try {
            val os = process!!.outputStream
            val is_ = process!!.inputStream
            val endMarker = "END_COMMAND_${System.currentTimeMillis()}"
            os.write(("$command\necho $endMarker\n").toByteArray())
            os.flush()
            val reader = BufferedReader(InputStreamReader(is_))
            val res = mutableListOf<String>()
            while (true) {
                val line = reader.readLine() ?: break
                if (line.trim() == endMarker) break
                res.add(line)
            }
            res
        } catch (e: Exception) { stop(); emptyList() }
    }
}

fun isRootAvailable(): Boolean = RootShell.isRootAvailable()
