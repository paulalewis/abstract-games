package com.castlefrog.games.asg

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem

public class GameActivity : Activity() {

    companion object {
        val ARG_GAME_TYPE = "gameType"

        fun navigate(context: Context, gameType: String): Unit {
            val intent = Intent(context, javaClass<GameActivity>())
            intent.putExtra(ARG_GAME_TYPE, gameType)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        val fragment = GameFragment.newInstance(getIntent().getExtras().getString(ARG_GAME_TYPE))
        getFragmentManager().beginTransaction()
                .replace(R.id.container, fragment, null)
                .commit()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_game, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item!!.getItemId()

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true
        }

        return super.onOptionsItemSelected(item)
    }
}
