package com.hpceen.tictactoe

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import android.net.wifi.p2p.WifiP2pConfig
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pManager
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener
import android.util.Log
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.Toast
import com.hpceen.tictactoe.databinding.FragmentConnectionBinding
import com.hpceen.tictactoe.help.ViewBindingFragment
import com.hpceen.tictactoe.help.WiFiDirectBroadcastReceiver


class Connection : ViewBindingFragment<FragmentConnectionBinding>() {
    private lateinit var context: Context
    override fun provideBinding(inflater: LayoutInflater) =
        FragmentConnectionBinding.inflate(inflater)

    private val wifiP2pManager: WifiP2pManager? by lazy {
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

    override fun onResume() {
        super.onResume()
        context.registerReceiver(receiver, intentFilter, Context.RECEIVER_NOT_EXPORTED)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("ConnectionFragment", "Fragment destroyed")
        context.unregisterReceiver(receiver)
        wifiP2pManager?.removeGroup(channel, object : WifiP2pManager.ActionListener {
            override fun onFailure(reason: Int) {
                Log.d("Disconnect", "Disconnect failed. Reason :$reason")
            }

            override fun onSuccess() {
                Log.d("Disconnect", "Disconnect success.")
            }
        })
    }

    @SuppressLint("MissingPermission")
    override fun setupView() {
        channel = wifiP2pManager?.initialize(requireActivity(), requireActivity().mainLooper, null)
        channel?.also { channel ->
            receiver = WiFiDirectBroadcastReceiver(wifiP2pManager!!, channel, this)
        }
        binding.discover.setOnClickListener {
            wifiP2pManager?.discoverPeers(channel, object : WifiP2pManager.ActionListener {
                override fun onSuccess() {
                    Toast.makeText(
                        requireContext(), "Discovery Started", Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onFailure(reason: Int) {
                    Toast.makeText(
                        requireContext(),
                        "Discovery starting failure due reason $reason",
                        Toast.LENGTH_SHORT
                    ).show()
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
                Toast.makeText(requireContext(), "No devices found", Toast.LENGTH_SHORT).show()
            }
        }
        connectionInfoListener = ConnectionInfoListener { info ->
            if (info.groupFormed) {
                Toast.makeText(
                    requireContext(), "Connected", Toast.LENGTH_LONG
                ).show()
                if (info.isGroupOwner) {
                    Toast.makeText(
                        requireContext(), "Group Formed. Group Owner.", Toast.LENGTH_LONG
                    ).show()
                    binding.start.setOnClickListener {
                        navController.navigate(
                            ConnectionDirections.actionConnectionToOnlineServerGame()
                        )
                    }
                } else {
                    Toast.makeText(requireContext(), "Group Formed.", Toast.LENGTH_SHORT).show()
                    binding.start.setOnClickListener {
                        navController.navigate(
                            ConnectionDirections.actionConnectionToOnlineClientGame(info.groupOwnerAddress)
                        )
                    }
                }
            }
        }
        binding.listView.setOnItemClickListener { _, _, position, _ ->
            val device = peers[position]
            val config = WifiP2pConfig().apply {
                deviceAddress = device.deviceAddress
            }
            wifiP2pManager?.connect(channel, config, object : WifiP2pManager.ActionListener {
                override fun onSuccess() {
                    binding.listView.isClickable = false
                    Toast.makeText(
                        requireContext(), "Connecting to ${device.deviceName}", Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onFailure(reason: Int) {
                    Toast.makeText(
                        requireContext(),
                        "Can't connect to ${device.deviceName}, Reason ${reason}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        }
    }

    override fun observe() {}

    companion object {
        const val MESSAGE_READ = 1
        const val PORT = 8080
    }
}