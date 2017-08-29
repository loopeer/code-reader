package com.loopeer.codereaderkt.utils

import rx.Observable
import rx.subjects.PublishSubject
import rx.subjects.SerializedSubject
import rx.subjects.Subject

class RxBus private constructor() {

    private val _bus = SerializedSubject(PublishSubject.create<Any>())

    fun send(o: Any) {
        _bus.onNext(o)
    }

    fun toObservable(): Observable<Any> {
        return _bus
    }

    companion object {

        @Volatile private var mDefaultInstance: RxBus? = null

        val instance: RxBus?
            get() {
                if (mDefaultInstance == null) {
                    synchronized(RxBus::class.java) {
                        if (mDefaultInstance == null) {
                            mDefaultInstance = RxBus()
                        }
                    }
                }
                return mDefaultInstance
            }
    }
}