package network.co.imge.usbdebugshortcut

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class MyPrefs(context: Context) {

    companion object {
        private const val PREFS_NAME = "usbdebug_prefs"
        const val KEY_DISCLAIMER_AGREED = "disclaimer_agreed"
        const val KEY_USB_DEBUG_ENABLED = "usb_debug_enabled"
    }

    private val _prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    val prefs: SharedPreferences = _prefs

    fun setDisclaimerAgreed(value: Boolean) {
        prefs.edit { putBoolean(KEY_DISCLAIMER_AGREED, value) }
    }

    fun isDisclaimerAgreed(): Boolean {
        return prefs.getBoolean(KEY_DISCLAIMER_AGREED, false)
    }

    fun setUsbDebugEnabled(value: Boolean) {
        prefs.edit { putBoolean(KEY_USB_DEBUG_ENABLED, value) }
    }
}