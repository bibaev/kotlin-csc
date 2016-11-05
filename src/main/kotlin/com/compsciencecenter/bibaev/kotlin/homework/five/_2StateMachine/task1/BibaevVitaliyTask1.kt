package _2StateMachine.task1

import _2StateMachine.model.*


interface StateBuilder {
    fun commands(vararg commands: Command)

    fun transition(event: Event, state: State)
}

fun State.configure(expr: StateBuilder.() -> Unit): Unit {
    class StateBuilderImpl(val myState: State) : StateBuilder {
        override fun commands(vararg commands: Command) {
            for (command in commands) {
                myState.addCommand(command)
            }
        }

        override fun transition(event: Event, state: State) {
            myState.addTransition(event, state)
        }
    }

    expr.invoke(StateBuilderImpl(this))
}
