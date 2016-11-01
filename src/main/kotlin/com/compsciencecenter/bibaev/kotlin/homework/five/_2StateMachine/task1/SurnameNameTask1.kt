package _2StateMachine.task1

import _2StateMachine.model.*


interface StateBuilder {

    fun commands(vararg commands: Command)

    fun transition(event: Event, state: State)

}
