package _2StateMachine.task2

import _2StateMachine.model.*

interface StateBuilder {
    fun transition(event: String, target: String)
    fun commands(vararg codes: String)
}

interface StateMachineBuilder {
    fun event(code: String): Event
    fun command(code: String): Command
    fun resetEvents(vararg codes: String)

    fun state(code: String, builder: StateBuilder.() -> Unit): State
}

fun stateMachine(start: String, expr: StateMachineBuilder.() -> Unit): StateMachine {
    class StateMachineBuilderImpl(val myMachine: StateMachine) : StateMachineBuilder {
        private val stateCache: MutableMap<String, State> = mutableMapOf(Pair(myMachine.start.code, myMachine.start))
        private val eventCache: MutableMap<String, Event> = mutableMapOf()
        private val commandCache: MutableMap<String, Command> = mutableMapOf()

        override fun event(code: String): Event = eventImpl(code)

        override fun command(code: String): Command = commandImpl(code)

        override fun state(code: String, builder: StateBuilder.() -> Unit): State {
            class StateBuilderImpl(val myState: State) : StateBuilder {
                override fun transition(event: String, target: String) =
                        myState.addTransition(eventImpl(event), stateImpl(target))

                override fun commands(vararg codes: String) = codes.forEach { myState.addCommand(commandImpl(it)) }
            }

            val state = stateImpl(code)
            builder.invoke(StateBuilderImpl(state))
            return state
        }

        override fun resetEvents(vararg codes: String) = codes.forEach { myMachine.addResetEvent(eventImpl(it)) }

        private fun stateImpl(code: String): State = stateCache.getOrPut(code, { State(code) })

        private fun eventImpl(code: String): Event = eventCache.getOrPut(code, { Event(code) })

        private fun commandImpl(code: String): Command = commandCache.getOrPut(code, { Command(code) })
    }

    val machine = StateMachine(State(start))
    expr.invoke(StateMachineBuilderImpl(machine))
    return machine
}
