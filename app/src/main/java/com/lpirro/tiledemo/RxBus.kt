package com.lpirro.tiledemo

import com.lpirro.tiledemo.customquicksettings.QuickSettingModel
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

object RxBus {
    private val subject = PublishSubject.create<Message>()

    fun publish(model: Message) {
        subject.onNext(model)
    }

    fun listen(): Observable<Message> {
        return subject.hide()
    }
}

sealed class Message
object CloseQuickSetting: Message()
