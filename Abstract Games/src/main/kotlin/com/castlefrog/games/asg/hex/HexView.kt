package com.castlefrog.games.asg.hex

import com.castlefrog.agl.domains.hex.HexState

interface HexView {

    fun updateState(state: HexState)

}