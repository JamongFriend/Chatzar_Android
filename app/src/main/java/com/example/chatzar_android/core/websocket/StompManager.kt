package com.example.chatzar_android.core.websocket

import io.reactivex.disposables.CompositeDisposable
import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.StompClient
import ua.naiksoftware.stomp.dto.LifecycleEvent

object StompManager {
    private const val WS_URL = "ws://10.0.2.2:8080/ws" // 너 서버 endpoint에 맞춰 수정

    private var stompClient: StompClient? = null
    private val disposables = CompositeDisposable()

    fun connect(onConnected: () -> Unit, onError: (Throwable) -> Unit) {
        stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, WS_URL)

        disposables.add(
            stompClient!!.lifecycle().subscribe { event ->
                when (event.type) {
                    LifecycleEvent.Type.OPENED -> onConnected()
                    LifecycleEvent.Type.ERROR -> onError(event.exception ?: RuntimeException("stomp error"))
                    LifecycleEvent.Type.CLOSED -> { /* 필요하면 재연결 */ }
                    else -> {}
                }
            }
        )

        stompClient!!.connect()
    }

    fun disconnect() {
        disposables.clear()
        stompClient?.disconnect()
        stompClient = null
    }

    fun client(): StompClient =
        stompClient ?: throw IllegalStateException("Stomp not connected")
}
