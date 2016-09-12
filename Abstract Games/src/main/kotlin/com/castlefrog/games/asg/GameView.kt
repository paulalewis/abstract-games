package com.castlefrog.games.asg

interface GameView {

    fun setTitle(title: String)

    fun updateState(x: Int, y: Int, player: Int)
}