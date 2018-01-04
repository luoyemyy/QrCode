package com.github.luoyemyy.qrcode.core

interface FocusListener {
    fun focus()
    fun parseResult(text: String)
}