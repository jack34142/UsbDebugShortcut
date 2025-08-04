package network.co.imge.usbdebugshortcut.ui.home

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.MutableStateFlow
import network.co.imge.usbdebugshortcut.R
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
            title = { Text(stringResource(R.string.disclaimer)) },
            text = {
                Column {
                    Text(stringResource(R.string.text_risk_root))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(stringResource(R.string.text_require_agreement))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(stringResource(R.string.text_nonroot_help))
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val isRoot = viewModel.agreeDisclaimer(true)
                    val resultText =
                        if (isRoot) context.getString(R.string.root_success)
                        else context.getString(R.string.root_failed)
                    Toast.makeText(context, context.getString(R.string.root_result, resultText), Toast.LENGTH_SHORT).show()
                }) {
                    Text(stringResource(R.string.agree))
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    viewModel.agreeDisclaimer(false)
                }) {
                    Text(stringResource(R.string.disagree))
                }
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            val status = if (isUsbDebugEnabled)
                stringResource(R.string.enabled)
            else
                stringResource(R.string.disabled)

            Text(stringResource(R.string.usb_debug_status, status))

            Button(onClick = {
                CommonTools.openDeveloperSettings(context)
            }) {
                Text(stringResource(R.string.open_developer_options))
            }
        }

        TextButton(onClick = {
            viewModel.showDisclaimer()
        }) {
            Text(stringResource(R.string.disclaimer))
        }
    }
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