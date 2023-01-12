package com.wwe

import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.test.runTest
import org.junit.Test

class FlowTest {

    @Test
    fun testOnEach() = runTest {
        flowOf(1, 2, 3, 4, 5).filter {
            it % 2 == 0
        }.onEach {
            println("onEach: $it")
        }.map {
            it * it
        }.collect {
            println("collect: $it")
        }
    }
}