package com.castlefrog.games.asg

import android.app.ActionBar
import android.app.Fragment
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.castlefrog.agl.Agent
import com.castlefrog.agl.Arbiter
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

    private val executor = Executors.newSingleThreadExecutor()
    private val arbiter: Arbiter<*, *>? = null
    private val agents: List<Agent>? = null
    private var helpUri: Uri? = null
    private var gameType = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bundle = savedInstanceState ?: getArguments()
        gameType = bundle.getString(ARG_GAME_TYPE)
        val actionBar: ActionBar? = getActivity().getActionBar()
        actionBar?.setTitle(gameType)
        //val resId = getResources().getIdentifier("help_uri_" + gameType, "string", getActivity().getPackageName())
        //helpUri = Uri.parse(getString(resId))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_game, container, false)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(ARG_GAME_TYPE, gameType)
    }
}