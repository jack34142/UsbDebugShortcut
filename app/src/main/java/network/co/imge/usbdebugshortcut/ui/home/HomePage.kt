package network.co.imge.usbdebugshortcut.ui.home

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.MutableStateFlow
import network.co.imge.usbdebugshortcut.utils.CommonTools

@Composable
fun HomePage(modifier: Modifier = Modifier, viewModel: HomeViewModel = viewModel()) {
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
                Column {
                    Text("本應用需要使用 root 權限。使用 root 功能可能會導致系統不穩定、應用程式異常、資料遺失，甚至使裝置失去官方保固。請務必在充分了解這些風險後謹慎操作。")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("只有同意本免責聲明後，才能使用 root 權限來控制 USB 偵錯的開啟與關閉。")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("若不同意，則本應用只能協助您快速開啟開發人員選項，無法控制 USB 偵錯功能。")
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val isRoot = viewModel.agreeDisclaimer(true)
                    Toast.makeText(context, "取得root權限 ${if (isRoot) "成功" else "失敗"}", Toast.LENGTH_SHORT).show()
                }) {
                    Text("同意")
                }
            },
            dismissButton = {
                TextButton(onClick = {
//                    (context as? ComponentActivity)?.finishAffinity()
                    viewModel.agreeDisclaimer(false)
                }) {
                    Text("不同意")
                }
            }
        )
    }

//    if (disclaimerAgreed) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(32.dp),
//            verticalArrangement = Arrangement.Top  // 預設就好
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)           // 這裡讓上半部撐滿剩餘空間
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Text("USB 偵錯: ${if (isUsbDebugEnabled) "開啟" else "關閉"}")
                Button(onClick = {
                    CommonTools.openDeveloperSettings(context)
                }) {
                    Text("開啟開發人員選項")
                }
            }

            TextButton(onClick = {
                viewModel.showDisclaimer()
            }) {
                Text("查看免責聲明")
            }
        }
//    }
}

@Preview(showBackground = true)
@Composable
fun HomePagePreview() {
    val fakeViewModel = remember {
        object : HomeViewModel() {
            override val showDisclaimer = MutableStateFlow(true)
            override val disclaimerAgreed = MutableStateFlow(true)
            override val isUsbDebugEnabled = MutableStateFlow(true)
        }
    }
    HomePage(viewModel = fakeViewModel)
}