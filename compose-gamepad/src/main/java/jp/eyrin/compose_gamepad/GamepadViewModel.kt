package jp.eyrin.compose_gamepad

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow

enum class GamepadAxis {
    LEFT_X, LEFT_Y, RIGHT_X, RIGHT_Y
}

enum class GamepadKey {
    UP, DOWN, LEFT, RIGHT, A, B, X, Y, L1, R1, L2, R2, L3, R3, START, SELECT
}

class GamepadState(
    private val keys: Int, private val keysPrev: Int, private val axes: FloatArray
) {
    fun getAxis(axis: GamepadAxis) = axes[axis.ordinal]
    fun getKey(key: GamepadKey) = (keys and (1 shl key.ordinal)) != 0
    fun getKeyDown(key: GamepadKey) = ((keys xor keysPrev) and keys and (1 shl key.ordinal)) != 0
    fun getKeyUp(key: GamepadKey) = ((keys xor keysPrev) and keysPrev and (1 shl key.ordinal)) != 0
}

class GamepadViewModel : ViewModel() {
    internal val axes = MutableStateFlow(floatArrayOf(0f, 0f, 0f, 0f))
    internal val keys = MutableStateFlow(0)
    internal val locked = MutableStateFlow(false)

    internal fun updateGamepadAxes(axes: FloatArray) {
        this.axes.value = axes
    }

    internal fun updateGamepadKey(key: GamepadKey, pressing: Boolean) {
        keys.value = if (pressing) {
            keys.value or (1 shl key.ordinal)
        } else {
            keys.value and (1 shl key.ordinal).inv()
        }
    }
}
