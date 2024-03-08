package com.hpceen.tictactoe

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageButton
import com.hpceen.tictactoe.databinding.FragmentOfflineGameBinding

class OfflineGame : ViewBindingFragment<FragmentOfflineGameBinding>() {
    //    private val args: OfflineGameArgs by navArgs<OfflineGameArgs>()
    private var winner = -1
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentOfflineGameBinding
        get() = FragmentOfflineGameBinding::inflate

    private lateinit var lastButton: ImageButton

    override fun setupView() {
        TODO("Not yet implemented")
    }
    //TODO winner offline game
//    override fun setupView() = with(binding) {
////        winner = args.winner
//        for (row in tableGame.children) {
//            if (row is TableRow) {
//                for (button in row.children) {
////                    button.setOnClickListener {
////                        lastButton = button as ImageButton
////                        button.isEnabled = false
////                        navController.navigate(OfflineGameDirections.actionOfflineGameToGame(X))
////                    }
//                }
//            }
//
//        }
//    }
}