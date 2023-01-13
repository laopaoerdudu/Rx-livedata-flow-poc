package com.wwe

import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.rxkotlin.zipWith
import io.reactivex.schedulers.Schedulers
import org.junit.Test

class RxjavaTest {

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

    @Test
    fun testConcatWith() {
        Single.just("1, 2, 3")
            .concatWith(Single.just("100, 200"))
    }


}