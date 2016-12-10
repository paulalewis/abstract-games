package com.castlefrog.games.asg.hex

import com.castlefrog.agl.domains.hex.HexAction
import com.castlefrog.agl.domains.hex.HexSimulator
import com.castlefrog.games.asg.model.Domain
import com.castlefrog.games.asg.model.Game
import java.util.*

class HexPresenter(val view: HexView,
                   val game: Game) {

    private val hexSimulator: HexSimulator

    init {
        hexSimulator = createSimulator(game.domain)
    }

    fun onAction(x: Int, y: Int) {
        if (!hexSimulator.isTerminalState) {
            val agentTurn = hexSimulator.state.agentTurn.toInt()
            val actions = ArrayList<HexAction?>()
            actions.add(null)
            actions.add(agentTurn, HexAction.valueOf(x, y))
            if (HexAction.valueOf(x, y) in hexSimulator.legalActions[agentTurn]) {
                hexSimulator.stateTransition(actions)
                view.updateState(hexSimulator.state)
            }
        }
    }

    private fun createSimulator(domain: Domain) : HexSimulator {
        val size = domain.params["size"]?.toInt() ?: 1
        val pieRule = domain.params["pieRule"]?.toBoolean() ?: true
        return HexSimulator.create(size, pieRule)
    }

}
