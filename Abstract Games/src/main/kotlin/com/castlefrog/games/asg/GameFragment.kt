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

class GameFragment : Fragment(), GameView {

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

    private var presenter: GamePresenter? = null
    private var hexView: HexGridView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bundle = savedInstanceState ?: arguments
        val game = bundle.getSerializable(ARG_GAME) as Game
        setHasOptionsMenu(true)

        presenter = GamePresenter(view = this,
                resourceManager = DefaultResourceManager(activity),
                navigationManager = DefaultNavigationManager(activity),
                game = game)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_game, container, false)
        val gameViewContainer = view.findViewById(R.id.gameViewContainer) as FrameLayout

        // TODO - change look base on game
        hexView = HexGridView(activity)
        hexView?.size = presenter?.game?.domain!!.params["size"]?.toInt() ?: 0
        hexView?.boardBackgroundColor = resources.getColor(android.R.color.darker_gray, null)
        hexView?.paletteColors?.put(1, Color.RED)
        hexView?.paletteColors?.put(2, Color.BLUE)
        hexView?.touchListener = { x, y, mv ->
            when (mv.action) {
                MotionEvent.ACTION_UP -> {
                    presenter?.onAction(x, y)
                }
            }
        }

        gameViewContainer.addView(hexView)
        return view
    }

    override fun onStart() {
        super.onStart()
        presenter?.onShow()
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
                presenter?.onAboutSelected()
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable(ARG_GAME, presenter?.game)
    }

    override fun setTitle(title: String) {
        activity.actionBar?.title = title
    }

    override fun updateState(x: Int, y: Int, player: Int) {
        hexView?.setLocationColor(x, y, player)
    }
}