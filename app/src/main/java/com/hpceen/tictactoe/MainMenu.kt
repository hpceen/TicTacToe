package com.hpceen.tictactoe

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import com.hpceen.tictactoe.databinding.FragmentMainMenuBinding
import com.hpceen.tictactoe.help_classes.ViewBindingFragment

class MainMenu : ViewBindingFragment<FragmentMainMenuBinding>() {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentMainMenuBinding
        get() = FragmentMainMenuBinding::inflate

    override fun setupView() = with(binding) {
        buttonOnlineGame.setOnClickListener {
            Toast.makeText(context, "Еще не реализовано", Toast.LENGTH_SHORT).show()
        }
        buttonOfflineGame.setOnClickListener {
            navController.navigate(MainMenuDirections.actionMainMenuToGameNewArchitecture())
        }
    }
}