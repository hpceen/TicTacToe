package com.hpceen.tictactoe

import android.view.LayoutInflater
import android.view.ViewGroup
import com.hpceen.tictactoe.databinding.FragmentOnlineGameBinding
import com.hpceen.tictactoe.help_classes.ViewBindingFragment

class OnlineGame : ViewBindingFragment<FragmentOnlineGameBinding>() {
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentOnlineGameBinding
        get() = FragmentOnlineGameBinding::inflate

    override fun setupView() {
        TODO("Not yet implemented")
    }

    override fun observe() {
        TODO("Not yet implemented")
    }
}