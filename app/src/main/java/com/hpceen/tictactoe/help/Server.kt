package com.hpceen.tictactoe.help

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.hpceen.tictactoe.Connection
import com.hpceen.tictactoe.OnlineGameViewModel
import java.net.ServerSocket
import java.net.Socket

class Server(private val viewModel: OnlineGameViewModel) {
    private lateinit var socket: Socket
    private var communicationThread: CommunicationThread? = null
    var isInitialised: MutableLiveData<Boolean> = MutableLiveData(false)
    private lateinit var serverSocket: ServerSocket

    init {
        ServerThread().start()
    }

    inner class ServerThread : Thread() {
        override fun run() {
            val serverSocket = ServerSocket(Connection.PORT)
            if (!currentThread().isInterrupted) {
                try {
                    socket = serverSocket.accept()
                    communicationThread = CommunicationThread(socket, viewModel)
                    communicationThread?.start()
                    isInitialised.setValue(true)
                } catch (e: Exception) {
                    Log.e("ServerThread", "Something doesn't work correctly")
                    e.printStackTrace()
                }
            }
        }
    }

    fun write(bytes: ByteArray) {
        communicationThread?.write(bytes)
    }

    fun closeConnection() {
        communicationThread?.closeConnection()
        serverSocket.close()
    }
}