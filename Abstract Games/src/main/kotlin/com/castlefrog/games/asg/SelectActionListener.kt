package com.castlefrog.games.asg

/**
 * Event triggered when an action is selected.
 */
public trait SelectActionListener<A> {
    public fun onActionSelected(action: A)
}

/**
 * Dummy listener to use instead of null
 */
private class DummySelectActionListener<A> : SelectActionListener<A> {
    override fun onActionSelected(action: A) {}
}
