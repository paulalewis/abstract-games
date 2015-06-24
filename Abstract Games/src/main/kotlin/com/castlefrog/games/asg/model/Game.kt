package com.castlefrog.games.asg.model

import java.io.Serializable

data class Game(val name: String, val domain: Domain, val lastMoved: Long) : Serializable

