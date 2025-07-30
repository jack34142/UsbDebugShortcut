package network.co.imge.usbdebugshortcut

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import network.co.imge.usbdebugshortcut.ui.theme.UsbDebugShortcutTheme

class MainActivity : ComponentActivity() {
    private var uiReceiver: BroadcastReceiver? = null
    private lateinit var usbDebugViewModel: UsbDebugViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        usbDebugViewModel = ViewModelProvider(this)[UsbDebugViewModel::class.java]
        usbDebugViewModel.initPrefs(this)
        setContent {
            UsbDebugShortcutTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainScreen(usbDebugViewModel, modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // 註冊 UI 狀態同步廣播
        if (uiReceiver == null) {
            uiReceiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
                    if (intent?.action == CommonTools.ACTION_REFRESH_UI) {
                        usbDebugViewModel.refresh(this@MainActivity)
                    }
                }
            }
            val filter = IntentFilter(CommonTools.ACTION_REFRESH_UI)
            registerReceiver(uiReceiver, filter, Context.RECEIVER_NOT_EXPORTED)
        }
    }

    override fun onPause() {
        super.onPause()
        if (uiReceiver != null) {
            unregisterReceiver(uiReceiver)
            uiReceiver = null
        }
    }

    @Composable
    fun MainScreen(viewModel: UsbDebugViewModel, modifier: Modifier = Modifier) {
        val context = LocalContext.current
        val isUsbDebugEnabled by viewModel.isUsbDebugEnabled.collectAsState()
        var showDisclaimer by remember { mutableStateOf(false) }
        var disclaimerAgreed by remember { mutableStateOf(CommonTools.isDisclaimerAgreed(context)) }

        // 只要 disclaimerAgreed 變動就刷新狀態
        LaunchedEffect(disclaimerAgreed) {
            disclaimerAgreed = CommonTools.isDisclaimerAgreed(context)
            if (!disclaimerAgreed) showDisclaimer = true
            viewModel.refresh(context)
        }

        if (showDisclaimer && !disclaimerAgreed) {
            AlertDialog(
                onDismissRequest = {},
                title = { Text("免責聲明") },
                text = {
                    Text("本應用提供的「USB 偵錯模式」一鍵開關功能需要取得裝置的 root 權限。使用 root 功能可能會導致系統不穩定、資料遺失、裝置損壞或失去保固。請自行承擔所有風險，開發者不對任何因使用本功能造成的損失負責。請確認您已充分了解風險，並同意本免責聲明後再啟用 root 相關功能。")
                },
                confirmButton = {
                    TextButton(onClick = {
                        CommonTools.setDisclaimerAgreed(context, true)
                        disclaimerAgreed = true
                        showDisclaimer = false
                    }) {
                        Text("同意")
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showDisclaimer = false
                    }) {
                        Text("不同意")
                    }
                }
            )
        }

        Column(
            modifier = modifier.fillMaxSize().padding(32.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                Text("USB 偵錯模式")
                Switch(
                    checked = isUsbDebugEnabled,
                    onCheckedChange = {
                        if (disclaimerAgreed) {
                            viewModel.toggle(context)
                        } else {
                            showDisclaimer = true
                        }
                    },
                    enabled = disclaimerAgreed,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
            Button(onClick = {
                CommonTools.openDeveloperSettings(context)
            }) {
                Text("開啟開發人員選項")
            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun MainScreenPreview() {
        val fakeViewModel = UsbDebugViewModel()
        UsbDebugShortcutTheme {
            MainScreen(fakeViewModel)
        }
    }
}