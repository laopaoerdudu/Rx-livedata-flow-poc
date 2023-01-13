## 为什么要用 Flow?

使用 flow 构建函数构建出的 Flow 是 Cold Flow，也叫做冷流。所谓冷流就是在没有任何接受端的情况下，Flow是不会工作的。

```
val timeFlow = flow {
    var time = 0
    while (true) {
        emit(time)
        delay(1000)
        time++
    }
}
```

## Flow 如何处理流速不均匀问题？

collectLatest ?

>`collectLatest` 函数只接收处理最新的数据。

### flatMapConcat

flatMap 的核心，就是将两个 flow 中的数据进行映射、合并、压平成一个 flow，最后再进行输出。
flatMap 的运行方式是一个 flow 中的数据流向另外一个 flow，是串行的关系。

>请求一个网络资源时需要依赖于另外一个网络资源的返回值。

```
fun getToken(): Flow<String> = flow {
    // send request to get token
    emit(token)
}

fun getUserInfo(token: String): Flow<String> = flow {
    // send request with token to get user info
    emit(userInfo)
}

getToken()
        .flatMapConcat { token ->
            getUserInfo(token)
        }
        .flowOn(Dispatchers.IO)
        .collect { userInfo ->
            println(userInfo)
        }
```

当然，这个用法并不仅限于只能将两个 flow 串连成一条链式任务，如果你有更多的任务需要串到这同一条链上，只需要不断连缀 `flatMapConcat` 即可：

```
flow1.flatMapConcat { flow2 }
             .flatMapConcat { flow3 }
             .flatMapConcat { flow4 }
             .collect { userInfo ->
                 println(userInfo)
             }
```

### flatMapMerge

concat 是连接的意思，merge 是合并的意思。连接一定会保证数据是**按照原有的顺序**连接起来的，而**合并则只保证将数据合并到一起，并不会保证顺序**。

flatMapMerge 函数的内部是启用并发来进行数据处理的，它不会保证最终结果的顺序。

### flatMapLatest

它只接收处理最新的数据。

### zip

使用 zip 连接的两个flow，它们之间是并行的运行关系。这点和 flatMap 差别很大。

3个请求同时并发处理：

```
fun getRealtimeWeather(): Flow<String> = flow {
    // send request to realtime weather
    emit(realtimeWeather)
}

fun getSevenDaysWeather(): Flow<String> = flow {
    // send request to get 7 days weather
    emit(sevenDaysWeather)
}

fun getWeatherBackgroundImage(): Flow<String> = flow {
    // send request to get weather background image
    emit(weatherBackgroundImage)
}

fun main() {
    runBlocking {
        getRealtimeWeather()
            .zip(getSevenDaysWeather()) { realtimeWeather, sevenDaysWeather ->
                weather.realtimeWeather = realtimeWeather
                weather.sevenDaysWeather = sevenDaysWeather
                weather
            }.zip(getWeatherBackgroundImage()) { weather, weatherBackgroundImage ->
                weather.weatherBackgroundImage = weatherBackgroundImage
                weather
            }.collect { weather ->
            }
    }
}
```

### buffer

解决 Flow 流速不均匀的问题。

>所谓流速不均匀问题，指的就是Flow上游发送数据的速度和Flow下游处理数据的速度不匹配，从而可能引发的一系列问题。

buffer 函数会让 flow 函数和 collect 函数运行在不同的协程当中，这样 flow 中的数据发送就不会受 collect 函数的影响了。

buffer 函数其实就是一种背压的处理策略，它提供了一份缓存区，当 Flow 数据流速不均匀的时候，使用这份缓存区来保证程序运行效率。

flow 函数只管发送自己的数据，它不需要关心数据有没有被处理，反正都缓存在 buffer 当中。

而 collect 函数只需要一直从 buffer 中获取数据来进行处理就可以了。

但是，如果流速不均匀问题持续放大，缓存区的内容越来越多时又该怎么办呢？

这个时候，我们又需要引入一种新的策略了-`conflate`，来适当地丢弃一些数据。

buffe r函数最大的问题在于，不管怎样调整它缓冲区的大小（buffer 函数可以通过传入参数来指定缓冲区大小），都无法完全地保障程序的运行效果。
究其原因，主要还是因为 buffer 函数不会丢弃数据。

### conflate

只接收处理最新的数据，如果有新数据到来了而前一个数据还没有处理完，则会将前一个数据剩余的处理逻辑全部取消。

collectLatest 函数，buffer 函数，conflate 函数，可以并称为背压三剑客。

Ref:

https://mp.weixin.qq.com/s/lXQ8NzZCng8vCumXAT1ayg



























