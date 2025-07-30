package network.co.imge.usbdebugshortcut

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.provider.Settings
import android.widget.Toast
import java.io.DataOutputStream

object CommonTools {
    const val ACTION_REFRESH_TILE = "network.co.imge.usbdebugshortcut.REFRESH_TILE"
    const val ACTION_REFRESH_UI = "network.co.imge.usbdebugshortcut.REFRESH_UI"
    const val PREFS_NAME = "usbdebug_prefs"
    private const val KEY_DISCLAIMER_AGREED = "disclaimer_agreed"
    const val KEY_LAST_USBDEBUG_STATE = "usbdebug_last_state"

    fun isUsbDebugEnabled(context: Context): Boolean {
        return Settings.Global.getInt(context.contentResolver, Settings.Global.ADB_ENABLED, 0) == 1
    }

    fun hasRootAccess(): Boolean {
        return try {
            val process = Runtime.getRuntime().exec("su")
            process.outputStream.write("exit\n".toByteArray())
            process.outputStream.flush()
            process.waitFor() == 0
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
            setLastUsbDebugState(context, enable == 1)
            Toast.makeText(context, "USB Debug 已 ${if (enable == 1) "開啟" else "關閉"}", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(context, "切換失敗：需要 root 權限", Toast.LENGTH_LONG).show()
        }
    }

    fun setLastUsbDebugState(context: Context, enabled: Boolean) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_LAST_USBDEBUG_STATE, enabled).apply()
    }

    fun getLastUsbDebugState(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_LAST_USBDEBUG_STATE, false)
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

    fun isDisclaimerAgreed(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_DISCLAIMER_AGREED, false)
    }

    fun setDisclaimerAgreed(context: Context, agreed: Boolean) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_DISCLAIMER_AGREED, agreed).apply()
    }
}