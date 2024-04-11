package com.hpceen.tictactoe

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TableLayout
import android.widget.TableRow
import androidx.core.view.children
import androidx.core.view.isVisible
import com.hpceen.tictactoe.databinding.FragmentGameBinding
import com.hpceen.tictactoe.help_classes.Cell
import com.hpceen.tictactoe.help_classes.Cluster
import com.hpceen.tictactoe.help_classes.ViewBindingFragment
import com.hpceen.tictactoe.states.State

class Game : ViewBindingFragment<FragmentGameBinding>() {
    private var viewModel = GameViewModel()
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentGameBinding
        get() = FragmentGameBinding::inflate

    //Подготовка View
    override fun setupView() = with(binding) {
        setupGameField()
        buttonBack.isEnabled = false
        buttonBack.isVisible = false
        buttonBack.setOnClickListener {
            navController.navigate(GameDirections.actionGameToMainMenu())
        }

        val gameField = viewModel.gameField
        gameField.forEachIndexed { clusterIndex, cluster ->
            cluster.forEachIndexed { cellIndex, cell ->
                cell.button.setOnClickListener {
                    makeMove(clusterIndex, cellIndex)
                }
            }
        }
    }

    //Создание наблюдателей для всех LiveData перменных
    override fun observe() = with(viewModel) {
        //Изменение надписи хода, при изменение хода
        currentTurn.observe(this@Game) {
            binding.turnImage.setImageResource(
                when (it) {
                    State.X -> R.drawable.cross
                    State.O -> R.drawable.circle
                    //Чтобы не вылазило предупреждения (гипотетически недостижимое состояние)
                    null -> R.drawable.ic_launcher_background
                }
            )
        }
        //Изменение надписи, при изменении состояния игры
        gameState.observe(this@Game) {
            binding.textViewTurn.setText(
                when (it) {
                    null -> {
                        binding.turnImage.isVisible = false
                        R.string.draw
                    }

                    else -> R.string.winner
                }
            )
            binding.buttonBack.isEnabled = true
            binding.buttonBack.isVisible = true
            gameField.forEach { cluster -> cluster.disableCluster() }
        }
        //Изменение состояния поля при изменении допустимых для хода кластеров
        allowedClusters.observeForever {
            gameField.forEach { cluster -> cluster.disableCluster() }
            it.forEach { index -> gameField[index].enableCluster() }
        }
        //Логика при изменении состояния клетки и кластера
        gameField.forEachIndexed { clusterIndex, cluster ->
            //При изменении состояния кластера изменяется set завершенных кластеров
            //и производится попытка завершения игры
            cluster.state.observeForever {
                finishedClusters.add(clusterIndex)
                tryFinishGame(clusterIndex)
            }
            cluster.forEachIndexed { cellIndex, cell ->
                //При изменении состояния ячейки изменяется set доступных для хода кластеров
                cell.state.observeForever {
                    allowedClusters.postValue(
                        if (cellIndex in finishedClusters) ((0..8).toSet() subtract finishedClusters)
                        else setOf(cellIndex)
                    )
                }
            }
        }
        //Чтобы начать игру изменяем значение переменной текущего хода
        viewModel.currentTurn.postValue(State.X)
    }

    //Совершить ход
    private fun makeMove(clusterIndex: Int, cellIndex: Int) {
        changeState(clusterIndex, cellIndex)
        nextTurn()
    }

    //Переключение хода
    private fun nextTurn() = with(viewModel) {
        currentTurn.postValue(if (currentTurn.value == State.X) State.O else State.X)
    }

    //Изменение состояния игры (параметры - индекс кластера и индекс ячейки в которую был совершен ход)
    private fun changeState(clusterIndex: Int, cellIndex: Int) = with(viewModel) {
        gameField[clusterIndex][cellIndex].state.postValue(if (currentTurn.value == State.X) State.X else State.O)
    }

    //Попытка завершения игры
    private fun tryFinishGame(clusterIndex: Int) {
        //Список полей для проверки
        //(некая оптимизация, чтобы каждый раз не проверять все выигрышные)
        val fieldsToCheck = fieldsToCheck(clusterIndex)
        var winner: State? = null
        val gameField = viewModel.gameField

        //Возвращает выигрывавшего или null. Проверка идет в трех клетках i, j и k.
        fun winner(
            i: Int, j: Int, k: Int
        ): State? {
            if (!gameField[i].state.isInitialized || !gameField[j].state.isInitialized || !gameField[k].state.isInitialized) return null
            if (gameField[i].state.value == gameField[j].state.value && gameField[i].state.value == gameField[k].state.value) return gameField[i].state.value
            return null
        }

        //Логика выигрыша
        fieldsToCheck.forEach {
            val currentWinner = winner(it[0], it[1], it[2])
            if (currentWinner != null) winner = currentWinner
        }
        if (winner != null) {
            viewModel.gameState.postValue(winner!!)
        }
        if (winner == null && gameField.all { cluster -> cluster.state.isInitialized }) {
            val numberCrossWinner = gameField.count { cluster -> cluster.state.value == State.X }
            val numberCircleWinner = gameField.count { cluster -> cluster.state.value == State.O }
            if (numberCrossWinner == numberCircleWinner) {
                viewModel.gameState.postValue(null)
            }
            if (numberCrossWinner > numberCircleWinner) viewModel.gameState.postValue(State.X)
            else viewModel.gameState.postValue(State.O)
        }
    }

    //Подготовка игрового поля (добавление всех ячеек в список)
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
                        viewModel.gameField.add(Cluster(listCells))
                    }
                }
            }
        }
    }

}