package com.castlefrog.games.asg.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Game(val name: String, val domain: Domain, val lastMoved: Long) : Parcelable

