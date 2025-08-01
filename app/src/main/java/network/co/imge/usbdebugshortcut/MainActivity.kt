package network.co.imge.usbdebugshortcut

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import network.co.imge.usbdebugshortcut.ui.theme.UsbDebugShortcutTheme
import network.co.imge.usbdebugshortcut.utils.CommonTools

class MainActivity : ComponentActivity() {
    private lateinit var viewModel: UsbDebugViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        viewModel = ViewModelProvider(this)[UsbDebugViewModel::class.java]
        setContent {
            UsbDebugShortcutTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainScreen(viewModel, modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.refresh(applicationContext)
    }

    @Composable
    fun MainScreen(viewModel: UsbDebugViewModel, modifier: Modifier = Modifier) {
        val context = LocalContext.current
        val showDisclaimer by viewModel.showDisclaimer.collectAsState()
        val disclaimerAgreed by viewModel.disclaimerAgreed.collectAsState()
        val isUsbDebugEnabled by viewModel.isUsbDebugEnabled.collectAsState()

        LaunchedEffect(context.applicationContext) {
            viewModel.initPrefs(context)
        }

        if (showDisclaimer) {
            AlertDialog(
                onDismissRequest = {},
                title = { Text("免責聲明") },
                text = {
                    Text("本應用需要 root 權限。使用 root 功能可能導致系統不穩定、資料遺失或失去保固。請確認您已了解風險並同意本免責聲明後再使用本應用。")
                },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.agreeDisclaimer()
//                        viewModel.refresh(context)
                    }) {
                        Text("同意")
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
//                        viewModel.rejectDisclaimer()
                        (context as? ComponentActivity)?.finishAffinity()
                    }) {
                        Text("不同意")
                    }
                }
            )
        }

        if (disclaimerAgreed) {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(32.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Text("USB 偵錯: ${if (isUsbDebugEnabled) "開啟" else "關閉"}")
                Button(onClick = {
                    CommonTools.openDeveloperSettings(context)
                }) {
                    Text("開啟開發人員選項")
                }
            }
        }
    }
}