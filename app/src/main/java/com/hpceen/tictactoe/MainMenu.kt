package com.hpceen.tictactoe

import android.app.Dialog
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import com.hpceen.tictactoe.databinding.FragmentMainMenuBinding
import com.hpceen.tictactoe.databinding.PopupRulesBinding
import com.hpceen.tictactoe.help_classes.ViewBindingFragment


class MainMenu : ViewBindingFragment<FragmentMainMenuBinding>() {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentMainMenuBinding
        get() = FragmentMainMenuBinding::inflate

    //Подготовка View
    override fun setupView() = with(binding) {
        buttonOnlineGame.setOnClickListener {
            Toast.makeText(context, "Еще не реализовано", Toast.LENGTH_SHORT).show()
        }
        buttonOfflineGame.setOnClickListener {
            navController.navigate(MainMenuDirections.actionMainMenuToGameNewArchitecture())
        }
        buttonRules.setOnClickListener {
            showRulesPopup()
        }
    }

    //popup Правил
    private fun showRulesPopup() {
        val dialog = Dialog(requireContext())
        val dialogBinding: PopupRulesBinding = PopupRulesBinding.inflate(dialog.layoutInflater)
        dialogBinding.rules.movementMethod = ScrollingMovementMethod()
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(dialogBinding.root)
        dialog.show()
    }
}