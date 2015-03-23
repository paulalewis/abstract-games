package com.castlefrog.games.asg

import android.app.Fragment
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import java.util.ArrayList

/**
 * Fragment for selected the game type.
 */
class SelectGameTypeFragment : Fragment() {
    private var recyclerView: RecyclerView? = null
    private val contents: MutableList<Pair<String, View>>

    init {
        contents = ArrayList<Pair<String, View>>()
    }

    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        contents.add(Pair<String, View>(getString(R.string.hex), HexView(getActivity())))
        contents.add(Pair<String, View>(getString(R.string.havannah), HavannahView(getActivity())))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle?): View {
        var view = inflater.inflate(R.layout.fragment_select_game_type, container, false)
        recyclerView = view.findViewById(R.id.recyclerView) as RecyclerView
        recyclerView?.setLayoutManager(GridLayoutManager(getActivity(), 3))
        recyclerView?.setAdapter(SelectGameTypeAdapter(contents, object: SelectGameTypeAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                val fragment = GameFragment.newInstance(contents.get(position).first)
                getFragmentManager().addOnBackStackChangedListener({
                    if (getFragmentManager().getBackStackEntryCount() == 1) {
                        getActivity().getActionBar().setTitle(R.string.app_name)
                    }
                })
                getFragmentManager().beginTransaction()
                        .add(fragment, "GameFragment")
                        .addToBackStack("GameFragment")
                        .commit()
            }
        }))
        return view;
    }
}