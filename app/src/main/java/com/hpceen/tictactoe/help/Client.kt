package com.hpceen.tictactoe.help

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.hpceen.tictactoe.Connection
import com.hpceen.tictactoe.OnlineGameViewModel
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
                try {
                    socket.connect(InetSocketAddress(address, Connection.PORT), 0)
                } catch (e: Exception) {
                    e.printStackTrace()
                    Log.w("ClientThread", "Try to reconnect")
                }
                Log.i("ClientThread", "Connection established")
                while (!socket.isConnected) {
                    sleep(1000)
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