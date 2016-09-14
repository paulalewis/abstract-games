package com.castlefrog.games.asg

import com.castlefrog.agl.Agent
import com.castlefrog.agl.Arbiter
import com.castlefrog.agl.History
import com.castlefrog.agl.agents.ExternalAgent
import com.castlefrog.agl.domains.havannah.HavannahSimulator
import com.castlefrog.agl.domains.hex.HexAction
import com.castlefrog.agl.domains.hex.HexSimulator
import com.castlefrog.agl.domains.hex.HexState
import com.castlefrog.games.asg.model.Domain
import com.castlefrog.games.asg.model.DomainType
import com.castlefrog.games.asg.model.Game
import java.util.*

class GamePresenter(val view: GameView,
                    val resourceManager: ResourceManager,
                    val navigationManager: NavigationManager,
                    val game: Game) {

    private val arbiter: Arbiter<*, *>
    private val agents: MutableList<Agent> = ArrayList()
    private val helpUri: String

    init {
        // TODO - dynamically set agents
        agents.add(ExternalAgent())
        agents.add(ExternalAgent())
        arbiter = createArbiter(game.domain)
        val resId = resourceManager.getStringIdentifier("help_uri_" + resourceManager.getLocalizedString(game.domain.type.nameRes).toLowerCase())
        helpUri = resourceManager.getLocalizedString(resId)
        arbiter.step()
    }

    fun onAction(x: Int, y: Int) {
        for (i in 0..arbiter.world.nAgents - 1) {
            val action = HexAction.valueOf(x, y)
            if (action in arbiter.world.legalActions[i]) {
                val agent = agents[i] as ExternalAgent
                agent.setAction(action)
                view.updateState(x, y, i + 1)
            }
            break
        }
    }

    fun onShow() {
        view.setTitle(resourceManager.getLocalizedString(game.domain.type.nameRes))
    }

    fun onAboutSelected() {
        navigationManager.navigate(helpUri)
    }

    private fun createArbiter(domain: Domain) : Arbiter<*, *> {
        val arbiter = when (domain.type) {
            DomainType.HEX -> {
                val size = domain.params["size"]!!.toInt()
                val pieRule = domain.params["pieRule"]!!.toBoolean()
                val simulator = HexSimulator.create(size, pieRule)
                Arbiter(History(simulator.state), simulator, agents)
            }
            DomainType.HAVANNAH -> {
                val size = domain.params["size"]!!.toInt()
                val pieRule = domain.params["pieRule"]!!.toBoolean()
                val simulator = HavannahSimulator.create(size, pieRule)
                Arbiter(History(simulator.state), simulator, agents)
            }
        }
        arbiter.listener = {
            // update board view
            //val state = arbiter.world.state as HexState
            //hexView!!.setLocationColor()
            // go to next step
            if (!arbiter.world.isTerminalState) {
                arbiter.step()
            }
        }
        return arbiter
    }

}