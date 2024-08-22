package com.castlefrog.games.asg

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import com.castlefrog.games.asg.havannah.HavannahFragment
import com.castlefrog.games.asg.hex.HexFragment
import com.castlefrog.games.asg.model.DomainType
import com.castlefrog.games.asg.model.Game
import com.castlefrog.games.asg.view.GameView

class GameActivity : AppCompatActivity() {

    companion object {
        private const val ARG_GAME = "game"

        fun navigate(context: Context, game: Game) {
            val intent = Intent(context, GameActivity::class.java)
            intent.putExtra(ARG_GAME, game)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GameView(
                infoClickListener = {},
            ) {
                val game : Game? = intent.extras?.getParcelable(ARG_GAME)
                //val fragment = when (game.domain.type) {
                //    DomainType.HEX -> HexFragment.newInstance(game)
                //    DomainType.HAVANNAH -> HavannahFragment.newInstance(game)
                //}
                //fragmentManager.beginTransaction()
                //    .replace(R.id.container, fragment, null)
                //    .commit()
            }
        }
    }
}
