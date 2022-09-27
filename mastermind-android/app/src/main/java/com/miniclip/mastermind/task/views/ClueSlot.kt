package com.miniclip.mastermind.task.views

import android.content.Context
import android.graphics.drawable.GradientDrawable
import com.miniclip.mastermind.task.R
import com.miniclip.mastermind.task.types.ClueType

class ClueSlot(context: Context, type:ClueType, howManyGuesses:Int) :
        GenericSlot(context, LayoutUtils.getColorSafe(context, R.color.colorAccent), LayoutUtils.getColorSafe(context, R.color.colorAccentDark)) {
    init {
        var buttonText = howManyGuesses.toString()

        buttonText += when(type) {
            ClueType.LOCATION -> {
                "L"
            }
            ClueType.COLOR -> {
                "C"
            }
        }

        text = buttonText

    }

    override fun setCornerRadius(gradientDrawable: GradientDrawable) {
        // Square, no need to do anything
    }


}