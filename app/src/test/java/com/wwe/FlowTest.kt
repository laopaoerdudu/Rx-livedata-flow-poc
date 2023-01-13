package com.wwe

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
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

    /** debounce 函数可以用来确保 flow 的各项数据之间存在一定的时间间隔，如果是时间点过于临近的数据只会保留最后一条。 */
    @Test
    fun testDebounce() = runTest {
        flow {
            emit(1)
            emit(2)
            delay(600)
            emit(3)
            delay(100)
            emit(4)
            delay(100)
            emit(5)
        }
            .debounce(500)
            .collect {
                println(it)
            }
    }

    /** sample 采样的意思，它可以从 flow 的数据流当中按照一定的时间间隔来采样某一条数据。 */
    @Test
    fun testSample() = runTest {
        flow {
            while (true) {
                emit("发送一条弹幕")
            }
        }
            .sample(1000)
            .flowOn(Dispatchers.IO)
            .collect {
                println(it)
            }
    }

    /** reduce 函数是一个终端操作符函数。 */
    @Test
    fun testReduce() = runTest {
        // acc: Flow的累积值，value：Flow的当前值
        val result = flow {
            for (i in (1..100)) {
                emit(i)
            }
        }.reduce { acc, value -> acc + value }
        println(result)
    }

    /** fold 也是一个终端操作符函数。fold 函数需要传入一个初始值，这个初始值会作为首个累积值被传递到fold的函数体当中 */
    @Test
    fun testFold() = runTest {
        val result = flow {
            for (i in ('A'..'Z')) {
                emit(i.toString())
            }
        }.fold("Alphabet: ") { acc, value -> acc + value}
        println(result)
    }

    @Test
    fun testFlatMapConcat() = runTest {
        flowOf(1, 2, 3)
            .flatMapConcat {
                flowOf("a$it", "b$it")
            }
            .collect {
                println(it)
            }
    }

    /** flatMapMerge 函数的内部是启用并发来进行数据处理的，它不会保证最终结果的顺序。 */
    @Test
    fun testFlatMapMerge() = runTest {
        flowOf(300, 200, 100)
            .flatMapMerge {
                flow {
                    delay(it.toLong())
                    emit("a$it")
                    emit("b$it")
                }
            }
            .collect {
                println("collect: $it")
            }
    }

    @Test
    fun testFlatMapLatest() = runTest {
        flow {
            emit("Kotlin")
            delay(150)
            emit("Java")
            delay(50)
            emit("C++")
        }.flatMapLatest {
            flow {
                delay(100)
                emit("Hello $it")
            }
        }
            .collect {
                println("collect: $it")
            }
    }

    @Test
    fun testZip() = runTest {
        val flow1 = flowOf("a", "b", "c")
        val flow2 = flowOf(1, 2, 3, 4, 5)
        flow1.zip(flow2) { a, b ->
            a + b
        }.collect {
            println("collect: $it")
        }
    }

    @Test
    fun testZipByTime() = runTest {
        val start = System.currentTimeMillis()
        val flow1 = flow {
            delay(3000)
            emit("a")
        }
        val flow2 = flow {
            delay(2000)
            emit(1)
        }
        flow1.zip(flow2) { a, b ->
            a + b
        }.onEach {
            println("onEach: $it")
        }
            .collect {
                val end = System.currentTimeMillis()
                // 如果它们之间是串行关系的话，那么最终的总耗时一定是5秒以上。
                println("Time cost: ${end - start}ms")
            }
    }

    @Test
    fun testBuffer() = runTest {
        flow {
            emit(1)
            delay(1000)
            emit(2)
            delay(1000)
            emit(3)
        }
            .onEach {
                println("onEach: $it")
            }
            .buffer()
            .collect {
                delay(1000)
                println("collect: $it")
            }
    }

    @Test
    fun testConflate() = runTest {
        flow {
            var count = 0
            while (true) {
                emit(count)
                delay(1000)
                count++
            }
        }
            .conflate()
            .collect {
                delay(2000)
                println("collect: $it")
            }
    }
}