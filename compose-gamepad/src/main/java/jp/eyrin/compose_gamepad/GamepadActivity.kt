package jp.eyrin.compose_gamepad

import android.view.InputDevice
import android.view.KeyEvent
import android.view.MotionEvent
import androidx.activity.ComponentActivity
import androidx.activity.viewModels

open class GamepadActivity : ComponentActivity() {
    private val viewModel: GamepadViewModel by viewModels()
    private var xKeyPrev: GamepadKey? = null
    private var yKeyPrev: GamepadKey? = null

    fun getAxes() =
        if (viewModel.locked.value) floatArrayOf(0f, 0f, 0f, 0f) else viewModel.axes.value
    fun getKeys() = if (viewModel.locked.value) 0 else viewModel.keys.value
    fun getAxis(axis: GamepadAxis) = getAxes()[axis.ordinal]
    fun getKey(key: GamepadKey) = (getKeys() and (1 shl key.ordinal)) != 0

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        val key = gamepadKeyFromKeyCode(keyCode)
        if (key != null) {
            viewModel.updateGamepadKey(key, true)
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        val key = gamepadKeyFromKeyCode(keyCode)
        if (key != null) {
            viewModel.updateGamepadKey(key, false)
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onGenericMotionEvent(event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_MOVE && event.source and InputDevice.SOURCE_JOYSTICK == InputDevice.SOURCE_JOYSTICK) {
            viewModel.updateGamepadAxes(
                floatArrayOf(
                    event.getAxisValue(MotionEvent.AXIS_X),
                    -event.getAxisValue(MotionEvent.AXIS_Y),
                    event.getAxisValue(MotionEvent.AXIS_Z),
                    -event.getAxisValue(MotionEvent.AXIS_RZ)
                )
            )

            val xAxis = event.getAxisValue(MotionEvent.AXIS_HAT_X)
            val yAxis = event.getAxisValue(MotionEvent.AXIS_HAT_Y)

            when (xAxis) {
                -1.0f -> GamepadKey.LEFT
                1.0f -> GamepadKey.RIGHT
                else -> null
            }?.let {
                if (xKeyPrev != it) {
                    viewModel.updateGamepadKey(it, true)
                    xKeyPrev = it
                }
            } ?: xKeyPrev?.let {
                viewModel.updateGamepadKey(it, false)
                xKeyPrev = null
            }

            when (yAxis) {
                -1.0f -> GamepadKey.UP
                1.0f -> GamepadKey.DOWN
                else -> null
            }?.let {
                if (yKeyPrev != it) {
                    viewModel.updateGamepadKey(it, true)
                    yKeyPrev = it
                }
            } ?: yKeyPrev?.let {
                viewModel.updateGamepadKey(it, false)
                yKeyPrev = null
            }

            return true
        }
        return super.onGenericMotionEvent(event)
    }

    private fun gamepadKeyFromKeyCode(keyCode: Int) = when (keyCode) {
        KeyEvent.KEYCODE_DPAD_UP -> GamepadKey.UP
        KeyEvent.KEYCODE_DPAD_DOWN -> GamepadKey.DOWN
        KeyEvent.KEYCODE_DPAD_LEFT -> GamepadKey.LEFT
        KeyEvent.KEYCODE_DPAD_RIGHT -> GamepadKey.RIGHT
        KeyEvent.KEYCODE_BUTTON_A -> GamepadKey.A
        KeyEvent.KEYCODE_BUTTON_B -> GamepadKey.B
        KeyEvent.KEYCODE_BUTTON_X -> GamepadKey.X
        KeyEvent.KEYCODE_BUTTON_Y -> GamepadKey.Y
        KeyEvent.KEYCODE_BUTTON_L1 -> GamepadKey.L1
        KeyEvent.KEYCODE_BUTTON_R1 -> GamepadKey.R1
        KeyEvent.KEYCODE_BUTTON_L2 -> GamepadKey.L2
        KeyEvent.KEYCODE_BUTTON_R2 -> GamepadKey.R2
        KeyEvent.KEYCODE_BUTTON_THUMBL -> GamepadKey.L3
        KeyEvent.KEYCODE_BUTTON_THUMBR -> GamepadKey.R3
        KeyEvent.KEYCODE_BUTTON_START -> GamepadKey.START
        KeyEvent.KEYCODE_BUTTON_SELECT -> GamepadKey.SELECT
        else -> null
    }
}
