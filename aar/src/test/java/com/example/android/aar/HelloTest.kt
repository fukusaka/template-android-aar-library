package com.example.android.aar

import org.junit.Assert.assertEquals
import org.junit.Test

class HelloTest {

    @Test
    fun getMessage() {
        assertEquals(Hello.message, "Hello World!")
    }

    @Test
    fun fetchMessage() {
        assertEquals(Hello.fetchMessage(), "Hello World!")
    }
}