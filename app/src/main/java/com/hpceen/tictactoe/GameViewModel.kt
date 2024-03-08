package com.hpceen.tictactoe

import android.widget.ImageButton
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class GameViewModel : ViewModel() {
    var gameField: MutableLiveData<MutableList<MutableList<ImageButton>>> = MutableLiveData()
    var wonClusters: MutableLiveData<MutableList<Int>> = MutableLiveData()
    var gameFieldCellStates: MutableLiveData<MutableList<MutableList<CellState>>> =
        MutableLiveData()
    var currentTurn: MutableLiveData<Turn> = MutableLiveData()
    var limitations: MutableLiveData<Int> = MutableLiveData()
    var clusterStates: MutableLiveData<MutableList<ClusterState>> = MutableLiveData()
//    var gameField: MutableLiveData<MutableList<Cluster>> = MutableLiveData()
}