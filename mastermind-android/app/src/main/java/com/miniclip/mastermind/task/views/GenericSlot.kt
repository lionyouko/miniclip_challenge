package com.miniclip.mastermind.task.views

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.widget.Button
import com.miniclip.mastermind.task.R
import com.miniclip.mastermind.task.defines.GameSettings

abstract class GenericSlot(context:Context, colorLight: Int, colorDark: Int) : Button(context) {
    init {
        setGradientColor(colorLight, colorDark)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        setTextColor(LayoutUtils.getColorSafe(context, R.color.darkBackgroundText))
        gravity = Gravity.CENTER

        layoutParams.width = GameSettings.tileSize
        layoutParams.height = GameSettings.tileSize

        LayoutUtils.setMargins(this, GameSettings.tileMargin, 0, GameSettings.tileMargin, GameSettings.tileMargin)
    }

    private fun setGradientColor(colorLight:Int, colorDark:Int) {
        val gradientColors = intArrayOf(colorLight, colorDark)
        val gradientDrawable =
            GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, gradientColors).apply {
                setCornerRadius(this)
            }
        background = gradientDrawable
    }

    abstract fun setCornerRadius(gradientDrawable: GradientDrawable)
}