package com.hpceen.tictactoe

import android.view.LayoutInflater
import android.view.ViewGroup
import com.hpceen.tictactoe.databinding.FragmentMainMenuBinding

class MainMenu : ViewBindingFragment<FragmentMainMenuBinding>() {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentMainMenuBinding
        get() = FragmentMainMenuBinding::inflate

    override fun setupView() = with(binding) {
        buttonOfflineGame.setOnClickListener {
            navController.navigate(MainMenuDirections.actionMainMenuToGame(Turn.X))
        }
        buttonOnlineGame.setOnClickListener {
            navController.navigate(MainMenuDirections.actionMainMenuToGameNewArchitecture())
        }
    }
}