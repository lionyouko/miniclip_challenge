package com.miniclip.mastermind.task.views

import android.content.Context
import android.graphics.drawable.GradientDrawable
import com.miniclip.mastermind.task.defines.GameSettings

class ClickableBallSlot(context: Context, colorLight:Int, colorDark:Int) : GenericSlot(context, colorLight, colorDark) {
    override fun setCornerRadius(gradientDrawable: GradientDrawable) {
        gradientDrawable.cornerRadius = GameSettings.cornerRadius
    }
}