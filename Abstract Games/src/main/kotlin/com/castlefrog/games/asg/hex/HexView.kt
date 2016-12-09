package com.castlefrog.games.asg.hex

interface HexView {

    fun clearBoard()

    fun setHex(x: Int, y: Int, player: Int)

    //fun updateBoard(locations: List<List<Byte>>)

}