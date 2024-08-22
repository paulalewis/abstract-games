package com.castlefrog.games.asg

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import com.castlefrog.games.asg.view.MainView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainView(
                newGameClickListener = {
                    SelectGameTypeFragment().show(fragmentManager, null)
                },
                settingsClickListener = {},
                onSearchListener = {},
                onSearchChangedListener = {},
                onSearchActiveChangedListener = {},
            ) {
                //val gameList = findViewById(R.id.gameList) as RecyclerView
                //gameList.layoutManager = LinearLayoutManager(this)
            }
        }
    }
}