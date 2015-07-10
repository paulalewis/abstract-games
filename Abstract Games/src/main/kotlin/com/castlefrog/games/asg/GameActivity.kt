package com.castlefrog.games.asg

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.castlefrog.games.asg.model.Game

public class GameActivity : Activity() {

    companion object {
        val ARG_GAME = "game"

        fun navigate(context: Context, game: Game): Unit {
            val intent = Intent(context, javaClass<GameActivity>())
            intent.putExtra(ARG_GAME, game)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        val game : Game = getIntent().getExtras().getSerializable(ARG_GAME) as Game
        val fragment = GameFragment.newInstance(game)
        getFragmentManager().beginTransaction()
                .replace(R.id.container, fragment, null)
                .commit()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        getMenuInflater().inflate(R.menu.game, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.getItemId()) {
            R.id.action_undo_move -> {
                true
            }
            R.id.action_redo_move -> {
                true
            }
            R.id.action_about -> {
                //startActivity(Intent(Intent.ACTION_VIEW, helpUri))
                true
            }
            else -> {
                super.onOptionsItemSelected(item);
            }
        }
    }
}
