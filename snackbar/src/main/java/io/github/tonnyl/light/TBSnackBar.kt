package io.github.tonnyl.light

import android.graphics.drawable.Drawable
import android.view.Gravity
import android.view.View
import android.widget.TextView
import androidx.annotation.CheckResult
import androidx.annotation.ColorInt
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar


/**
 * Created by hd on 2018/7/13 .
 * manage top or bottom Snackbar
 */
class TBSnackBar {

    private var bSnackbar: Snackbar? = null

    @CheckResult
    fun make(
        view: View,
        text: CharSequence,
        duration: Int = Snackbar.LENGTH_SHORT,
        textIcon: Drawable?,
        @ColorInt backgroundColorInt: Int = ContextCompat.getColor(
            view.context,
            R.color.color_normal
        ),
        @ColorInt textColorInt: Int = ContextCompat.getColor(view.context, android.R.color.white),
        actionIcon: Drawable?,
        @ColorInt actionTextColorInt: Int = ContextCompat.getColor(
            view.context,
            android.R.color.white
        )
    ): TBSnackBar {
        bSnackbar = with(
            make(
                view,
                text,
                duration,
                textIcon,
                backgroundColorInt,
                textColorInt
            ).getSnackbar() as Snackbar
        ) {
            setSnackbarText(this.view, actionIcon, actionTextColorInt, R.id.snackbar_action)
            this
        }
        return this
    }

    @CheckResult
    fun make(
        view: View,
        text: CharSequence,
        duration: Int = Snackbar.LENGTH_SHORT,
        textIcon: Drawable?,
        @ColorInt backgroundColorInt: Int = ContextCompat.getColor(
            view.context,
            R.color.color_normal
        ),
        @ColorInt textColorInt: Int = ContextCompat.getColor(view.context, android.R.color.white)
    ): TBSnackBar {
        bSnackbar = with(Snackbar.make(view, text, duration)) {
            this.view.apply {
                setBackgroundTint(backgroundColorInt)
            }
            setSnackbarText(this.view, textIcon, textColorInt, R.id.snackbar_text)
            this
        }
        return this
    }

    inline fun setAction(@StringRes resId: Int, crossinline click: () -> Unit): TBSnackBar {
        return setAction(resId, View.OnClickListener { click() })
    }

    inline fun setAction(text: CharSequence, crossinline click: () -> Unit): TBSnackBar {
        return setAction(text, View.OnClickListener { click() })
    }

    fun setAction(@StringRes resId: Int, listener: View.OnClickListener): TBSnackBar {
        bSnackbar?.setAction(resId, listener)
        return this
    }

    fun setAction(text: CharSequence, listener: View.OnClickListener): TBSnackBar {
        bSnackbar?.setAction(text, listener)
        return this
    }

    fun show() {
        bSnackbar?.show()
    }

    fun dismiss() {
        bSnackbar?.dismiss()
    }

    fun getSnackbar(): Any? {
        return bSnackbar
    }

    private fun setSnackbarText(
        view: View,
        drawableIcon: Drawable?,
        @ColorInt textColorInt: Int,
        @IdRes textViewId: Int
    ) {
        view.findViewById<TextView>(textViewId)
            .apply {
                // Set the left icon of message.
                setCompoundDrawablesWithIntrinsicBounds(drawableIcon, null, null, null)
                // Set the padding between message and icon.
                compoundDrawablePadding = 16
                // To make icon and message aligned.
                this.gravity = Gravity.START or Gravity.CENTER
                // Chan
                // ge color of message text.
                setTextColor(textColorInt)
            }
    }

}