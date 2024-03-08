package com.hpceen.tictactoe

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TableLayout
import android.widget.TableRow
import androidx.core.view.children
import androidx.core.view.isVisible
import com.hpceen.tictactoe.databinding.FragmentGameNewArchitectureBinding

class GameNewArchitecture : ViewBindingFragment<FragmentGameNewArchitectureBinding>() {
    private var viewModel = GameNewArchitectureViewModel()
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentGameNewArchitectureBinding
        get() = FragmentGameNewArchitectureBinding::inflate

    override fun setupView() = with(binding) {
        setupGameField()
        buttonBack.isEnabled = false
        buttonBack.isVisible = false
        buttonBack.setOnClickListener {
            navController.navigate(GameNewArchitectureDirections.actionGameNewArchitectureToMainMenu())
        }
        textViewTurn.setText(R.string.turn_x)
        val gameField = viewModel.gameField.value!!
        for (clusterIndex in gameField.indices) {
            for (cellIndex in gameField[clusterIndex].indices) {
                gameField[clusterIndex][cellIndex].button.setOnClickListener {
                    makeMove(clusterIndex, cellIndex)
                }
            }
        }
    }

    private fun makeMove(clusterIndex: Int, cellIndex: Int) {
        changeState(clusterIndex, cellIndex)
        nextTurn()
    }

    private fun nextTurn() = with(binding) {
        when (viewModel.gameState.value!!) {
            GameState.XWinner -> {
                textViewTurn.setText(R.string.winner_x)
                return
            }

            GameState.OWinner -> {
                textViewTurn.setText(R.string.winner_o)
                return
            }

            GameState.Draw -> {
                textViewTurn.setText(R.string.draw)
                return
            }

            GameState.Nothing -> {}
        }
        viewModel.currentTurn.value = when (viewModel.currentTurn.value!!) {
            Turn.X -> Turn.O
            Turn.O -> Turn.X
        }
        if (viewModel.currentTurn.value!! == Turn.X) textViewTurn.setText(R.string.turn_x)
        if (viewModel.currentTurn.value!! == Turn.O) textViewTurn.setText(R.string.turn_o)
    }

    private fun changeState(clusterIndex: Int, cellIndex: Int) = with(binding) {
        val gameField = viewModel.gameField.value!!
        changeCellState(gameField[clusterIndex][cellIndex])
        changeFinishedClusters(clusterIndex, cellIndex)
        changeAllowedClusters(clusterIndex, cellIndex)
        viewModel.gameState.value = tryFinish()
        changeGameFieldState()
    }

    private fun changeCellState(cell: Cell) {
        cell.button.isClickable = false
        cell.changeImage(viewModel.currentTurn.value!!)
        cell.state = turnToCellState(viewModel.currentTurn.value!!)
    }

    private fun turnToCellState(turn: Turn): CellState {
        return when (turn) {
            Turn.X -> CellState.X
            Turn.O -> CellState.O
        }
    }

    private fun changeAllowedClusters(clusterIndex: Int, cellIndex: Int) {
        if (cellIndex in viewModel.finishedClusters.value!!) viewModel.allowedClusters.value =
            ((0..8) subtract viewModel.finishedClusters.value!!).toMutableSet()
        else {
            viewModel.allowedClusters.value!!.clear()
            viewModel.allowedClusters.value!!.add(cellIndex)
        }

    }

    private fun changeFinishedClusters(clusterIndex: Int, cellIndex: Int) {
        val cluster = viewModel.gameField.value!![clusterIndex]
        cluster.tryFinish()
        if (cluster.state != ClusterState.Nothing) {
            viewModel.finishedClusters.value!!.add(clusterIndex)
            cluster.changeAllImages()
        }
    }

    private fun tryFinish(): GameState {
        fun isWin(
            listStates: MutableList<ClusterState>, i: Int, j: Int, k: Int
        ): Boolean {
            if (listStates[i] == ClusterState.Nothing) return false
            return listStates[i] == listStates[j] && listStates[i] == listStates[k]
        }

        fun clusterStateToGameState(
            listStates: MutableList<ClusterState>, index: Int
        ): GameState {
            return when (listStates[index]) {
                ClusterState.X -> GameState.XWinner
                ClusterState.O -> GameState.OWinner
                ClusterState.Draw -> GameState.Draw
                ClusterState.Nothing -> GameState.Nothing
            }
        }

        val listStates = mutableListOf<ClusterState>()
        for (cluster in viewModel.gameField.value!!) listStates.add(cluster.state)
        //Rows
        if (isWin(listStates, 0, 1, 2)) return clusterStateToGameState(listStates, 0)
        if (isWin(listStates, 3, 4, 5)) return clusterStateToGameState(listStates, 3)
        if (isWin(listStates, 6, 7, 8)) return clusterStateToGameState(listStates, 6)
        //Columns
        if (isWin(listStates, 0, 3, 6)) return clusterStateToGameState(listStates, 0)
        if (isWin(listStates, 1, 4, 7)) return clusterStateToGameState(listStates, 1)
        if (isWin(listStates, 2, 5, 8)) return clusterStateToGameState(listStates, 2)
        //Diagonals
        if (isWin(listStates, 0, 4, 8)) return clusterStateToGameState(listStates, 0)
        if (isWin(listStates, 2, 4, 6)) return clusterStateToGameState(listStates, 2)
        if (listStates.all { clusterState -> clusterState != ClusterState.Nothing }) {
            val xWinnerCount = listStates.count { clusterState -> clusterState == ClusterState.X }
            val oWinnerCount = listStates.count { clusterState -> clusterState == ClusterState.O }
            if (xWinnerCount > oWinnerCount) return GameState.XWinner
            if (oWinnerCount > xWinnerCount) return GameState.OWinner
            return GameState.Draw
        }
        return GameState.Nothing
    }

    private fun changeGameFieldState() = with(binding) {
        val gameField = viewModel.gameField.value!!
        if (viewModel.gameState.value!! == GameState.Nothing) {
            gameField.forEach { cluster -> cluster.disableCluster() }
            for (clusterIndex in viewModel.allowedClusters.value!!) {
                gameField[clusterIndex].enableCluster()
            }
        } else {
            gameField.forEach { cluster -> cluster.disableCluster() }
            buttonBack.isEnabled = true
            buttonBack.isVisible = true
        }
    }

    private fun setupGameField() = with(binding) {
        for (bigRow in tableGame.children) {
            if (bigRow is TableRow) {
                for (smallTable in bigRow.children) {
                    if (smallTable is TableLayout) {
                        val listCells = mutableListOf<Cell>()
                        for (smallRow in smallTable.children) {
                            if (smallRow is TableRow) {
                                for (button in smallRow.children) {
                                    if (button is ImageButton) {
                                        listCells.add(Cell(button))
                                    }
                                }
                            }
                        }
                        viewModel.gameField.value!!.add(Cluster(listCells))
                    }
                }
            }
        }
    }

}