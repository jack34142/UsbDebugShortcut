package network.co.imge.usbdebugshortcut

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import android.graphics.drawable.Icon
import android.widget.Toast

class UsbDebugTileService : TileService() {
    private var tileReceiver: BroadcastReceiver? = null

    override fun onStartListening() {
        super.onStartListening()
        updateTileState()
        // 註冊廣播
        if (tileReceiver == null) {
            tileReceiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
                    if (intent?.action == CommonTools.ACTION_REFRESH_TILE) {
                        updateTileState()
                    }
                }
            }
            val filter = IntentFilter(CommonTools.ACTION_REFRESH_TILE)
            registerReceiver(tileReceiver, filter, Context.RECEIVER_NOT_EXPORTED)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (tileReceiver != null) {
            unregisterReceiver(tileReceiver)
            tileReceiver = null
        }
    }

    override fun onClick() {
        if (!CommonTools.isDisclaimerAgreed(applicationContext)) {
            Toast.makeText(applicationContext, "請先在主程式同意免責聲明後才能使用 root 相關功能", Toast.LENGTH_LONG).show()
            return
        }
        if (CommonTools.hasRootAccess()) {
            collapseStatusBarWithShell()
            CommonTools.toggleUsbDebugWithRoot(applicationContext)
            // tile 狀態同步
            val enabled = CommonTools.isUsbDebugEnabled(applicationContext)
            CommonTools.setLastUsbDebugState(applicationContext, enabled)
            sendBroadcast(Intent(CommonTools.ACTION_REFRESH_TILE))
            sendBroadcast(Intent(CommonTools.ACTION_REFRESH_UI))
        } else {
            CommonTools.openDeveloperSettings(applicationContext)
        }
        updateTileState()
    }

    private fun collapseStatusBarWithShell() {
        try {
            val process = Runtime.getRuntime().exec(arrayOf("su", "-c", "service call statusbar 2"))
            process.waitFor()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun updateTileState() {
        val tile = qsTile ?: return
        tile.state = if (CommonTools.isUsbDebugEnabled(applicationContext)) {
            Tile.STATE_ACTIVE
        } else {
            Tile.STATE_INACTIVE
        }
        tile.icon = Icon.createWithResource(this, R.drawable.shortcut_icon)
        tile.updateTile()
    }
}

