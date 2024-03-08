package com.hpceen.tictactoe

class Cluster(private val listCells: MutableList<Cell>) : Iterable<Cell> {
    override fun iterator(): Iterator<Cell> {
        return listCells.iterator()
    }

}