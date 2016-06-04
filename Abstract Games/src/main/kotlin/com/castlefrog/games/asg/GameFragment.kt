package com.castlefrog.games.asg

import android.app.Fragment
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.FrameLayout
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

class GameFragment : Fragment() {

    companion object {
        val ARG_GAME = "game"
        val ARG_AGENTS = "agents"

        fun newInstance(game: Game): GameFragment {
            val args = Bundle()
            args.putSerializable(ARG_GAME, game)
            val fragment = GameFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private var arbiter: Arbiter<*, *>? = null
    private val agents: MutableList<Agent> = ArrayList()
    private var game: Game? = null
    private var helpUri: Uri? = null
    private var hexView: HexGridView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bundle = savedInstanceState ?: arguments
        game = bundle.getSerializable(ARG_GAME) as Game
        val resId = resources.getIdentifier("help_uri_" + activity.getString(game?.domain?.type?.nameRes!!).toLowerCase(), "string", activity.packageName)
        helpUri = Uri.parse(getString(resId))
        setHasOptionsMenu(true)

        // TODO - dynamically set agents
        agents.add(ExternalAgent())
        agents.add(ExternalAgent())

        arbiter = createArbiter(game?.domain!!)
        arbiter?.step()
    }

    private fun createArbiter(domain: Domain) : Arbiter<*, *> {
        val arbiter = when (domain.type) {
            DomainType.HEX -> {
                val simulator = HexSimulator.create(8, true)
                Arbiter(History(simulator.state), simulator, agents)
            }
            DomainType.HAVANNAH -> {
                val simulator = HavannahSimulator.create(5, true)
                Arbiter(History(simulator.state), simulator, agents)
            }
        }
        arbiter.listener = {
            // update board view
            val state = arbiter.world.state as HexState;
            //hexView!!.setLocationColor()
            // go to next step
            if (!arbiter.world.isTerminalState) {
                arbiter.step()
            }
        }
        return arbiter
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_game, container, false)
        val gameViewContainer = view.findViewById(R.id.gameViewContainer) as FrameLayout

        // TODO - change look base on game
        hexView = HexGridView(activity)
        hexView?.size = 5
        hexView?.boardBackgroundColor = resources.getColor(android.R.color.darker_gray, null)
        hexView?.paletteColors?.put(1, Color.RED)
        hexView?.paletteColors?.put(2, Color.BLUE)
        hexView?.touchListener = { x, y, mv ->
            when (mv.action) {
                MotionEvent.ACTION_UP -> {
                    for (i in 0..arbiter!!.world.nAgents - 1) {
                        val action = HexAction.valueOf(x, y)
                        if (action in arbiter!!.world.legalActions[i]) {
                            val agent = agents[i] as ExternalAgent
                            agent.setAction(action)
                            hexView!!.setLocationColor(x, y, i + 1)
                        }
                        break
                    }
                }
            }
        }

        gameViewContainer.addView(hexView)
        return view
    }

    override fun onResume() {
        super.onResume()
        activity.actionBar?.title = resources.getString(game?.domain?.type?.nameRes!!)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_undo_move -> {
                true
            }
            R.id.action_redo_move -> {
                true
            }
            R.id.action_about -> {
                startActivity(Intent(Intent.ACTION_VIEW, helpUri))
                true
            }
            else -> {
                super.onOptionsItemSelected(item);
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable(ARG_GAME, game)
    }
}