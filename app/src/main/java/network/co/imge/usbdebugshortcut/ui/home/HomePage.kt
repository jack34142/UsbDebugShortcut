package network.co.imge.usbdebugshortcut.ui.home

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import network.co.imge.usbdebugshortcut.utils.CommonTools

@Composable
fun HomePage(modifier: Modifier = Modifier) {
    val viewModel: HomeViewModel = viewModel()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val showDisclaimer by viewModel.showDisclaimer.collectAsState()
    val disclaimerAgreed by viewModel.disclaimerAgreed.collectAsState()
    val isUsbDebugEnabled by viewModel.isUsbDebugEnabled.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.init(context)
    }
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    viewModel.onResume(context)
                }
                Lifecycle.Event.ON_DESTROY -> {
                    viewModel.onDestroy()
                }
                else -> {}
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
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
                }) {
                    Text("同意")
                }
            },
            dismissButton = {
                TextButton(onClick = {
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