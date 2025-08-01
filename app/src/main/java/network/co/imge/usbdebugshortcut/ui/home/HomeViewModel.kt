package network.co.imge.usbdebugshortcut.ui.home

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import network.co.imge.usbdebugshortcut.MyPrefs
import network.co.imge.usbdebugshortcut.utils.CommonTools

class HomeViewModel : ViewModel() {
    private val _isUsbDebugEnabled = MutableStateFlow(false)
    val isUsbDebugEnabled: StateFlow<Boolean> = _isUsbDebugEnabled

    private val _disclaimerAgreed = MutableStateFlow(false)
    val disclaimerAgreed: StateFlow<Boolean> = _disclaimerAgreed

    private val _showDisclaimer = MutableStateFlow(false)
    val showDisclaimer: StateFlow<Boolean> = _showDisclaimer

    private var myPrefs: MyPrefs? = null

    fun init(context: Context) {
        if (myPrefs == null) {
            myPrefs = MyPrefs(context)
            val agreed = myPrefs!!.isDisclaimerAgreed()
            _disclaimerAgreed.value = agreed
            _showDisclaimer.value = !agreed

            myPrefs!!.prefs.registerOnSharedPreferenceChangeListener(preferenceChangeListener)
        }
    }

    fun onResume(context: Context) {
        _isUsbDebugEnabled.value = CommonTools.isUsbDebugEnabled(context)
    }

    fun onDestroy(){
        myPrefs?.prefs?.unregisterOnSharedPreferenceChangeListener(preferenceChangeListener)
    }

    fun agreeDisclaimer() {
        _disclaimerAgreed.value = true
        _showDisclaimer.value = false
        myPrefs?.setDisclaimerAgreed(true)
    }

    private val preferenceChangeListener = SharedPreferences.OnSharedPreferenceChangeListener { prefs, key ->
        if (key == MyPrefs.Companion.KEY_USB_DEBUG_ENABLED) {
            _isUsbDebugEnabled.value = prefs.getBoolean(key, false)
        }
    }
}