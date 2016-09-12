package com.castlefrog.games.asg

import android.app.Activity
import android.content.Intent
import android.net.Uri

class DefaultNavigationManager(val activity: Activity): NavigationManager {

    override fun navigate(uri: String) {
        activity.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(uri)))
    }

}

