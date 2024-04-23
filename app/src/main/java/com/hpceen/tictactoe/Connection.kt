package com.hpceen.tictactoe

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import android.net.wifi.p2p.WifiP2pConfig
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pManager
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.ArrayAdapter
import android.widget.Toast
import com.hpceen.tictactoe.databinding.FragmentConnectionBinding
import com.hpceen.tictactoe.databinding.PopupClientAttentionBinding
import com.hpceen.tictactoe.help.ViewBindingFragment
import com.hpceen.tictactoe.help.WiFiDirectBroadcastReceiver


class Connection : ViewBindingFragment<FragmentConnectionBinding>() {
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

    private val intentFilter = IntentFilter().apply {
        addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)
        addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)
        addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)
        addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION)
    }

    override fun onDestroy() {
        super.onDestroy()
        requireContext().unregisterReceiver(receiver)
        wifiP2pManager?.removeGroup(channel, object : WifiP2pManager.ActionListener {
            override fun onFailure(reason: Int) {
                Log.d("Disconnect", "Disconnection failed. Reason :$reason")
            }

            override fun onSuccess() {
                Log.d("Disconnect", "Disconnect success.")
            }
        })
    }

    override fun onResume() {
        super.onResume()
        requireContext().registerReceiver(receiver, intentFilter)
    }

    @SuppressLint("MissingPermission")
    override fun setupView() {
        peerListListener = WifiP2pManager.PeerListListener { peerList ->
            if (peerList!!.deviceList != peers) {
                deviceNameList.clear()
                peers.clear()
                peers.addAll(peerList.deviceList)

                peers.forEach {
                    deviceNameList.add(it.deviceName)
                }
                if (activity != null) {
                    binding.listViewDeviceNames.adapter = ArrayAdapter(
                        requireActivity().applicationContext,
                        android.R.layout.simple_list_item_1,
                        deviceNameList
                    )
                }
            }
            if (peers.size == 0) {
                Toast.makeText(requireContext(), "Устройств не найдено", Toast.LENGTH_SHORT).show()
            }
        }
        connectionInfoListener = ConnectionInfoListener { info ->
            if (info.groupFormed) {
                Toast.makeText(
                    requireContext(), "Подключено", Toast.LENGTH_LONG
                ).show()
                if (info.isGroupOwner) {
                    binding.buttonStartGame.visibility = View.VISIBLE
                    binding.buttonStartGame.setOnClickListener {
                        navController.navigate(
                            ConnectionDirections.actionConnectionToOnlineServerGame()
                        )
                    }
                } else {
                    showClientAttentionPopup()
                    binding.buttonStartGame.visibility = View.VISIBLE
                    binding.buttonStartGame.setOnClickListener {
                        navController.navigate(
                            ConnectionDirections.actionConnectionToOnlineClientGame(info.groupOwnerAddress)
                        )
                    }
                }
            }
        }
        channel = wifiP2pManager?.initialize(requireContext(), requireContext().mainLooper, null)
        channel?.also { channel ->
            receiver = WiFiDirectBroadcastReceiver(
                wifiP2pManager!!,
                channel,
                peerListListener,
                connectionInfoListener
            )
        }
        binding.buttonDiscoverPeers.setOnClickListener {
            wifiP2pManager?.discoverPeers(channel, object : WifiP2pManager.ActionListener {
                override fun onSuccess() {
                    Toast.makeText(
                        requireContext(), "Поиск", Toast.LENGTH_SHORT
                    ).show()
                }

                @Suppress("IMPLICIT_CAST_TO_ANY")
                override fun onFailure(reason: Int) {
                    Toast.makeText(
                        requireContext(),
                        "Не удалось начать поиск, причина:${
                            when (reason) {
                                0 -> "ERROR (ошибка)"
                                1 -> "P2P_UNSUPPORTED (WiFi - Direct не поддерживается)"
                                2 -> "BUSY (WiFiManager занят)"
                                else -> {}
                            }
                        }",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
        }
        binding.listViewDeviceNames.setOnItemClickListener { _, _, position, _ ->
            val device = peers[position]
            val config = WifiP2pConfig().apply {
                deviceAddress = device.deviceAddress
            }
            wifiP2pManager?.connect(channel, config, object : WifiP2pManager.ActionListener {
                override fun onSuccess() {
                    binding.listViewDeviceNames.isClickable = false
                    Toast.makeText(
                        requireContext(), "Подключаюсь к ${device.deviceName}", Toast.LENGTH_SHORT
                    ).show()
                }
                @Suppress("IMPLICIT_CAST_TO_ANY")
                override fun onFailure(reason: Int) {
                    Toast.makeText(
                        requireContext(),
                        "Не удалось подключиться к ${device.deviceName}, причина:${
                            when (reason) {
                                0 -> "ERROR (ошибка)"
                                1 -> "P2P_UNSUPPORTED (WiFi - Direct не поддерживается)"
                                2 -> "BUSY (WiFiManager занят)"
                                else -> {}
                            }
                        }",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
        }
    }

    private fun showClientAttentionPopup() {
        val dialog = Dialog(requireContext())
        val dialogBinding: PopupClientAttentionBinding =
            PopupClientAttentionBinding.inflate(dialog.layoutInflater)
        dialogBinding.rules.movementMethod = ScrollingMovementMethod()
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(dialogBinding.root)
        dialog.show()
    }

    override fun observe() {}

    companion object {
        const val MESSAGE_READ = 1
        const val PORT = 8080
    }
}