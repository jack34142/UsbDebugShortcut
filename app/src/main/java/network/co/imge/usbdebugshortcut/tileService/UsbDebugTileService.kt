package network.co.imge.usbdebugshortcut.tileService

import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import network.co.imge.usbdebugshortcut.MyPrefs
import network.co.imge.usbdebugshortcut.utils.CommonTools

class UsbDebugTileService : TileService() {
    override fun onStartListening() {
        super.onStartListening()
        updateTileState()
    }

    override fun onClick() {
        val myPrefs = MyPrefs(this)
        if (myPrefs.isDisclaimerAgreed() && CommonTools.hasRootAccess()) {
            collapseStatusBarWithShell()
            CommonTools.toggleUsbDebugWithRoot(this)
            updateTileState()
        } else {
            CommonTools.openDeveloperSettings(this)
        }
    }

    private fun collapseStatusBarWithShell() {  //必須root才能收起通知欄
        try {
            val process = Runtime.getRuntime().exec(arrayOf("su", "-c", "service call statusbar 2"))
            process.waitFor()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun updateTileState() {
        val tile = qsTile ?: return
        tile.state = if (CommonTools.isUsbDebugEnabled(this)) {
            Tile.STATE_ACTIVE
        } else {
            Tile.STATE_INACTIVE
        }
//        tile.icon = Icon.createWithResource(this, R.drawable.shortcut_icon)
        tile.updateTile()
    }
}