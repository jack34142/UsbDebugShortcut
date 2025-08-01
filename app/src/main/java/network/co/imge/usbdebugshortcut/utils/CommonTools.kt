package network.co.imge.usbdebugshortcut.utils

import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.widget.Toast
import java.io.DataOutputStream

object CommonTools {
    fun isUsbDebugEnabled(context: Context): Boolean {
        return Settings.Global.getInt(context.contentResolver, Settings.Global.ADB_ENABLED, 0) == 1
    }

    fun hasRootAccess(): Boolean {
        return try {
            val process = Runtime.getRuntime().exec("su")
            val output = DataOutputStream(process.outputStream)
            output.writeBytes("exit\n")
            output.flush()
            val exitCode = process.waitFor()
            output.close()
            process.destroy()
            exitCode == 0
        } catch (e: Exception) {
            false
        }
    }

    fun toggleUsbDebugWithRoot(context: Context) {
        val enable = if (isUsbDebugEnabled(context)) 0 else 1
        try {
            val process = Runtime.getRuntime().exec("su")
            val output = DataOutputStream(process.outputStream)
            output.writeBytes("settings put global adb_enabled $enable\n")
            output.writeBytes("exit\n")
            output.flush()
            process.waitFor()
            output.close()
            process.destroy()

            // 你可以根據需要自己實作這個儲存狀態的邏輯
            // setLastUsbDebugState(context, enable == 1)

            Toast.makeText(
                context,
                "USB 偵錯已${if (enable == 1) "開啟" else "關閉"}",
                Toast.LENGTH_SHORT
            ).show()
        } catch (e: Exception) {
            Toast.makeText(context, "切換失敗：需要 root 權限", Toast.LENGTH_LONG).show()
        }
    }

    fun openDeveloperSettings(context: Context) {
        try {
            val intent = Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            Toast.makeText(context, "請手動切換 USB 偵錯模式", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(context, "無法開啟開發人員選項", Toast.LENGTH_LONG).show()
        }
    }
}