package com.castlefrog.games.asg

import android.app.Activity

class DefaultResourceManager(val activity: Activity): ResourceManager {

    override fun getLocalizedString(resId: Int): String {
        return activity.resources.getString(resId)
    }

    override fun getStringIdentifier(resName: String): Int {
        return activity.resources.getIdentifier(resName, "string", activity.packageName)
    }

}
