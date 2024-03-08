package com.hpceen.tictactoe.help_classes

import com.hpceen.tictactoe.states.CellState
import com.hpceen.tictactoe.states.ClusterState
import com.hpceen.tictactoe.states.Turn

class Cluster(private val listCells: MutableList<Cell>) : Iterable<Cell> {

    var state: ClusterState = ClusterState.Nothing
    val indices: IntRange
        get() = listCells.indices

    override fun iterator(): Iterator<Cell> {
        return listCells.iterator()
    }

    operator fun get(cellIndex: Int): Cell {
        return listCells[cellIndex]
    }

    fun tryFinish() {
        val listStates: MutableList<CellState> = mutableListOf()
        for (cell in listCells) {
            listStates.add(cell.state)
        }
        //Rows
        state = if (isWin(listStates, 0, 1, 2)) cellStateToClusterState(listStates, 0)
        else if (isWin(listStates, 3, 4, 5)) cellStateToClusterState(listStates, 3)
        else if (isWin(listStates, 6, 7, 8)) cellStateToClusterState(listStates, 6)
        //Columns
        else if (isWin(listStates, 0, 3, 6)) cellStateToClusterState(listStates, 0)
        else if (isWin(listStates, 1, 4, 7)) cellStateToClusterState(listStates, 1)
        else if (isWin(listStates, 2, 5, 8)) cellStateToClusterState(listStates, 2)
        //Diagonals
        else if (isWin(listStates, 0, 4, 8)) cellStateToClusterState(listStates, 0)
        else if (isWin(listStates, 2, 4, 6)) cellStateToClusterState(listStates, 2)
        else if (listStates.all { cellState -> cellState != CellState.Nothing }) ClusterState.Draw
        else ClusterState.Nothing

    }

    private fun isWin(
        listStates: MutableList<CellState>, i: Int, j: Int, k: Int
    ): Boolean {
        if (listStates[i] == CellState.Nothing) return false
        return listStates[i] == listStates[j] && listStates[i] == listStates[k]
    }

    private fun cellStateToClusterState(
        listStates: MutableList<CellState>, index: Int
    ): ClusterState {
        return when (listStates[index]) {
            CellState.X -> ClusterState.X
            CellState.O -> ClusterState.O
            CellState.Nothing -> ClusterState.Nothing
        }
    }

    fun disableCluster() {
        listCells.forEach { cell -> cell.button.isEnabled = false }
    }

    fun enableCluster() {
        listCells.forEach { cell -> cell.button.isEnabled = true }
    }

    fun changeAllImages() {
        when (state) {
            ClusterState.X -> listCells.forEach { cell -> cell.changeImage(Turn.X) }
            ClusterState.O -> listCells.forEach { cell -> cell.changeImage(Turn.O) }
            ClusterState.Draw -> return
            ClusterState.Nothing -> return
        }
    }
}