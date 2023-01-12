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

