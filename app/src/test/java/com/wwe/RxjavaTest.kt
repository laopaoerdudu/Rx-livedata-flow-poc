package com.wwe

import io.reactivex.Single
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.rxkotlin.zipWith
import org.junit.Test

class RxjavaTest {

    /** 用于执行串行的网络请求 */
    @Test
    fun testFlatMap() {
        Single.just(1)
            .flatMap {
                Single.just("hello $it")
            }.subscribeBy(
                onSuccess = {
                    println(it)
                },
                onError = {

                }
            )
    }

    /** 用于执行并行的网络请求 */
    @Test
    fun testZip() {
        Single.just("hello")
            .zipWith(Single.just(1))
            .subscribeBy(
                onSuccess = {

                },
                onError = {

                }
            )
    }
}