package com.castlefrog.games.asg

import android.app.DialogFragment
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.castlefrog.games.asg.model.Domain
import com.castlefrog.games.asg.model.DomainType
import com.castlefrog.games.asg.model.Game
import com.castlefrog.games.asg.view.HexGridView
import java.util.*

/**
 * Fragment for selected the game type.
 */
class SelectGameTypeFragment : DialogFragment() {
    private var recyclerView: RecyclerView? = null
    private val contents: MutableList<Pair<Domain, View>>

    init {
        contents = ArrayList<Pair<Domain, View>>()
    }

    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        val hexIconView = HexGridView(activity)
        hexIconView.size = 3
        hexIconView.boardBackgroundColor = Color.GRAY
        val hexDomainParams = HashMap<String, String>()
        hexDomainParams.put("size", "6")
        hexDomainParams.put("pieRule", "true")
        val hexDomain = Domain(DomainType.HEX, hexDomainParams)
        contents.add(Pair<Domain, View>(hexDomain, hexIconView))
        val havannahIconView = HexGridView(activity)
        havannahIconView.size = 3
        havannahIconView.boardBackgroundColor = Color.GRAY
        val havannahDomainParams = HashMap<String, String>()
        havannahDomainParams.put("size", "6")
        havannahDomainParams.put("pieRule", "true")
        val havannahDomain = Domain(DomainType.HAVANNAH, havannahDomainParams)
        contents.add(Pair<Domain, View>(havannahDomain, havannahIconView))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_select_game_type, container, false)
        recyclerView = view.findViewById(R.id.recyclerView) as RecyclerView
        recyclerView?.layoutManager = GridLayoutManager(activity, 3)
        recyclerView?.adapter = (SelectGameTypeAdapter(contents, object: SelectGameTypeAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                val domain = contents[position].first
                val game = Game("default", domain, 0)
                GameActivity.navigate(activity, game)
            }
        }))
        return view
    }
}