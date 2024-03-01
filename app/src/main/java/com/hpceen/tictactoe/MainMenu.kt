package com.hpceen.tictactoe

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.hpceen.tictactoe.databinding.FragmentMainMenuBinding

class MainMenu : Fragment() {

    private lateinit var binding: FragmentMainMenuBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentMainMenuBinding.inflate(layoutInflater)
        navController = findNavController()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            buttonOfflineGame.setOnClickListener {
                navController.navigate(R.id.action_mainMenu_to_offlineGame)
            }
            buttonOnlineGame.setOnClickListener {
                navController.navigate(R.id.action_mainMenu_to_onlineGame)
            }
        }
    }
}