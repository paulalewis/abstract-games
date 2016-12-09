package com.castlefrog.games.asg.hex

import com.castlefrog.agl.Agent
import com.castlefrog.agl.Arbiter
import com.castlefrog.agl.History
import com.castlefrog.agl.State
import com.castlefrog.agl.domains.havannah.HavannahSimulator
import com.castlefrog.agl.domains.hex.HexAction
import com.castlefrog.agl.domains.hex.HexSimulator
import com.castlefrog.agl.domains.hex.HexState
import com.castlefrog.games.asg.model.Domain
import com.castlefrog.games.asg.model.DomainType
import com.castlefrog.games.asg.model.Game

class HexPresenter(val view: HexView,
                   val game: Game,
                   val agents: List<Agent>) {

    private val arbiter: Arbiter<HexState, HexAction>

    init {
        arbiter = createArbiter(game.domain)
        arbiter.stateChange.subscribe({ state ->
            view.updateBoard(state.locations.map { it.map { it } })
        })
    }

    fun onAction(x: Int, y: Int) {
        for (i in 0..arbiter.world.nAgents - 1) {
            val action = HexAction.valueOf(x, y)
            if (action in arbiter.world.legalActions[i]) {
                //val agent = agents[i] as ExternalAgent
                //agent.setAction(action)
            }
            break
        }
    }

    private fun createArbiter(domain: Domain) : Arbiter<HexState, HexAction> {
        val size = domain.params["size"]?.toInt() ?: 1
        val pieRule = domain.params["pieRule"]?.toBoolean() ?: true
        val simulator = HexSimulator.create(size, pieRule)
        return Arbiter(History(simulator.state), simulator, agents)
    }

}
