package com.ms.ergoseatingdelivery

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle

class LoadingDialog(context: Context) : AlertDialog(context) {

    init {
        setCancelable(false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_loading)
    }
}