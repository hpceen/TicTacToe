package com.hpceen.tictactoe.help_classes

import com.hpceen.tictactoe.states.CellState
import com.hpceen.tictactoe.states.ClusterState
import com.hpceen.tictactoe.states.Turn

//Клетка большого поля (кластер)
//Конструктор принимает на вход список ячеек
//Наследуется от Iterable<T>, для обеспечения функциональности циклов и т.д.
class Cluster(private val listCells: MutableList<Cell>) : Iterable<Cell> {

    //Состояние кластера (по умолчанию "Ничего")
    var state: ClusterState = ClusterState.Nothing

    //Свойство indices (для обеспечения итерирования по списку ячеек)
    val indices: IntRange
        get() = listCells.indices

    //Метод возвращающий итератор для списка ячеек
    override fun iterator(): Iterator<Cell> {
        return listCells.iterator()
    }

    //Оператор для обеспечения доступа к списку по индексу
    operator fun get(cellIndex: Int): Cell {
        return listCells[cellIndex]
    }

    //Попытка проверить завершенность кластера
    fun tryFinish() {
        //Проверка выигрыша в трех клетках i, j и k
        fun isWin(
            listStates: MutableList<CellState>, i: Int, j: Int, k: Int
        ): Boolean {
            if (listStates[i] == CellState.Nothing) return false
            return listStates[i] == listStates[j] && listStates[i] == listStates[k]
        }

        //Перевод статуса ячейки в статус кластера (enum)
        fun cellStateToClusterState(
            listStates: MutableList<CellState>, index: Int
        ): ClusterState {
            return when (listStates[index]) {
                CellState.X -> ClusterState.X
                CellState.O -> ClusterState.O
                CellState.Nothing -> ClusterState.Nothing
            }
        }

        //Создание списка с состояниями ячеек
        val listStates: MutableList<CellState> = mutableListOf()
        for (cell in listCells) {
            listStates.add(cell.state)
        }
        //Проверка всех рядов
        state = if (isWin(listStates, 0, 1, 2)) cellStateToClusterState(listStates, 0)
        else if (isWin(listStates, 3, 4, 5)) cellStateToClusterState(listStates, 3)
        else if (isWin(listStates, 6, 7, 8)) cellStateToClusterState(listStates, 6)
        //Проверка всех столбцов
        else if (isWin(listStates, 0, 3, 6)) cellStateToClusterState(listStates, 0)
        else if (isWin(listStates, 1, 4, 7)) cellStateToClusterState(listStates, 1)
        else if (isWin(listStates, 2, 5, 8)) cellStateToClusterState(listStates, 2)
        //Проверка всех диагоналей
        else if (isWin(listStates, 0, 4, 8)) cellStateToClusterState(listStates, 0)
        else if (isWin(listStates, 2, 4, 6)) cellStateToClusterState(listStates, 2)
        //Проверка на ничью (если у всех ячеек статус != "Ничего", то ничья)
        else if (listStates.all { cellState -> cellState != CellState.Nothing }) ClusterState.Draw
        //Иначе ничего
        else ClusterState.Nothing
    }

    //Выключить кластер (выключить все ячейки кластера)
    fun disableCluster() {
        listCells.forEach { cell -> cell.button.isEnabled = false }
    }

    //Включить кластер (включить все ячейки кластера)
    fun enableCluster() {
        listCells.forEach { cell -> cell.button.isEnabled = true }
    }

    //Изменить все изображения ячеек в кластере (в зависимости от хода)
    fun changeAllImages() {
        when (state) {
            ClusterState.X -> listCells.forEach { cell -> cell.changeImage(Turn.X) }
            ClusterState.O -> listCells.forEach { cell -> cell.changeImage(Turn.O) }
            ClusterState.Draw -> return
            ClusterState.Nothing -> return
        }
    }
}