package com.castlefrog.games.asg

interface ResourceManager {

    fun getLocalizedString(resId: Int): String

    fun getStringIdentifier(resName: String): Int

}
