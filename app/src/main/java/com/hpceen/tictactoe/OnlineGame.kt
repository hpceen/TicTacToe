package com.hpceen.tictactoe

import android.view.LayoutInflater
import android.view.ViewGroup
import com.hpceen.tictactoe.databinding.FragmentOnlineGameBinding

class OnlineGame : ViewBindingFragment<FragmentOnlineGameBinding>() {
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentOnlineGameBinding
        get() = FragmentOnlineGameBinding::inflate

    override fun setupView() {
        TODO("Not yet implemented")
    }


}