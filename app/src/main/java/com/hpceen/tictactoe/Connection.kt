package com.hpceen.tictactoe

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import android.net.wifi.p2p.WifiP2pConfig
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pManager
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener
import android.os.Handler
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.Toast
import com.hpceen.tictactoe.databinding.FragmentConnectionBinding
import com.hpceen.tictactoe.help.SendReceive
import com.hpceen.tictactoe.help.ViewBindingFragment
import com.hpceen.tictactoe.help.WiFiDirectBroadcastReceiver
import java.io.IOException
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.net.Socket

class Connection : ViewBindingFragment<FragmentConnectionBinding>() {
    val viewModel = ConnectionViewModel()
    private lateinit var context: Context
    private lateinit var sendReceive: SendReceive
    lateinit var handler: Handler
    lateinit var server: Server
    lateinit var client: Client
    override fun provideBinding(inflater: LayoutInflater) =
        FragmentConnectionBinding.inflate(inflater)

    val wifiP2pManager: WifiP2pManager? by lazy(LazyThreadSafetyMode.NONE) {
        activity?.getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager?
    }


    lateinit var peerListListener: WifiP2pManager.PeerListListener
    lateinit var connectionInfoListener: ConnectionInfoListener

    var channel: WifiP2pManager.Channel? = null
    var receiver: BroadcastReceiver? = null
    var peers = mutableListOf<WifiP2pDevice>()
    var deviceNameList = mutableListOf<String>()

    val intentFilter = IntentFilter().apply {
        addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)
        addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)
        addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)
        addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.context = context
    }

    class Server(private val sendReceive: SendReceive) : Thread() {
        override fun run() {
            super.run()
            try {
                sendReceive.start()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    class Client(private val sendReceive: SendReceive) : Thread() {

        override fun run() {
            super.run()
            try {
                sendReceive.start()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun setupView() {
        channel = wifiP2pManager?.initialize(activity, activity?.mainLooper, null)
        channel?.also { channel ->
            receiver = WiFiDirectBroadcastReceiver(wifiP2pManager!!, channel, this)
        }
        context.registerReceiver(receiver, intentFilter, Context.RECEIVER_NOT_EXPORTED)
        binding.discover.setOnClickListener {
            wifiP2pManager?.discoverPeers(channel, object : WifiP2pManager.ActionListener {
                override fun onSuccess() {
                    binding.connectionStatus.text = "Discovery Started"
                }

                override fun onFailure(reason: Int) {
                    binding.connectionStatus.text = "Discovery starting failure reason $reason"
                }
            })
        }
        peerListListener = WifiP2pManager.PeerListListener { peerList ->
            if (peerList!!.deviceList != peers) {
                deviceNameList.clear()
                peers.clear()
                peers.addAll(peerList.deviceList)

                peers.forEach {
                    deviceNameList.add(it.deviceName)
                }
                if (activity != null) {
                    binding.listView.adapter = ArrayAdapter(
                        requireActivity().applicationContext,
                        android.R.layout.simple_list_item_1,
                        deviceNameList
                    )
                }
            }
            if (peers.size == 0) {
                Toast.makeText(requireContext(), "No device found", Toast.LENGTH_SHORT).show()
            }
        }
        handler = Handler(activity?.mainLooper!!) { msg ->
            when (msg.what) {
                MESSAGE_READ -> {
                    val tempMessage = String(msg.obj as ByteArray, 0, msg.arg1)
                    binding.message.text = tempMessage
                }
            }
            true
        }
        connectionInfoListener = ConnectionInfoListener { info ->
            if (info.groupFormed) {
                if (info.isGroupOwner) {
                    binding.connectionStatus.text = "Server"
                    val socket = ServerSocket(8888).accept()
                    sendReceive = SendReceive(socket, handler)
                    server = Server(sendReceive)
                    server.start()
                } else {
                    binding.connectionStatus.text = "Client"
                    val socket = Socket().apply {
                        connect(InetSocketAddress(info.groupOwnerAddress, 8888))
                    }
                    sendReceive = SendReceive(socket, handler)
                    client = Client(sendReceive)
                    client.start()
                }
                viewModel.isConnected.postValue(true)
            }
        }
        binding.listView.setOnItemClickListener { _, _, position, _ ->
            val device = peers[position]
            val config = WifiP2pConfig().apply { deviceAddress = device.deviceAddress }
            wifiP2pManager?.connect(channel, config, object : WifiP2pManager.ActionListener {
                override fun onSuccess() {
                    Toast.makeText(
                        requireContext(), "Connected to ${device.deviceName}", Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onFailure(reason: Int) {
                    Toast.makeText(
                        requireContext(),
                        "Can't connect to ${device.deviceName}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        }
    }

    override fun observe() = with(viewModel) {
        isConnected.observe(this@Connection) {
            binding.send.setOnClickListener {
                val message = binding.writeMessage.text.toString()
                sendReceive.write(message.toByteArray())
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        channel?.close()
        context.unregisterReceiver(receiver)
    }

    companion object {
        const val MESSAGE_READ = 1
    }
}