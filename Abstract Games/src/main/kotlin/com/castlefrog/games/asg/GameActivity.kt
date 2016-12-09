package com.castlefrog.games.asg

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.castlefrog.games.asg.havannah.HavannahFragment
import com.castlefrog.games.asg.hex.HexFragment
import com.castlefrog.games.asg.model.DomainType
import com.castlefrog.games.asg.model.Game
import org.jetbrains.anko.*

class GameActivity : Activity() {

    companion object {
        val ARG_GAME = "game"

        fun navigate(context: Context, game: Game) {
            val intent = Intent(context, GameActivity::class.java)
            intent.putExtra(ARG_GAME, game)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        frameLayout {
            id = R.id.container
            background = context.getDrawable(android.R.color.black)
        }
        val extras = intent.extras
        val game : Game = extras.getSerializable(ARG_GAME) as Game
        val fragment = when (game.domain.type) {
            DomainType.HEX -> HexFragment.newInstance(game)
            DomainType.HAVANNAH -> HavannahFragment.newInstance(game)
        }
        fragmentManager.beginTransaction()
                .replace(R.id.container, fragment, null)
                .commit()
    }

}
