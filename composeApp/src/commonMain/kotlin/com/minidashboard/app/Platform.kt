package com.minidashboard.app

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform