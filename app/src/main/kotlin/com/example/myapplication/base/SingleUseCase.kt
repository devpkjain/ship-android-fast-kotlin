package com.example.myapplication.base

import com.example.myapplication.ExecutionSchedulers
import io.reactivex.Completable
import io.reactivex.Single
import timber.log.Timber

abstract class SingleUseCase<R, in Params>
constructor(private val executionSchedulers: ExecutionSchedulers) {

    protected abstract fun buildUseCase(params: Params?, fresh: Boolean): Single<R>

    protected abstract fun validate(params: Params?): Completable

    fun execute(params: Params? = null, fresh: Boolean = false): Single<R> = validate(params)
        .andThen(
            Single.defer {
                buildUseCase(params, fresh)
                    .subscribeOn(executionSchedulers.io())
                    .observeOn(executionSchedulers.ui())
            }
        )
        .doOnError { Timber.tag("Single").d(it) }
}