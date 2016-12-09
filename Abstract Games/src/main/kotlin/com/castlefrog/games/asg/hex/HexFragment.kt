package com.castlefrog.games.asg.hex

import android.app.Fragment
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.*
import com.castlefrog.agl.Agent
import com.castlefrog.agl.agents.RandomAgent
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bundle = savedInstanceState ?: arguments
        val game = bundle.getSerializable(ARG_GAME) as Game
        setHasOptionsMenu(true)

        val agents = ArrayList<Agent>()
        agents.add(RandomAgent())
        agents.add(RandomAgent())
        presenter = HexPresenter(view = this,
                game = game,
                agents = agents)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return UI {
            linearLayout {
                toolbar {
                    title = resources.getString(R.string.hex)
                }
                hexGridView {
                    size = presenter?.game?.domain!!.params["size"]?.toInt() ?: 0
                    boardBackgroundColor = resources.getColor(android.R.color.darker_gray, null)
                    paletteColors.put(1, Color.RED)
                    paletteColors.put(2, Color.BLUE)
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable(ARG_GAME, presenter?.game)
    }

    override fun updateBoard(locations: List<List<Byte>>) {
        for (i in 0..locations.size - 1) {
            for (j in 0..locations[i].size - 1) {
                hexView?.setLocationColor(i, j, locations[i][j].toInt())
            }
        }
    }

}