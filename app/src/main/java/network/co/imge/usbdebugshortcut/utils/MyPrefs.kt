package network.co.imge.usbdebugshortcut

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class MyPrefs(context: Context) {

    companion object {
        private const val PREFS_NAME = "usbdebug_prefs"
        private const val KEY_DISCLAIMER_AGREED = "disclaimer_agreed"
    }

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun setDisclaimerAgreed(value: Boolean) {
        prefs.edit { putBoolean(KEY_DISCLAIMER_AGREED, value) }
    }

    fun isDisclaimerAgreed(): Boolean {
        return prefs.getBoolean(KEY_DISCLAIMER_AGREED, false)
    }
}