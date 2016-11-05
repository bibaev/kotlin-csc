package _2StateMachine.task1

import _2StateMachine.model.*


interface StateBuilder {
    fun commands(vararg commands: Command)

    fun transition(event: Event, state: State)

    infix fun Event.leadsTo(state: State)
}

fun State.configure(expr: StateBuilder.() -> Unit): Unit {
    class StateBuilderImpl(val myState: State) : StateBuilder {
        override fun commands(vararg commands: Command) = commands.forEach { myState.addCommand(it) }

        override fun transition(event: Event, state: State) = myState.addTransition(event, state)

        override infix fun Event.leadsTo(state: State): Unit = transition(this, state)
    }

    expr.invoke(StateBuilderImpl(this))
}
