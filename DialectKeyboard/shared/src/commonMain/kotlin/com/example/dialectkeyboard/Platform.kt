package com.example.dialectkeyboard

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform