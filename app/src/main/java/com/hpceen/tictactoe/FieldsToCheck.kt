package com.hpceen.tictactoe

fun fieldsToCheck(index: Int): MutableList<List<Int>> {
    val list: MutableList<List<Int>> = mutableListOf()
    when (index) {
        0 -> {
            list.add(listOf(0, 1, 2))
            list.add(listOf(0, 3, 6))
            list.add(listOf(0, 4, 8))
        }

        1 -> {
            list.add(listOf(0, 1, 2))
            list.add(listOf(1, 4, 7))
        }

        2 -> {
            list.add(listOf(0, 1, 2))
            list.add(listOf(2, 5, 8))
            list.add(listOf(2, 4, 6))
        }

        3 -> {
            list.add(listOf(3, 4, 5))
            list.add(listOf(0, 3, 6))
        }

        4 -> {
            list.add(listOf(3, 4, 5))
            list.add(listOf(1, 4, 7))

            list.add(listOf(0, 4, 8))
            list.add(listOf(2, 4, 6))
        }

        5 -> {
            list.add(listOf(3, 4, 5))
            list.add(listOf(2, 5, 8))
        }

        6 -> {
            list.add(listOf(6, 7, 8))
            list.add(listOf(0, 3, 6))
            list.add(listOf(2, 4, 6))
        }

        7 -> {
            list.add(listOf(6, 7, 8))
            list.add(listOf(1, 4, 7))
        }

        8 -> {
            list.add(listOf(6, 7, 8))
            list.add(listOf(2, 5, 8))
            list.add(listOf(0, 4, 8))
        }

        else -> {}
    }
    return list
}