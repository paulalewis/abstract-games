package com.castlefrog.games.asg.hex

import com.castlefrog.agl.domains.hex.HexAction
import com.castlefrog.agl.domains.hex.HexSimulator
import com.castlefrog.agl.domains.hex.HexState
import com.castlefrog.agl.util.isTerminalState
import com.castlefrog.games.asg.model.Domain
import com.castlefrog.games.asg.model.Game
import java.util.*

class HexPresenter(val view: HexView,
                   val game: Game) {

    private val hexSimulator: HexSimulator
    private val hexState: HexState

    init {
        hexSimulator = createSimulator(game.domain)
        hexState = hexSimulator.initialState
    }

    fun onAction(x: Int, y: Int) {
        val hexAction = HexAction(x.toByte(), y.toByte())
        if (!hexSimulator.isTerminalState(hexState)) {
            val agentTurn = hexState.agentTurn.toInt()
            //val actions = ArrayList<HexAction?>()
            //actions.add(null)
            //actions.add(agentTurn, hexAction)
            val actions = listOf(null, hexAction)
            //if (hexAction in hexSimulator.legalActions[agentTurn]) {
            //    hexSimulator.stateTransition(hexState, actions)
            //    view.updateState(hexSimulator.state)
            //}
        }
    }

    private fun createSimulator(domain: Domain) : HexSimulator {
        val size = domain.params["size"]?.toInt() ?: 1
        val pieRule = domain.params["pieRule"]?.toBoolean() ?: true
        return HexSimulator(size, pieRule)
    }
}
