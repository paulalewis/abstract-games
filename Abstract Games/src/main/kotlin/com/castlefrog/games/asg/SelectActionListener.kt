package com.castlefrog.games.asg

import com.castlefrog.agl.Action

/**
 * Event triggered when an action is selected.
 */
public trait SelectActionListener<A> {
    public fun onActionSelected(action: A)
}
