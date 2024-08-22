package com.castlefrog.games.asg.havannah

import com.castlefrog.agl.Agent
import com.castlefrog.agl.History
import com.castlefrog.agl.domains.havannah.HavannahAction
import com.castlefrog.agl.domains.havannah.HavannahSimulator
import com.castlefrog.agl.domains.havannah.HavannahState
import com.castlefrog.games.asg.model.Domain
import com.castlefrog.games.asg.model.Game

class HavannahPresenter(val view: HavannahView,
                    val game: Game,
                    val agents: List<Agent>) {

    // private val arbiter: Arbiter<HavannahState, HavannahAction>

    init {
        // arbiter = createArbiter(game.domain)
        // arbiter.stateChange.subscribe({
            //view.updateBoard(it.locations)
        // })
    }

    fun onAction(x: Int, y: Int) {
        //for (i in 0..arbiter.world.nAgents - 1) {
        //    val action = HavannahAction.valueOf(x, y)
        //    if (action in arbiter.world.legalActions[i]) {
                //val agent = agents[i] as ExternalAgent
                //agent.setAction(action)
        //    }
        //    break
        //}
    }

    // private fun createArbiter(domain: Domain) : Arbiter<HavannahState, HavannahAction> {
    //     val size = domain.params["size"]?.toInt() ?: 1
    //     val pieRule = domain.params["pieRule"]?.toBoolean() ?: true
    //     val simulator = HavannahSimulator.create(size, pieRule)
    //     return Arbiter(History(simulator.state), simulator, agents)
    // }
}
