package com.castlefrog.games.asg;

import android.app.ActionBar;
import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.castlefrog.agl.Agent;
import com.castlefrog.agl.Arbiter;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GameFragment extends Fragment {

    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private Arbiter<?, ?> arbiter;
    private List<Agent> agents;
    private Uri helpUri;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        String gameType = bundle.getString("gameType");
        ActionBar actionBar = getActivity().getActionBar();
        actionBar.setTitle(gameType);
        int resId = getResources().getIdentifier("help_uri_" + gameType, "string", getActivity().getPackageName());
        helpUri = Uri.parse(getString(resId));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_game, container, false);
    }
}
