package jp.eyrin.compose_gamepad

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ComposeGamepad(locked: Boolean = false, handler: (GamepadState) -> Unit) {
    val viewModel: GamepadViewModel = viewModel()
    val coroutineScope = rememberCoroutineScope()

    DisposableEffect(Unit) {
        viewModel.locked.value = locked
        val job = coroutineScope.launch {
            var keysPrev = viewModel.keys.value
            while (true) {
                val keys = viewModel.keys.value
                handler(GamepadState(keys, keysPrev, viewModel.axes.value))
                keysPrev = keys
                delay(20)
            }
        }

        onDispose {
            job.cancel()
            viewModel.locked.value = false
        }
    }
}
