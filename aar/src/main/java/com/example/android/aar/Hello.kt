package com.example.android.aar

@Suppress("unused")
object Hello {
    /**
     * message
     */
    @JvmStatic
    val message = "Hello World!"

    /**
     * fetch message
     *
     * @return message
     */
    @JvmStatic
    fun fetchMessage(): String = message
}
