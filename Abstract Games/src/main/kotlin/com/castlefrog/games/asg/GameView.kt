package com.castlefrog.games.asg

import com.castlefrog.games.asg.model.DomainType

interface GameView {

    fun setTitle(domainType: DomainType)

    fun navigateToHelp(domainType: DomainType)

    fun updateState(x: Int, y: Int, player: Int)
}