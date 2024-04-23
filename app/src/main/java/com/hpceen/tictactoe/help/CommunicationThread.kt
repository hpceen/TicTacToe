package com.hpceen.tictactoe.help

import android.os.Handler
import android.os.Looper
import android.util.Log
import com.hpceen.tictactoe.Connection
import com.hpceen.tictactoe.OnlineGameViewModel
import java.io.InputStream
import java.io.OutputStream
import java.net.Socket

class CommunicationThread(
    private val socket: Socket, private val viewModel: OnlineGameViewModel
) : Thread() {
    private var handler: Handler = Handler(Looper.getMainLooper()) { message ->
        when (message.what) {
            Connection.MESSAGE_READ -> {
                val strings = String(message.obj as ByteArray, 0, message.arg1).split(" ")
                viewModel.turn.postValue(Pair(strings[0].toInt(), strings[1].toInt()))
            }
        }
        true
    }
    private lateinit var outputStream: OutputStream
    private lateinit var inputStream: InputStream
    private val buffer = ByteArray(1024)

    override fun run() {
        outputStream = socket.getOutputStream()
        inputStream = socket.getInputStream()
        while (!socket.isClosed) {
            try {
                val bytes = inputStream.read(buffer)
                if (bytes > 0) {
                    handler.obtainMessage(Connection.MESSAGE_READ, bytes, -1, buffer).sendToTarget()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e(
                    "com.hpceen.tictactoe.help.CommunicationThread",
                    "Something doesn't work correctly: ${e.message}"
                )
            }
        }
    }

    fun closeConnection() {
        socket.close()
        inputStream.close()
        outputStream.close()
    }

    fun write(bytes: ByteArray) {
        try {
            outputStream.write(bytes)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}