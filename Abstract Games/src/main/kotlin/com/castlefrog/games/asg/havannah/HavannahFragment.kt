package com.castlefrog.games.asg.havannah

import android.app.Fragment
import android.os.Bundle
import android.view.*
import com.castlefrog.agl.Agent
import com.castlefrog.agl.agents.RandomAgent
// import com.castlefrog.games.asg.hexGridView
import com.castlefrog.games.asg.model.Game
import java.util.*

class HavannahFragment : Fragment(), HavannahView {

    companion object {
        private const val ARG_GAME = "game"

        fun newInstance(game: Game): HavannahFragment {
            return HavannahFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_GAME, game)
                }
            }
        }
    }

    private var presenter: HavannahPresenter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bundle = savedInstanceState ?: arguments
        val game = bundle.getSerializable(ARG_GAME) as Game
        setHasOptionsMenu(true)

        val agents = ArrayList<Agent>()
        agents.add(RandomAgent())
        agents.add(RandomAgent())
        presenter = HavannahPresenter(view = this,
                game = game,
                agents = agents)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        /*return UI {
            verticalLayout {
                toolbar {
                    title = resources.getString(R.string.havannah)
                    lparams(width = matchParent, height = wrapContent)
                    background = context.getDrawable(R.color.primary)
                    inflateMenu(R.menu.game)
                    setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.action_about -> {
                                activity.startActivity(Intent(Intent.ACTION_VIEW,
                                        Uri.parse(resources.getString(R.string.help_uri_havannah))))
                                true
                            }
                            else -> {
                                super.onOptionsItemSelected(item)
                            }
                        }
                    }
                }
                hexGridView {
                    padding = resources.getDimensionPixelSize(R.dimen.game_margin)
                    size = presenter?.game?.domain!!.params["size"]?.toInt() ?: 0
                    boardBackgroundColor = resources.getColor(android.R.color.darker_gray, null)
                    paletteColors.put(1, Color.RED)
                    paletteColors.put(2, Color.BLUE)
                    touchListener = { x, y, me ->
                        when (me.action) {
                            MotionEvent.ACTION_UP -> {
                                presenter?.onAction(x, y)
                            }
                        }
                    }
                }
            }
        }.view*/
        return null
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(ARG_GAME, presenter?.game)
    }

    override fun updateBoard(locations: List<List<Byte>>) {
        // update view
    }
}