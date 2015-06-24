package com.castlefrog.games.asg

import android.app.ActionBar
import android.app.Fragment
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import com.castlefrog.agl.Agent
import com.castlefrog.agl.Arbiter
import com.castlefrog.agl.TurnType
import com.castlefrog.agl.agents.ExternalAgent
import com.castlefrog.agl.agents.RandomAgent
import com.castlefrog.agl.domains.hex.HexAction
import com.castlefrog.agl.domains.hex.HexSimulator
import com.castlefrog.agl.domains.hex.HexState
import java.util.*
import java.util.concurrent.Executors

public class GameFragment : Fragment() {

    companion object {
        val ARG_GAME_TYPE = "gameType"

        fun newInstance(gameType: String): GameFragment {
            val args = Bundle()
            args.putString(ARG_GAME_TYPE, gameType)
            val fragment = GameFragment()
            fragment.setArguments(args)
            return fragment
        }
    }

    private var arbiter: Arbiter<*, *>? = null
    private val agents: MutableList<Agent> = ArrayList()
    private var gameType = ""
    private var helpUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bundle = savedInstanceState ?: getArguments()
        gameType = bundle.getString(ARG_GAME_TYPE)
        val resId = getResources().getIdentifier("help_uri_" + gameType.toLowerCase(), "string", getActivity().getPackageName())
        helpUri = Uri.parse(getString(resId))
        setHasOptionsMenu(true)

        // TODO - dynamically set agents
        agents.add(RandomAgent())
        agents.add(ExternalAgent())

        /*val simulator = HexSimulator.create(8, TurnType.SEQUENTIAL)
        arbiter = Arbiter<HexState, HexAction>(simulator.getState(), simulator, agents)
        arbiter?.setOnStateChangeListener(Arbiter.OnEventListener(){
            // TODO - go to next step
            if (arbiter?.getWorld()?.isTerminalState() == false) {
                arbiter?.step()
            }
        })
        arbiter?.step()*/
    }

    override fun onResume() {
        super.onResume()
        getActivity().getActionBar().setTitle(gameType)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_game, container, false)
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
        outState.putString(ARG_GAME_TYPE, gameType)
    }
}