package com.hpceen.tictactoe.help

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.hpceen.tictactoe.Connection
import com.hpceen.tictactoe.OnlineGameViewModel
import java.net.ConnectException
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Socket

class Client(
    private val address: InetAddress,
    private val viewModel: OnlineGameViewModel
) {
    private lateinit var socket: Socket
    private var communicationThread: CommunicationThread? = null
    var isInitialised: MutableLiveData<Boolean> = MutableLiveData(false)

    init {
        ClientThread().start()
    }

    inner class ClientThread : Thread() {
        override fun run() {
            if (!currentThread().isInterrupted) {
                socket = Socket()
                var flag = true
                while (flag) {
                    try {
                        socket.connect(InetSocketAddress(address, Connection.PORT))
                        flag = false
                    } catch (e: ConnectException) {
                        Log.w("ClientThread", "Try to reconnect")
                    }
                }
                communicationThread = CommunicationThread(socket, viewModel)
                communicationThread?.start()
                isInitialised.postValue(true)
            }
        }
    }

    fun write(bytes: ByteArray) {
        communicationThread?.write(bytes)
    }

    fun closeConnection() {
        communicationThread?.closeConnection()
    }
}