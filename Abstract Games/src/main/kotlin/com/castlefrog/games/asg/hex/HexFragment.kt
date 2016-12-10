package com.castlefrog.games.asg.hex

import android.app.Fragment
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.*
import com.castlefrog.agl.Agent
import com.castlefrog.agl.agents.RandomAgent
import com.castlefrog.agl.domains.hex.HexState
import com.castlefrog.games.asg.HexGridView
import com.castlefrog.games.asg.R
import com.castlefrog.games.asg.hexGridView
import com.castlefrog.games.asg.model.Game
import java.util.*
import org.jetbrains.anko.*

class HexFragment : Fragment(), HexView {

    companion object {
        val ARG_GAME = "game"
        val ARG_AGENTS = "agents"

        fun newInstance(game: Game): HexFragment {
            val args = Bundle()
            args.putSerializable(ARG_GAME, game)
            val fragment = HexFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private var presenter: HexPresenter? = null
    private var hexView: HexGridView? = null
    private var player1Color = Color.BLACK
    private var player2Color = Color.WHITE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bundle = savedInstanceState ?: arguments
        val game = bundle.getSerializable(ARG_GAME) as Game
        setHasOptionsMenu(true)

        val agents = ArrayList<Agent>()
        agents.add(RandomAgent())
        agents.add(RandomAgent())
        presenter = HexPresenter(view = this, game = game)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return UI {
            verticalLayout {
                toolbar {
                    title = resources.getString(R.string.hex)
                    lparams(width = matchParent, height = wrapContent)
                    background = context.getDrawable(R.color.primary)
                    inflateMenu(R.menu.game)
                    setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.action_about -> {
                                activity.startActivity(Intent(Intent.ACTION_VIEW,
                                        Uri.parse(resources.getString(R.string.help_uri_hex))))
                                true
                            }
                            else -> {
                                super.onOptionsItemSelected(item)
                            }
                        }
                    }
                }
                hexView = hexGridView {
                    padding = resources.getDimensionPixelSize(R.dimen.game_margin)
                    size = presenter?.game?.domain!!.params["size"]?.toInt() ?: 0
                    boardBackgroundColor = resources.getColor(android.R.color.darker_gray, null)
                    paletteColors.put(1, player1Color)
                    paletteColors.put(2, player2Color)
                    touchListener = { x, y, mv ->
                        when (mv.action) {
                            MotionEvent.ACTION_UP -> {
                                presenter?.onAction(x, y)
                            }
                        }
                    }
                }
            }
        }.view
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable(ARG_GAME, presenter?.game)
    }

    override fun updateState(state: HexState) {
        for (i in 0..state.boardSize - 1) {
            for (j in 0..state.boardSize - 1) {
                hexView?.setLocationColor(i, j, state.getLocation(i, j))
            }
        }
    }

}