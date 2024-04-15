package com.hpceen.tictactoe.help

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding

//Класс инициализирующий navController, а также binding
abstract class ViewBindingFragment<VB : ViewBinding> : Fragment() {
    protected val navController: NavController by lazy { findNavController() }
    private var bindingRef: VB? = null

    protected val binding: VB
        get() = bindingRef.let { bd ->
            if (bd != null) {
                bd
            } else {
                val newBd = provideBinding(layoutInflater)
                bindingRef = newBd
                newBd
            }
        }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? = binding.root

    protected abstract fun provideBinding(inflater: LayoutInflater): VB
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        observe()
    }

    abstract fun observe()

    abstract fun setupView()

    override fun onDestroyView() {
        super.onDestroyView()
    }

}
