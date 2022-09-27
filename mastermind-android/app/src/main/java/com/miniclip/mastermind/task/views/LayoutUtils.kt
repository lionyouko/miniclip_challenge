package com.miniclip.mastermind.task.views

import android.content.Context
import android.os.Build
import android.view.View
import android.view.ViewGroup

object LayoutUtils {
    fun setMargins(view:View, left:Int, top:Int, right:Int, bottom:Int) {
        if (view.layoutParams is ViewGroup.MarginLayoutParams) {
            val mlp:ViewGroup.MarginLayoutParams = view.layoutParams as ViewGroup.MarginLayoutParams
            mlp.setMargins(left, top, right, bottom)
            view.requestLayout()
        }
    }

    fun getColorSafe(context: Context, colorId:Int) : Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            context.resources.getColor(colorId, context.theme)
        } else {
            context.resources.getColor(colorId)
        }
    }
}