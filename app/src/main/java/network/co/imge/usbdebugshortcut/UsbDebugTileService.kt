package network.co.imge.usbdebugshortcut

import android.content.Intent
import android.graphics.drawable.Icon
import android.provider.Settings
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import android.widget.Toast
import java.io.DataOutputStream

class UsbDebugTileService : TileService() {

    override fun onStartListening() {
        super.onStartListening()
        updateTileState()
    }

    override fun onClick() {
        if (hasRootAccess()) {
            collapseStatusBarWithShell()
            toggleUsbDebugWithRoot()
        } else {
            openDeveloperSettings()
        }
        updateTileState()
    }

    private fun collapseStatusBarWithShell() {
        try {
            val process = Runtime.getRuntime().exec(arrayOf("su", "-c", "input keyevent 3")) // KEYCODE_HOME
            process.waitFor()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun updateTileState() {
        val tile = qsTile ?: return
        tile.state = if (isUsbDebugEnabled()) {
            Tile.STATE_ACTIVE
        } else {
            Tile.STATE_INACTIVE
        }
        tile.icon = Icon.createWithResource(this, R.drawable.usb_debug_shortcut)
        tile.updateTile()
    }

    private fun isUsbDebugEnabled(): Boolean {
        return Settings.Global.getInt(contentResolver, Settings.Global.ADB_ENABLED, 0) == 1
    }

    private fun toggleUsbDebugWithRoot() {
        val enable = if (isUsbDebugEnabled()) 0 else 1
        try {
            val process = Runtime.getRuntime().exec("su")
            val output = DataOutputStream(process.outputStream)
            output.writeBytes("settings put global adb_enabled $enable\n")
            output.writeBytes("exit\n")
            output.flush()
            process.waitFor()
            Toast.makeText(applicationContext, "USB Debug 已 ${if (enable == 1) "開啟" else "關閉"}", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(applicationContext, "切換失敗：需要 root 權限", Toast.LENGTH_LONG).show()
        }
    }

    private fun openDeveloperSettings() {
        try {
            val intent = Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            startActivity(intent)
            Toast.makeText(applicationContext, "請手動切換 USB 偵錯模式", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(applicationContext, "無法開啟開發人員選項", Toast.LENGTH_LONG).show()
        }
    }

    private fun hasRootAccess(): Boolean {
        return try {
            val process = Runtime.getRuntime().exec("su")
            process.outputStream.write("exit\n".toByteArray())
            process.outputStream.flush()
            process.waitFor() == 0
        } catch (e: Exception) {
            false
        }
    }
}

