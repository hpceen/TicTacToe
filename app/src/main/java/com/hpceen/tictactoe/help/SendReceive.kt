package com.hpceen.tictactoe.help

import android.os.Handler
import com.hpceen.tictactoe.Connection
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.Socket

class SendReceive(socket: Socket, handler: Handler) : Thread() {
    private var handler: Handler
    private lateinit var inputStream: InputStream
    private lateinit var outputStream: OutputStream
    private val buffer = ByteArray(1024)

    init {
        this.handler = handler
        try {
            inputStream = socket.getInputStream()
            outputStream = socket.getOutputStream()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun run() {
        super.run()
        try {
            val bytes = inputStream.read(buffer)
            if (bytes > 0) {
                handler.obtainMessage(Connection.MESSAGE_READ, bytes, -1, buffer).sendToTarget()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun write(bytes: ByteArray) {
        try {
            outputStream.write(bytes)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

}