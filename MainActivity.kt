package com.agung.settings

import android.app.Activity
import android.os.Bundle
import android.os.Build
import android.content.pm.PackageManager
import android.graphics.Color
import android.view.Gravity
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import rikka.shizuku.Shizuku
import java.io.BufferedReader
import java.io.InputStreamReader

class MainActivity : Activity() {
    private lateinit var logView: TextView
    private lateinit var infoView: TextView
    private val requestCode = 4102
    private val blue = Color.rgb(47, 170, 255)
    private val panel = Color.rgb(22, 39, 91)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = Color.rgb(5, 8, 24)
        window.navigationBarColor = Color.rgb(5, 8, 24)
        buildUi()
        refreshDeviceInfo()
        Shizuku.addRequestPermissionResultListener(permissionListener)
    }

    override fun onDestroy() {
        super.onDestroy()
        Shizuku.removeRequestPermissionResultListener(permissionListener)
    }

    private val permissionListener = Shizuku.OnRequestPermissionResultListener { code, result ->
        if (code == requestCode) {
            if (result == PackageManager.PERMISSION_GRANTED) runOptimization()
            else log("SHIZUKU: permission denied; no changes applied.")
        }
    }

    private fun dp(v: Int) = (v * resources.displayMetrics.density).toInt()
    private fun lp() = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

    private fun buildUi() {
        val scroll = ScrollView(this)
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(18), dp(22), dp(18), dp(24))
            setBackgroundColor(Color.rgb(8, 13, 43))
        }
        scroll.addView(root)
        setContentView(scroll)

        root.addView(TextView(this).apply {
            text = "AGUNG SETTINGS\nOPTIMIZER  v1.0.0"
            textSize = 22f
            setTextColor(Color.WHITE)
            setPadding(dp(14), dp(16), dp(14), dp(16))
            setBackgroundColor(Color.rgb(26, 59, 139))
        }, lp())

        heading(root, "▣  DEVICE IDENTITY")
        infoView = TextView(this).apply {
            textSize = 14f
            setTextColor(Color.WHITE)
            setPadding(dp(14), dp(14), dp(14), dp(14))
            setBackgroundColor(panel)
        }
        root.addView(infoView, lp())

        heading(root, ">_  OPTIMIZER / EXEC")
        root.addView(TextView(this).apply {
            text = "Detect device capability and request supported system settings. DPI and screen resolution are not changed."
            textSize = 14f
            setTextColor(Color.LTGRAY)
            setPadding(dp(14), dp(14), dp(14), dp(14))
            setBackgroundColor(panel)
        }, lp())

        val run = Button(this).apply {
            text = "⏻  EXEC OPTIMIZE"
            textSize = 16f
            setTextColor(Color.WHITE)
            backgroundTintList = android.content.res.ColorStateList.valueOf(blue)
            setOnClickListener { requestShizukuAndRun() }
        }
        root.addView(run, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(58)).apply { topMargin = dp(12) })

        heading(root, ">_  EXECUTION LOG")
        logView = TextView(this).apply {
            text = "[READY] AGUNG SETTINGS initialized."
            textSize = 12f
            setTextColor(Color.rgb(117, 220, 255))
            setPadding(dp(14), dp(14), dp(14), dp(14))
            setBackgroundColor(Color.rgb(4, 8, 28))
        }
        root.addView(logView, lp())
        root.addView(TextView(this).apply {
            text = "\nDEVELOPER: AGUNG  •  SHIZUKU REQUIRED"
            gravity = Gravity.CENTER
            textSize = 11f
            setTextColor(Color.GRAY)
        }, lp())
    }

    private fun heading(root: LinearLayout, title: String) {
        root.addView(TextView(this).apply {
            text = title
            textSize = 16f
            setTextColor(Color.WHITE)
            setPadding(dp(2), dp(22), dp(2), dp(10))
        }, lp())
    }

    private fun maxRefreshRate(): Float {
        val wm = getSystemService(WINDOW_SERVICE) as WindowManager
        return try {
            wm.defaultDisplay.supportedModes.maxOfOrNull { it.refreshRate } ?: wm.defaultDisplay.refreshRate
        } catch (_: Exception) { 0f }
    }

    private fun refreshDeviceInfo() {
        val cores = Runtime.getRuntime().availableProcessors()
        val ramMb = (getSystemService(ACTIVITY_SERVICE) as android.app.ActivityManager).memoryInfo.totalMem / (1024 * 1024)
        infoView.text = """
            Android       : ${Build.VERSION.RELEASE}
            SDK Level     : ${Build.VERSION.SDK_INT}
            Architecture  : ${Build.SUPPORTED_ABIS.firstOrNull() ?: "Unknown"}
            CPU Cores     : $cores
            Total RAM     : ${ramMb / 1024} GB
            Max Display   : ${"%.1f".format(maxRefreshRate())} Hz
            Privilege     : ${if (Shizuku.pingBinder()) "Shizuku available" else "User / no Shizuku"}
            Touch 240 Hz  : Hardware-dependent; no universal API
        """.trimIndent()
    }

    private fun requestShizukuAndRun() {
        if (!Shizuku.pingBinder()) {
            log("[ERROR] Shizuku is not running. Start Shizuku first.")
            return
        }
        if (Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED) runOptimization()
        else Shizuku.requestPermission(requestCode)
    }

    private fun runOptimization() {
        log("[START] AGUNG SETTINGS OPTIMIZER")
        log("[DETECT] Maximum display refresh: ${"%.1f".format(maxRefreshRate())} Hz")
        log("[NOTE] Display Hz does not guarantee in-game FPS.")
        log("[INFO] 240 Hz touch sampling depends on touch hardware/vendor support.")
        log("[INFO] Fast click and drag behavior cannot be universally forced by Android.")
        log("[INFO] Game Driver is vendor/Android-dependent; not forced.")
        val commands = listOf(
            listOf("settings", "put", "global", "window_animation_scale", "0.5"),
            listOf("settings", "put", "global", "transition_animation_scale", "0.5"),
            listOf("settings", "put", "global", "animator_duration_scale", "0.5")
        )
        var success = 0
        for (cmd in commands) {
            val result = runShizuku(cmd)
            if (result.first) success++ else log("[WARN] ${cmd.last()}: ${result.second}")
        }
        log("[APPLY] Animation scale 0.5x: $success/3")
        log("[FPS] 60 FPS lock cannot be universally enforced for another app.")
        log("[DONE] Supported system settings requested.")
    }

    private fun runShizuku(command: List<String>): Pair<Boolean, String> {
        return try {
            val process = Shizuku.newProcess(command.toTypedArray(), null, null)
            val output = BufferedReader(InputStreamReader(process.inputStream)).readText()
            val error = BufferedReader(InputStreamReader(process.errorStream)).readText()
            val code = process.waitFor()
            Pair(code == 0, (error.ifBlank { output }).take(160))
        } catch (e: Exception) {
            Pair(false, e.message ?: "execution failed")
        }
    }

    private fun log(message: String) {
        runOnUiThread { logView.append("\n$message") }
    }
}
