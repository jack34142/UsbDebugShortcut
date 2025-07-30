package network.co.imge.usbdebugshortcut

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UsbDebugViewModel : ViewModel() {
    private val _isUsbDebugEnabled = MutableStateFlow(false)
    val isUsbDebugEnabled: StateFlow<Boolean> = _isUsbDebugEnabled
    private var prefs: SharedPreferences? = null
    private var listener: SharedPreferences.OnSharedPreferenceChangeListener? = null

    fun initPrefs(context: Context) {
        if (prefs == null) {
            prefs = context.getSharedPreferences(CommonTools.PREFS_NAME, Context.MODE_PRIVATE)
            listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
                if (key == CommonTools.KEY_LAST_USBDEBUG_STATE) {
                    _isUsbDebugEnabled.value = CommonTools.getLastUsbDebugState(context)
                }
            }
            prefs?.registerOnSharedPreferenceChangeListener(listener)
        }
    }

    fun refresh(context: Context) {
        _isUsbDebugEnabled.value = CommonTools.isUsbDebugEnabled(context)
    }

    fun toggle(context: Context) {
        viewModelScope.launch {
            if (CommonTools.hasRootAccess()) {
                CommonTools.toggleUsbDebugWithRoot(context)
                val enabled = CommonTools.isUsbDebugEnabled(context)
                CommonTools.setLastUsbDebugState(context, enabled)
                _isUsbDebugEnabled.value = enabled
                val intent = android.content.Intent(CommonTools.ACTION_REFRESH_TILE)
                context.sendBroadcast(intent)
            } else {
                CommonTools.openDeveloperSettings(context)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        prefs?.unregisterOnSharedPreferenceChangeListener(listener)
    }
}