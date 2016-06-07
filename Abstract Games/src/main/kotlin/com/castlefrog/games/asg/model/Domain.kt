package com.castlefrog.games.asg.model

import java.io.Serializable

data class Domain(val type: DomainType, val params: Map<String, String>) : Serializable
