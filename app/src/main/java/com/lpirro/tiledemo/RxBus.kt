package com.lpirro.tiledemo

import android.util.Log
import com.lpirro.tiledemo.customquicksettings.NotificationModel
import com.lpirro.tiledemo.customquicksettings.QuickSettingModel
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

object RxBus {
    private val subject = PublishSubject.create<Message>()
    private val notificationSubject = PublishSubject.create<NotificationModel>()

    fun publish(model: Message) {
        subject.onNext(model)
    }

    fun listen(): Observable<Message> {
        return subject.hide()
    }

    fun publishNotification(model: NotificationModel) {
        notificationSubject.onNext(model)
    }

    fun listenNotification(): PublishSubject<NotificationModel> {
        return notificationSubject
    }
}

sealed class Message
object CloseQuickSetting: Message()
object ClearAllNotification: Message()
data class ConfigSetting(val set: List<QuickSettingModel.TilesModel>): Message()
