package com.hpceen.tictactoe

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ConnectionViewModel : ViewModel() {
    var isConnected : MutableLiveData<Boolean> = MutableLiveData(false)
}