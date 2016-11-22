package _SurnameName

import bridges.*
import bridges.BridgeState.CLOSED
import bridges.SwitchAction.CHANGE
import bridges.SwitchType.HALF_BLOCK

// Your solution should live in this folder/package only (rename _SurnameName accordingly.)
// You may add as many subpackages as you need, but the function 'bridgesInfo' below should live in the root _SurnameName package.
// Please DON'T copy the class 'BridgesInfo' and others here.

fun bridgesInfo(init: BridgesBuilder.() -> Unit): BridgesInfo {
    val bridges = mutableMapOf<Char, Bridge>()
    val switches = mutableMapOf<Char, Switch>()

    object : BridgesBuilder {
        override fun bridge(name: Char, initialState: BridgeState, init: BridgeBuilder.() -> Unit) {
            val bridge = Bridge(name, initialState)
            object : BridgeBuilder {
                override fun switch(name: Char, action: SwitchAction, type: SwitchType) {
                    switches[name] = Switch(name, bridge, action, type)
                }

            }.init()
            bridges[name] = bridge
        }
    }.init()

    return BridgesInfo(bridges, switches)
}

interface BridgesBuilder {
    fun bridge(name: Char, initialState: BridgeState = CLOSED, init: BridgeBuilder.() -> Unit = {})
}

interface BridgeBuilder {
    fun switch(name: Char, action: SwitchAction = CHANGE, type: SwitchType = HALF_BLOCK)
}
