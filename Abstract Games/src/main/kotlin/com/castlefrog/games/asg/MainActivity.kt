package com.castlefrog.games.asg

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.castlefrog.games.asg.view.MainView
import com.castlefrog.games.asg.viewmodel.MainViewModel

class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainView(
                newGameClickListener = {
                    viewModel.showNewGameSelection()
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