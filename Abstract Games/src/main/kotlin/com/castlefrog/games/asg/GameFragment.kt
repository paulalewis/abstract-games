package com.castlefrog.games.asg

import android.app.ActionBar
import android.app.Fragment
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.FrameLayout
import com.castlefrog.agl.Agent
import com.castlefrog.agl.Arbiter
import com.castlefrog.agl.Simulator
import com.castlefrog.agl.TurnType
import com.castlefrog.agl.agents.ExternalAgent
import com.castlefrog.agl.agents.RandomAgent
import com.castlefrog.agl.domains.havannah.HavannahSimulator
import com.castlefrog.agl.domains.hex.HexAction
import com.castlefrog.agl.domains.hex.HexSimulator
import com.castlefrog.agl.domains.hex.HexState
import com.castlefrog.games.asg.model.Domain
import com.castlefrog.games.asg.model.DomainType
import com.castlefrog.games.asg.model.Game
import java.util.*
import java.util.concurrent.Executors

public class GameFragment : Fragment() {

    companion object {
        val ARG_GAME = "game"

        fun newInstance(game: Game): GameFragment {
            val args = Bundle()
            args.putSerializable(ARG_GAME, game)
            val fragment = GameFragment()
            fragment.setArguments(args)
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
        val bundle = savedInstanceState ?: getArguments()
        game = bundle.getSerializable(ARG_GAME) as Game
        val resId = getResources().getIdentifier("help_uri_" + game?.domain?.type?.name()?.toLowerCase(), "string", getActivity().getPackageName())
        helpUri = Uri.parse(getString(resId))
        setHasOptionsMenu(true)

        // TODO - dynamically set agents
        agents.add(ExternalAgent())
        agents.add(ExternalAgent())

        arbiter = createArbiter(game?.domain!!)
        arbiter!!.setOnStateChangeListener(Arbiter.OnEventListener() {
            // TODO - go to next step
            if (arbiter!!.getWorld()?.isTerminalState() == false) {
                arbiter!!.stepAsync()
            }
        })
        arbiter!!.stepAsync()
    }

    private fun createArbiter(domain: Domain) : Arbiter<*, *> {
        when (domain.type) {
            DomainType.HEX -> {
                val simulator = HexSimulator.create(8, TurnType.SEQUENTIAL)
                return Arbiter(simulator.getState(), simulator, agents)
            }
            DomainType.HAVANNAH -> {
                val simulator = HavannahSimulator.create(5, TurnType.SEQUENTIAL);
                return Arbiter(simulator.getState(), simulator, agents)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        getActivity().getActionBar().setTitle(getResources().getString(game?.domain?.type?.nameRes!!))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_game, container, false)
        val gameViewContainer = view.findViewById(R.id.gameViewContainer) as FrameLayout

        // TODO - change look base on game
        hexView = HexGridView(getActivity())
        hexView?.boardSize = 5
        hexView?.boardBackgroundColor = getResources().getColor(android.R.color.darker_gray)
        hexView?.paletteColors?.put(1, Color.RED)
        hexView?.paletteColors?.put(2, Color.BLUE)
        hexView?.setOnHexTouchListener(object : HexGridView.HexTouchListener {
            override fun onHexTouchEvent(x: Int, y: Int, mv: MotionEvent) {
                for (i in 0..arbiter!!.getWorld().getNAgents() - 1) {
                    if (arbiter!!.getWorld().hasLegalActions(i)) {
                        val action = HexAction.valueOf(x, y)
                        if (arbiter!!.getWorld().getLegalActions(i).contains(action)) {
                            val agent = agents.get(i) as ExternalAgent
                            agent.setAction(action)
                            hexView!!.setLocationColor(x, y, i + 1)
                        }
                        break
                    }
                }
            }
        })

        gameViewContainer.addView(hexView)
        return view
    }

    override public fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.getItemId()) {
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