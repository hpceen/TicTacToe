package com.hpceen.tictactoe

import android.app.Dialog
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.Window
import com.hpceen.tictactoe.databinding.FragmentMainMenuBinding
import com.hpceen.tictactoe.databinding.PopupRulesBinding
import com.hpceen.tictactoe.help.ViewBindingFragment


class MainMenu : ViewBindingFragment<FragmentMainMenuBinding>() {
    override fun provideBinding(inflater: LayoutInflater) =
        FragmentMainMenuBinding.inflate(inflater)

    //Подготовка View
    override fun setupView() = with(binding) {
        buttonOnlineGame.setOnClickListener {
            navController.navigate(MainMenuDirections.actionMainMenuToConnection())
        }
        buttonOfflineGame.setOnClickListener {
            navController.navigate(MainMenuDirections.actionMainMenuToGame())
        }
        buttonRules.setOnClickListener {
            showRulesPopup()
        }
    }

    override fun observe() {}

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