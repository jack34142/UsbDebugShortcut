package network.co.imge.usbdebugshortcut

import android.content.Context
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import network.co.imge.usbdebugshortcut.utils.CommonTools

class UsbDebugViewModel : ViewModel() {
    private val _isUsbDebugEnabled = MutableStateFlow(false)
    val isUsbDebugEnabled: StateFlow<Boolean> = _isUsbDebugEnabled

    private val _disclaimerAgreed = MutableStateFlow(false)
    val disclaimerAgreed: StateFlow<Boolean> = _disclaimerAgreed

    private val _showDisclaimer = MutableStateFlow(false)
    val showDisclaimer: StateFlow<Boolean> = _showDisclaimer

    private var prefs: MyPrefs? = null

    fun initPrefs(context: Context) {
        if (prefs == null) {
            prefs = MyPrefs(context)
            val agreed = prefs!!.isDisclaimerAgreed()
            _disclaimerAgreed.value = agreed
            _showDisclaimer.value = !agreed
        }
    }

    fun agreeDisclaimer() {
        _disclaimerAgreed.value = true
        _showDisclaimer.value = false
        prefs?.setDisclaimerAgreed(true)
    }

//    fun rejectDisclaimer() {
//        _showDisclaimer.value = false
//    }

    fun refresh(context: Context) {
        _isUsbDebugEnabled.value = CommonTools.isUsbDebugEnabled(context)
    }
}