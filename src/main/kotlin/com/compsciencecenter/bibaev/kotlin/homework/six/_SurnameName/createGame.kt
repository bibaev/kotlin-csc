package _SurnameName

import bloxorz.Direction
import bloxorz.Game
import bridges.*
import java.util.*

// Your solution should live in this folder/package only (rename _SurnameName accordingly.)
// You may add as many subpackages as you need, but the function 'createGame' below should live in the root _SurnameName package.
// Please DON'T copy the interface 'Game' here.

class GameImpl(board: String, bridgesInfo: BridgesInfo?) : Game {
    private data class Position(val i: Int, val j: Int)

    companion object {
        val DIRECTIONS: List<Direction> = listOf(Direction.UP, Direction.LEFT, Direction.DOWN, Direction.RIGHT)
    }

    private val myHeight: Int
    private val myWidth: Int

    private val myPositions: Map<Int, MutableMap<Int, Position>> // all available positions (bridges included)
    private val myPosition2Char: Map<Position, Char>

    private val myChar2BridgeState: MutableMap<Char, BridgeState> = mutableMapOf()
    private val myPosition2Switch: Map<Position, Switch>

    private val myTarget: Position
    private val myStart: Position
    private var myBlock: Block

    override val height: Int
        get() = myHeight
    override val width: Int
        get() = myWidth

    init {
        val boardArrays = board.split('\n')
                .map { x -> x.trimEnd().toCharArray().filterIndexed { i, x -> i % 2 == 0 } }
        assert(boardArrays.isNotEmpty(), { "Board cannot be empty" })
        val positionsMap = mutableMapOf<Int, MutableMap<Int, Position>>()
        val pos2Char = mutableMapOf<Position, Char>()
        boardArrays.indices.forEach { i ->
            run {
                val nestedMap = mutableMapOf<Int, Position>()
                boardArrays[i].indices.forEach { j ->
                    run {
                        val pos = Position(i, j)
                        val ch = boardArrays[i][j]
                        if (ch != ' ') {
                            pos2Char[pos] = boardArrays[i][j]
                            nestedMap[j] = pos
                        }
                    }
                }
                positionsMap[i] = nestedMap
            }
        }
        myPositions = positionsMap
        myPosition2Char = pos2Char

        val switches = mutableMapOf<Position, Switch>()
        if (bridgesInfo != null) {
            for ((name, bridge) in bridgesInfo.bridges) {
                for (bridgePosition in findSymbol(name)) {
                    myChar2BridgeState[name] = bridge.initialState
                }
            }

            for ((name, switch) in bridgesInfo.switches) {
                for (switchPosition in findSymbol(name)) {
                    switches[switchPosition] = switch
                }
            }
        }
        myPosition2Switch = switches
        myHeight = boardArrays.size
        myWidth = boardArrays.map { x -> x.size }.max()!!
        val start = findSymbol('S')
        assert(start.size == 1, { "Error: start position must be exactly one" })
        myStart = start[0]
        myBlock = StandingBlock(myStart)
        val target = findSymbol('T')
        assert(target.size <= 1, { "Error: target position must be not greater then one" })
        if (target.isEmpty()) {
            myTarget = Position(-1, -1)
        } else {
            myTarget = target[0]
        }
    }

    override fun getCellValue(i: Int, j: Int): Char? {
        val position = myPositions[i - 1]?.get(j - 1) ?: return null

        if (myBlock.cover(position)) {
            return 'x'
        }

        val char = myPosition2Char[position]!!
        if (!char.isLetter() || char == 'S' || char == 'T') {
            return char
        }

        if (char.isLowerCase()) {
            val state = myChar2BridgeState[char]!!
            if (state == BridgeState.CLOSED) {
                return null
            } else {
                return char
            }
        }

        return char
    }

    override fun processMove(direction: Direction) {
        if (!myBlock.canMove(direction, myChar2BridgeState)) {
            myBlock = StandingBlock(myStart)
        } else {
            myBlock = myBlock.move(direction)
            myBlock.updateBridgesStates(myChar2BridgeState)
        }
    }

    override fun hasWon(): Boolean = myBlock.isStand && myBlock.cover(myTarget)

    override fun toString(): String =
            (0 until myHeight).map { i ->
                (0 until myWidth).map { j ->
                    getCellValue(i + 1, j + 1) ?: " "
                }.joinToString(" ")
            }.joinToString("\n", transform = String::trimEnd)


    override fun suggestMoves(): List<Direction>? {
        data class Node(val block: Block, val state: Map<Char, BridgeState>)

        val previous = mutableMapOf<Node, Pair<Direction, Node>>()
        val queue: Queue<Node> = LinkedList<Node>()
        val current = Node(myBlock, myChar2BridgeState)
        queue.add(current)
        while (!queue.isEmpty()) {
            val prev = queue.poll()
            val (block, state) = prev
            if (block.isStand && block.cover(myTarget)) {
                break
            }

            for (direction in DIRECTIONS.filter { block.canMove(it, state) }) {
                val nextBlock = block.move(direction)
                val newBridgesState = HashMap<Char, BridgeState>(state)
                nextBlock.updateBridgesStates(newBridgesState)
                val next = Node(nextBlock, newBridgesState)
                if (!previous.containsKey(next)) {
                    previous[next] = direction to prev
                    queue.add(next)
                }
            }
        }

        var prev = (previous.filterKeys { it.block == StandingBlock(myTarget) }.toList().getOrNull(0)
                ?: return null).first

        val result = mutableListOf<Direction>()
        while (prev != current) {
            val dir2prev = previous[prev] ?: return null

            result += dir2prev.first
            prev = dir2prev.second
        }

        return result.asReversed()
    }

    private fun findSymbol(sym: Char): List<Position> = myPosition2Char.filterValues { x -> x == sym }.keys.toList()

    private fun toMove(direction: Direction): (Position) -> Position? {
        when (direction) {
            Direction.UP -> return { up(it) }
            Direction.DOWN -> return { down(it) }
            Direction.RIGHT -> return { right(it) }
            Direction.LEFT -> return { left(it) }
        }
    }

    private fun up(pos: Position): Position? = myPositions[pos.i - 1]?.get(pos.j)
    private fun left(pos: Position): Position? = myPositions[pos.i]?.get(pos.j - 1)
    private fun right(pos: Position): Position? = myPositions[pos.i]?.get(pos.j + 1)
    private fun down(pos: Position): Position? = myPositions[pos.i + 1]?.get(pos.j)

    private interface Block {
        val isStand: Boolean
        fun canMove(direction: Direction, state: Map<Char, BridgeState>): Boolean
        fun move(direction: Direction): Block
        fun cover(position: Position): Boolean

        fun updateBridgesStates(state: MutableMap<Char, BridgeState>)
    }

    private inner class StandingBlock(val pos: Position) : Block {
        override val isStand: Boolean = true

        override fun cover(position: Position): Boolean = pos == position

        override fun canMove(direction: Direction, state: Map<Char, BridgeState>): Boolean {
            val move = toMove(direction)
            val moved = move(pos) ?: return false
            val movedMoved = move(moved)
            return movedMoved != null && !checkClosedBridges(moved, state) && !checkClosedBridges(movedMoved, state)
        }

        override fun move(direction: Direction): Block {
            val move = toMove(direction)
            val fst = move(pos)!!
            val snd = move(fst)!!
            return LyingBlock(fst, snd)
        }

        override fun updateBridgesStates(state: MutableMap<Char, BridgeState>) {
            handleSwitch(pos, true, state)
        }

        override fun equals(other: Any?): Boolean = other is StandingBlock && other.pos == pos

        override fun hashCode(): Int = pos.hashCode()

        override fun toString(): String = "pos = $pos"
    }

    private inner class LyingBlock(val firstPosition: Position, val secondPosition: Position) : Block {
        override val isStand: Boolean = false

        override fun cover(position: Position): Boolean = firstPosition == position || secondPosition == position

        override fun canMove(direction: Direction, state: Map<Char, BridgeState>): Boolean {
            val newPos = getNewPositions(direction) ?: return false
            // No closed bridges
            if (newPos.first == newPos.second) {
                val pos = newPos.first
                if (myPosition2Char[pos] == '.') {
                    return false
                }

                return !checkClosedBridges(pos, state)
            } else {
                return !checkClosedBridges(newPos.first, state) && !checkClosedBridges(newPos.second, state)
            }
        }

        override fun move(direction: Direction): Block {
            val newPos = getNewPositions(direction)!!
            if (newPos.first == newPos.second) {
                return StandingBlock(newPos.first)
            } else {
                return LyingBlock(newPos.first, newPos.second)
            }
        }

        override fun updateBridgesStates(state: MutableMap<Char, BridgeState>) {
            handleSwitch(firstPosition, false, state)
            handleSwitch(secondPosition, false, state)
        }

        override fun equals(other: Any?): Boolean = other is LyingBlock
                && other.firstPosition == firstPosition
                && other.secondPosition == secondPosition

        override fun hashCode(): Int = Objects.hash(firstPosition, secondPosition)

        override fun toString(): String = "fst = $firstPosition, snd = $secondPosition"

        private fun getNewPositions(direction: Direction): Pair<Position, Position>? {
            val move = toMove(direction)
            if (firstPosition == move(secondPosition)) {
                val pos = move(firstPosition)
                if (pos != null) {
                    return pos to pos
                }
            } else if (secondPosition == move(firstPosition)) {
                val pos = move(secondPosition)
                if (pos != null) {
                    return pos to pos
                }
            } else {
                val fstPos = move(firstPosition)
                val sndPos = move(secondPosition)
                if (fstPos != null && sndPos != null) {
                    return fstPos to sndPos
                }
            }
            return null
        }
    }

    private fun checkClosedBridges(pos: Position, state: Map<Char, BridgeState>): Boolean {
        val char = myPosition2Char[pos] ?: return false
        return state[char] == BridgeState.CLOSED
    }

    private fun handleSwitch(pos: Position, isStand: Boolean, bridgesState: MutableMap<Char, BridgeState>) {
        val switch = myPosition2Switch[pos] ?: return
        if (switch.type == SwitchType.FULL_BLOCK && !isStand) {
            return
        }

        val state = bridgesState[switch.bridge.name]!!
        when (switch.action) {
            SwitchAction.OPEN -> bridgesState[switch.bridge.name] = BridgeState.OPENED
            SwitchAction.CLOSE -> bridgesState[switch.bridge.name] = BridgeState.CLOSED
            SwitchAction.CHANGE -> when (state) {
                BridgeState.OPENED -> bridgesState[switch.bridge.name] = BridgeState.CLOSED
                BridgeState.CLOSED -> bridgesState[switch.bridge.name] = BridgeState.OPENED
            }
        }
    }
}

fun createGame(board: String, bridgesInfo: BridgesInfo? = null): Game {
    return GameImpl(board, bridgesInfo)
}
