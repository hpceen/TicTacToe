package com.hpceen.tictactoe

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TableLayout
import android.widget.TableRow
import androidx.core.view.children
import com.hpceen.tictactoe.databinding.FragmentGameBinding

class Game : ViewBindingFragment<FragmentGameBinding>() {
    private lateinit var viewModel: GameViewModel
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentGameBinding
        get() = FragmentGameBinding::inflate

//    private fun setupGameField() = with(binding) {
//        for (tableRow in tableGame.children) {
//            if (tableRow is TableRow) {
//                for (table in tableRow.children) {
//                    val listCells = mutableListOf<Cell>()
//                    if (table is TableLayout) {
//                        for (row in table.children) {
//                            if (row is TableRow) {
//                                for (button in row.children) {
//                                    if (button is ImageButton) listCells.add(Cell(button))
//                                }
//                            }
//                        }
//                    }
//                    viewModel.gameField.value!!.add(Cluster(listCells))
//                }
//            }
//        }
//    }
//
//    private fun makeMove(cell: Cell) {
//
//    }
//
//    override fun setupView() = with(binding) {
//        viewModel.gameField.value = mutableListOf()
//        setupGameField()
//        for (cluster in viewModel.gameField.value!!) {
//            for (cell in cluster) {
//                cell.setOnClickListener { cell -> makeMove() }
//            }
//        }
//    }

    private fun setupGameField(tableGame: TableLayout) {
        for (tableRow in tableGame.children) {
            if (tableRow is TableRow) {
                for (table in tableRow.children) {
                    if (table is TableLayout) {
                        val listButtons = mutableListOf<ImageButton>()
                        for (row in table.children) {
                            if (row is TableRow) {
                                for (button in row.children) {
                                    if (button is ImageButton) listButtons.add(button)
                                }
                            }
                        }
                        viewModel.gameField.value!!.add(listButtons)
                    }
                }
            }
        }
    }

    private fun changeImage(indexOfTable: Int, indexOfButton: Int) {
        viewModel.gameField.value!![indexOfTable][indexOfButton].setImageResource(if (viewModel.currentTurn.value == Turn.X) R.drawable.cross else R.drawable.circle)
    }

    private fun makeMove(indexOfTable: Int, indexOfButton: Int) {
        viewModel.limitations.value = indexOfButton
        viewModel.gameField.value!![indexOfTable][indexOfButton].isClickable = false
        viewModel.gameFieldCellStates.value!![indexOfTable][indexOfButton] =
            if (viewModel.currentTurn.value == Turn.X) CellState.X else CellState.O
        changeImage(indexOfTable, indexOfButton)
        changeClusterState(indexOfTable)
        changeButtonsState()
        viewModel.currentTurn.value = if (viewModel.currentTurn.value == Turn.X) Turn.O else Turn.X
        setTurnText()
    }

    private fun enableAllButtons() {
        for (table in viewModel.gameField.value!!) {
            for (button in table) {
                button.isEnabled = true
            }
        }
    }

    private fun disableAllButtons() {
        for (table in viewModel.gameField.value!!) {
            for (button in table) {
                button.isEnabled = false
            }
        }
    }

    private fun enableCluster(indexOfTable: Int) {
        for (button in viewModel.gameField.value!![indexOfTable]) {
            button.isEnabled = true
        }
    }

    private fun disableCluster(indexOfTable: Int) {
        for (button in viewModel.gameField.value!![indexOfTable]) {
            button.isEnabled = false
        }
    }


    private fun changeButtonsState() {
        if (viewModel.limitations.value!! in viewModel.wonClusters.value!!) {
            enableAllButtons()
            disableCluster(viewModel.limitations.value!!)
        } else {
            disableAllButtons()
            enableCluster(viewModel.limitations.value!!)
        }
    }

    private fun getClusterWinnerEnumFromCellStateEnum(cellState: CellState): ClusterState {
        return when (cellState) {
            CellState.Nothing -> ClusterState.Draw
            CellState.O -> ClusterState.O
            CellState.X -> ClusterState.X
        }
    }

    private fun clusterWinner(indexOfTable: Int): ClusterState {
        val cluster = viewModel.gameFieldCellStates.value!![indexOfTable]
        if (cluster[0] != CellState.Nothing && cluster[0] == cluster[1] && cluster[0] == cluster[2]) return getClusterWinnerEnumFromCellStateEnum(
            cluster[0]
        )
        if (cluster[3] != CellState.Nothing && cluster[3] == cluster[4] && cluster[3] == cluster[5]) return getClusterWinnerEnumFromCellStateEnum(
            cluster[3]
        )
        if (cluster[6] != CellState.Nothing && cluster[6] == cluster[7] && cluster[6] == cluster[8]) return getClusterWinnerEnumFromCellStateEnum(
            cluster[6]
        )
        if (cluster[0] != CellState.Nothing && cluster[0] == cluster[3] && cluster[0] == cluster[6]) return getClusterWinnerEnumFromCellStateEnum(
            cluster[0]
        )
        if (cluster[1] != CellState.Nothing && cluster[1] == cluster[4] && cluster[1] == cluster[7]) return getClusterWinnerEnumFromCellStateEnum(
            cluster[1]
        )
        if (cluster[2] != CellState.Nothing && cluster[2] == cluster[5] && cluster[2] == cluster[8]) return getClusterWinnerEnumFromCellStateEnum(
            cluster[2]
        )
        if (cluster[0] != CellState.Nothing && cluster[0] == cluster[4] && cluster[0] == cluster[8]) return getClusterWinnerEnumFromCellStateEnum(
            cluster[0]
        )
        if (cluster[2] != CellState.Nothing && cluster[2] == cluster[4] && cluster[2] == cluster[6]) return getClusterWinnerEnumFromCellStateEnum(
            cluster[2]
        )
        if (cluster.all { cell -> cell != CellState.Nothing }) return ClusterState.Draw
        return ClusterState.Nothing
    }

    private fun changeClusterState(indexOfTable: Int) {
        val clusterState = clusterWinner(indexOfTable)
        viewModel.clusterStates.value!![indexOfTable] = clusterState
        when (clusterState) {
            ClusterState.Nothing -> return

            ClusterState.X -> {
                viewModel.wonClusters.value!!.add(indexOfTable)
                for (button in viewModel.gameField.value!![indexOfTable]) {
                    button.setImageResource(R.drawable.cross)
                }
                viewModel.gameFieldCellStates.value!![indexOfTable].replaceAll { CellState.X }
            }

            ClusterState.O -> {
                viewModel.wonClusters.value!!.add(indexOfTable)
                for (button in viewModel.gameField.value!![indexOfTable]) {
                    button.setImageResource(R.drawable.circle)
                }
                viewModel.gameFieldCellStates.value!![indexOfTable].replaceAll { CellState.O }
            }

            ClusterState.Draw -> {
                viewModel.wonClusters.value!!.add(indexOfTable)
            }
        }
        val winner = checkGameWinner()
        if (winner != GameResults.Nothing) {
            disableAllButtons()
            setWinnerText(winner)
        }
    }

    private fun setWinnerText(winner: GameResults) {
        when (winner) {
            GameResults.Nothing -> binding.textViewTurn.setText(R.string.error)
            GameResults.Draw -> binding.textViewTurn.setText(R.string.draw)
            GameResults.XWinner -> binding.textViewTurn.setText(R.string.winner_x)
            GameResults.OWinner -> binding.textViewTurn.setText(R.string.winner_o)
        }
    }

    private fun clusterStateEnumToGameWinnerEnum(clusterState: ClusterState): GameResults {
        return when (clusterState) {
            ClusterState.Nothing -> GameResults.Nothing
            ClusterState.Draw -> GameResults.Nothing
            ClusterState.O -> GameResults.OWinner
            ClusterState.X -> GameResults.XWinner
        }
    }

    private fun checkGameWinner(): GameResults {
//        if (!viewModel.gameFieldCellStates.value!!.flatten()
//                .all { state -> state != CellState.Nothing }
//        ) return GameResults.Draw
        val clusterStates = viewModel.clusterStates.value!!
        if (clusterStates[0] != ClusterState.Nothing && clusterStates[0] == clusterStates[1] && clusterStates[0] == clusterStates[2]) return clusterStateEnumToGameWinnerEnum(
            clusterStates[0]
        )
        if (clusterStates[3] != ClusterState.Nothing && clusterStates[3] == clusterStates[4] && clusterStates[3] == clusterStates[5]) return clusterStateEnumToGameWinnerEnum(
            clusterStates[3]
        )
        if (clusterStates[6] != ClusterState.Nothing && clusterStates[6] == clusterStates[7] && clusterStates[6] == clusterStates[8]) return clusterStateEnumToGameWinnerEnum(
            clusterStates[6]
        )
        if (clusterStates[0] != ClusterState.Nothing && clusterStates[0] == clusterStates[3] && clusterStates[0] == clusterStates[6]) return clusterStateEnumToGameWinnerEnum(
            clusterStates[0]
        )
        if (clusterStates[1] != ClusterState.Nothing && clusterStates[1] == clusterStates[4] && clusterStates[1] == clusterStates[7]) return clusterStateEnumToGameWinnerEnum(
            clusterStates[1]
        )
        if (clusterStates[2] != ClusterState.Nothing && clusterStates[2] == clusterStates[5] && clusterStates[2] == clusterStates[8]) return clusterStateEnumToGameWinnerEnum(
            clusterStates[2]
        )
        if (clusterStates[0] != ClusterState.Nothing && clusterStates[0] == clusterStates[4] && clusterStates[0] == clusterStates[8]) return clusterStateEnumToGameWinnerEnum(
            clusterStates[0]
        )
        if (clusterStates[2] != ClusterState.Nothing && clusterStates[2] == clusterStates[4] && clusterStates[2] == clusterStates[6]) return clusterStateEnumToGameWinnerEnum(
            clusterStates[2]
        )
        if (clusterStates.all { clusterState -> clusterState != ClusterState.Nothing }) {
            val numOfO = clusterStates.count { clusterState -> clusterState == ClusterState.O }
            val numOfX = clusterStates.count { clusterState -> clusterState == ClusterState.X }
            if (numOfX > numOfO) return GameResults.XWinner
            if (numOfO > numOfX) return GameResults.OWinner
            return GameResults.Draw
        }
        return GameResults.Nothing
    }

    private fun setTurnText() = with(binding) {
        if (viewModel.currentTurn.value!! == Turn.X) textViewTurn.setText(R.string.turn_x)
        else textViewTurn.setText(R.string.turn_o)
    }

    override fun setupView() = with(binding) {
        viewModel = GameViewModel()
        viewModel.currentTurn.value = Turn.X
        viewModel.limitations.value = -1
        viewModel.gameField.value = mutableListOf()
        viewModel.gameFieldCellStates.value =
            (MutableList(9) { MutableList(9) { CellState.Nothing } })
        viewModel.wonClusters.value = mutableListOf()
        viewModel.clusterStates.value = MutableList(9) { ClusterState.Nothing }
        setupGameField(tableGame)
        setTurnText()
        val table = viewModel.gameField.value!!
        for (i in viewModel.gameField.value!!.indices) {
            for (j in table[i].indices) {
                val button = table[i][j]
                button.setOnClickListener {
                    makeMove(i, j)
                }
            }
        }
    }
}