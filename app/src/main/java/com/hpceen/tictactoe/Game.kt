package com.hpceen.tictactoe

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TableRow
import androidx.core.view.children
import androidx.navigation.fragment.navArgs
import com.hpceen.tictactoe.Constants.O
import com.hpceen.tictactoe.Constants.X
import com.hpceen.tictactoe.databinding.FragmentGameBinding

class Game : ViewBindingFragment<FragmentGameBinding>() {

    private val args: GameArgs by navArgs<GameArgs>()

    private var currentTurn: Int = 0
    private val imageButtons = mutableListOf<ImageButton>()
    private val gameResult = mutableListOf<Int>()
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentGameBinding
        get() = FragmentGameBinding::inflate

    private fun checkWinner(): Int {
        if (gameResult[0] == gameResult[1] && gameResult[0] == gameResult[2]) return gameResult[1]
        if (gameResult[3] == gameResult[4] && gameResult[3] == gameResult[5]) return gameResult[4]
        if (gameResult[6] == gameResult[7] && gameResult[6] == gameResult[8]) return gameResult[7]
        if (gameResult[0] == gameResult[3] && gameResult[0] == gameResult[6]) return gameResult[1]
        if (gameResult[1] == gameResult[4] && gameResult[1] == gameResult[7]) return gameResult[2]
        if (gameResult[2] == gameResult[5] && gameResult[2] == gameResult[8]) return gameResult[3]
        if (gameResult[0] == gameResult[4] && gameResult[0] == gameResult[8]) return gameResult[1]
        if (gameResult[2] == gameResult[4] && gameResult[2] == gameResult[6]) return gameResult[3]
        return -1;
    }

    private fun continueGame() {
        navController.navigate(GameDirections.actionGameToOfflineGame(checkWinner()))
    }

    override fun setupView() = with(binding) {
        currentTurn = args.turn
        for (row in binding.tableGame.children) {
            if (row is TableRow) {
                for (button in row.children) {
                    imageButtons.add(button as ImageButton)
                    gameResult.add(-1)
                }
            }
        }
        for (i in imageButtons.indices) {
            imageButtons[i].setOnClickListener { currentButton ->
                if (checkWinner() != -1) continueGame()
                currentButton.isEnabled = false
                when (currentTurn) {
                    X -> {
                        gameResult[i] = X
                        (currentButton as ImageButton).setImageResource(R.drawable.cross)
                        currentTurn = O
                    }

                    O -> {
                        gameResult[i] = X
                        (currentButton as ImageButton).setImageResource(R.drawable.circle)
                        currentTurn = X
                    }
                }
            }
        }
    }
}