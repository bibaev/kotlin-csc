package _SurnameName

import bridges.BridgeState
import bridges.BridgeState.CLOSED
import bridges.BridgesInfo
import bridges.SwitchAction
import bridges.SwitchAction.CHANGE
import bridges.SwitchType
import bridges.SwitchType.HALF_BLOCK

// Your solution should live in this folder/package only (rename _SurnameName accordingly.)
// You may add as many subpackages as you need, but the function 'bridgesInfo' below should live in the root _SurnameName package.
// Please DON'T copy the class 'BridgesInfo' and others here.

fun bridgesInfo(init: () -> Unit): BridgesInfo = TODO()

// These functions need receivers to works correctly. The declarations below are only used to have the compiled code.

fun bridge(name: Char, initialState: BridgeState = CLOSED, init: () -> Unit = {}) { TODO() }
fun switch(name: Char, action: SwitchAction = CHANGE, type: SwitchType = HALF_BLOCK) { TODO() }
