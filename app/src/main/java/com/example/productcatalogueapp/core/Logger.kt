package com.example.productcatalogueapp.core

import android.util.Log
import com.example.productcatalogueapp.BuildConfig


object Logger {

    fun d(tag: String, message: String) {
        if (BuildConfig.DEBUG) Log.d(tag, message)
    }

    fun e(tag: String, message: String, throwable: Throwable? = null) {
        if (BuildConfig.DEBUG) Log.e(tag, message, throwable)
    }
}
