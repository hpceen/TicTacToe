package com.hpceen.tictactoe.help

import androidx.lifecycle.MutableLiveData
import com.hpceen.tictactoe.states.State

//Клетка большого поля (кластер)
//Конструктор принимает на вход список ячеек
//Наследуется от Iterable<T>, для обеспечения функциональности циклов и т.д.
class Cluster(private val listCells: List<Cell>) : Iterable<Cell> {

    //Состояние кластера (по умолчанию "Ничего")
    var state: MutableLiveData<State> = MutableLiveData()

    init {
        listCells.forEachIndexed { index, cell ->
            cell.state.observeForever {
                tryFinishCluster(index)
            }
        }
        //Если поменялось state кластера, то меняем все state ячеек и делаем disable cluster
        state.observeForever { clusterState ->
            listCells.forEach { cell -> cell.changeImage(clusterState) }
        }
        disableCluster()
    }

    //Метод возвращающий итератор для списка ячеек
    override fun iterator(): Iterator<Cell> {
        return listCells.iterator()
    }

    //Оператор для обеспечения доступа к списку по индексу
    operator fun get(cellIndex: Int): Cell {
        return listCells[cellIndex]
    }

    //Попытка проверить завершенность кластера
    private fun tryFinishCluster(cellIndex: Int) {
        val fieldsToCheck = fieldsToCheck(cellIndex)
        var winner: State? = null

        //Возвращает выигрывавшего или null. Проверка идет в трех клетках i, j и k.
        fun winner(
            i: Int, j: Int, k: Int
        ): State? {
            if (!listCells[i].state.isInitialized || !listCells[j].state.isInitialized || !listCells[k].state.isInitialized) return null
            if (listCells[i].state.value == listCells[j].state.value && listCells[i].state.value == listCells[k].state.value) return listCells[i].state.value
            return null
        }

        fieldsToCheck.forEach {
            val currentWinner = winner(it[0], it[1], it[2])
            if (currentWinner != null) winner = currentWinner
        }
        if (winner != null) state.postValue(winner!!)
        if (winner == null && listCells.all { cell -> cell.state.isInitialized }) disableCluster()
    }

    //Выключить кластер (выключить все кнопки кластера)
    fun disableCluster() {
        listCells.forEach { cell -> cell.button.isEnabled = false }
    }

    //Включить кластер (включить все кнопки кластера)
    fun enableCluster() {
        listCells.forEach { cell -> cell.button.isEnabled = true }
    }
}