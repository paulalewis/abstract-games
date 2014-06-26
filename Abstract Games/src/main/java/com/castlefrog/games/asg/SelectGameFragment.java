package com.castlefrog.games.asg;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.List;

public class SelectGameFragment extends Fragment {

    public class GamesAdapter extends BaseAdapter {
        private List<Pair<String, View>> contents;

        public GamesAdapter(List<Pair<String, View>> contents) {
            this.contents = contents;
        }

        public int getCount() {
            return contents.size();
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return 0;
        }

        // create a new ImageView for each item referenced by the Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = contents.get(position).second;
            int width = parent.getWidth() / 4;
            v.setLayoutParams(new GridView.LayoutParams(width, width));
            return v;
        }
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_select_game, container, false);
        GridView games = (GridView) view.findViewById(R.id.games);
        List<Pair<String, View>> contents = new ArrayList<>();
        contents.add(new Pair<String, View>(getString(R.string.hex), new HexView(getActivity())));
        contents.add(new Pair<String, View>(getString(R.string.havannah), new HavannahView(getActivity())));
        games.setAdapter(new GamesAdapter(contents));
        games.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //TODO
            }
        });
        return view;
    }
}
