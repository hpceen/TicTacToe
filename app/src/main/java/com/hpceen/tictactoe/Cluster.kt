package com.hpceen.tictactoe

class Cluster(private val listCells: MutableList<Cell>) : Iterable<Cell> {

    var state: ClusterState = ClusterState.Nothing
        get() = field
        set(value) {
            field = value
        }
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
        if (isWin(listStates, 0, 1, 2)) state = cellStateToClusterState(listStates, 0)
        else if (isWin(listStates, 3, 4, 5)) state = cellStateToClusterState(listStates, 3)
        else if (isWin(listStates, 6, 7, 8)) state = cellStateToClusterState(listStates, 6)
        //Columns
        else if (isWin(listStates, 0, 3, 6)) state = cellStateToClusterState(listStates, 0)
        else if (isWin(listStates, 1, 4, 7)) state = cellStateToClusterState(listStates, 1)
        else if (isWin(listStates, 2, 5, 8)) state = cellStateToClusterState(listStates, 2)
        //Diagonals
        else if (isWin(listStates, 0, 4, 8)) state = cellStateToClusterState(listStates, 0)
        else if (isWin(listStates, 2, 4, 6)) state = cellStateToClusterState(listStates, 2)
        else if (listStates.all { cellState -> cellState != CellState.Nothing }) state =
            ClusterState.Draw
        else state = ClusterState.Nothing

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