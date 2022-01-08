package com.alxnophis.jetpack.core.ui.manager

import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.view.View
import android.view.ViewTreeObserver

/***
 * Compose issue to be fixed in alpha 1.03
 * track from here : https://issuetracker.google.com/issues/192433071?pli=1
 * current work around.
 *
 * Source: https://stackoverflow.com/questions/68389802/how-to-clear-textfield-focus-when-closing-the-keyboard-and-prevent-two-back-pres
 * Possible fix: https://issuetracker.google.com/issues/192433071?pli=1
 */
class KeyBoardManager(context: Context) {

    private val activity = context as Activity
    private var keyboardDismissListener: KeyboardDismissListener? = null

    private abstract class KeyboardDismissListener(
        private val rootView: View,
        private val onKeyboardDismiss: () -> Unit
    ) : ViewTreeObserver.OnGlobalLayoutListener {
        private var isKeyboardClosed: Boolean = false
        override fun onGlobalLayout() {
            val r = Rect()
            rootView.getWindowVisibleDisplayFrame(r)
            val screenHeight = rootView.rootView.height
            val keypadHeight = screenHeight - r.bottom
            if (keypadHeight > screenHeight * 0.15) {
                // 0.15 ratio is right enough to determine keypad height.
                isKeyboardClosed = false
            } else if (!isKeyboardClosed) {
                isKeyboardClosed = true
                onKeyboardDismiss.invoke()
            }
        }
    }

    fun attachKeyboardDismissListener(onKeyboardDismiss: () -> Unit) {
        val rootView = activity.findViewById<View>(android.R.id.content)
        keyboardDismissListener = object : KeyboardDismissListener(rootView, onKeyboardDismiss) {}
        keyboardDismissListener?.let {
            rootView.viewTreeObserver.addOnGlobalLayoutListener(it)
        }
    }

    fun release() {
        val rootView = activity.findViewById<View>(android.R.id.content)
        keyboardDismissListener?.let {
            rootView.viewTreeObserver.removeOnGlobalLayoutListener(it)
        }
        keyboardDismissListener = null
    }
}
