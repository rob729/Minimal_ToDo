package com.example.robin.roomwordsample.utils

import android.content.Context
import android.view.inputmethod.InputMethodManager

object Utils {

    fun showKeyboard (ctx: Context) {
        val inputMethodManager =
            ctx.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
    }

    fun closeKeyboard(ctx: Context) {
        val inputMethodManager =
            ctx.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
    }
}