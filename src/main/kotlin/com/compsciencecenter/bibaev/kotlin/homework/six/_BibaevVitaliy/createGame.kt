package _BibaevVitaliy

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

    private interface Block {
        val isStand: Boolean
        val positions: List<Position>
        fun canMove(direction: Direction, state: Map<Char, BridgeState>): Boolean
        fun move(direction: Direction, state: Map<Char, BridgeState>): Block
        fun cover(position: Position): Boolean
    }

    private val myHeight: Int
    private val myWidth: Int

    private val myPositions: Map<Int, Map<Int, Position>> // all available positions (bridges included)
    private val myPosition2Char: Map<Position, Char>
    private val myDefaultBridgeState: Map<Char, BridgeState>

    private val myPosition2Switch: Map<Position, Switch>
    private val myTarget: Position

    private val myStart: Position
    override val height: Int
        get() = myHeight

    override val width: Int
        get() = myWidth

    /**
     * the only mutable field - block, bridges state
     */
    private val myMutableState: GameState

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
        val bridge2State = mutableMapOf<Char, BridgeState>()
        if (bridgesInfo != null) {
            for ((name, bridge) in bridgesInfo.bridges) {
                for (bridgePosition in findSymbol(name)) {
                    bridge2State[name] = bridge.initialState
                }
            }

            for ((name, switch) in bridgesInfo.switches) {
                for (switchPosition in findSymbol(name)) {
                    switches[switchPosition] = switch
                }
            }
        }

        myDefaultBridgeState = bridge2State
        myPosition2Switch = switches

        myHeight = boardArrays.size
        myWidth = boardArrays.map { x -> x.size }.max()!!

        val start = findSymbol('S')
        assert(start.size == 1, { "Error: start position must be exactly one" })
        myStart = start[0]

        myMutableState = GameState(StandingBlock(myStart), bridge2State)

        val target = findSymbol('T')
        assert(target.size <= 1, { "Error: target position must be not greater then one" })

        myTarget = target.getOrElse(0, { Position(-1, -1) })
    }

    override fun getCellValue(i: Int, j: Int): Char? {
        val position = myPositions[i - 1]?.get(j - 1) ?: return null

        if (myMutableState.block.cover(position)) {
            return 'x'
        }

        val char = myPosition2Char[position]!!
        if (!char.isLetter() || char.isUpperCase()) {
            return char
        }

        val bridgeState = myMutableState.bridgeState(char)
        if (bridgeState == null || bridgeState == BridgeState.CLOSED) {
            return null
        }

        return char
    }

    override fun processMove(direction: Direction) = myMutableState.move(direction)

    override fun hasWon(): Boolean = myMutableState.block.isStand && myMutableState.block.cover(myTarget)

    override fun toString(): String =
            (0 until myHeight).map { i ->
                (0 until myWidth).map { j ->
                    getCellValue(i + 1, j + 1) ?: " "
                }.joinToString(" ")
            }.joinToString("\n", transform = String::trimEnd)


    override fun suggestMoves(): List<Direction>? {
        val previous = mutableMapOf<GameState, Pair<Direction, GameState>>()
        val queue: Queue<GameState> = LinkedList<GameState>()
        val current = myMutableState
        queue.add(current)
        var last: GameState? = null
        while (!queue.isEmpty()) {
            val prev = queue.poll()
            val block = prev.block
            if (block.isStand && block.cover(myTarget)) {
                last = prev
                break
            }

            for (direction in DIRECTIONS.filter { prev.canMove(it) }) {
                val next = prev.copy()
                next.move(direction)
                if (!previous.containsKey(next)) {
                    previous[next] = direction to prev
                    queue.add(next)
                }
            }
        }

        if (last == null) {
            return null
        }

        val result = mutableListOf<Direction>()
        while (last != current) {
            val dir2prev = previous[last] ?: return null

            result += dir2prev.first
            last = dir2prev.second
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

    private inner class StandingBlock(val pos: Position) : Block {
        override val isStand: Boolean = true

        override val positions: List<Position> by lazy { listOf(pos) }

        override fun cover(position: Position): Boolean = pos == position

        override fun canMove(direction: Direction, state: Map<Char, BridgeState>): Boolean {
            val move = toMove(direction)
            val moved = move(pos) ?: return false
            val movedMoved = move(moved)
            return movedMoved != null && !isOnClosedBridge(moved, state) && !isOnClosedBridge(movedMoved, state)
        }

        override fun move(direction: Direction, state: Map<Char, BridgeState>): Block {
            assert(canMove(direction, state), { "cannot make a turn: $pos -> $direction" })
            val move = toMove(direction)
            val fst = move(pos)!!
            val snd = move(fst)!!
            return LyingBlock(fst, snd)
        }

        override fun equals(other: Any?): Boolean = other is StandingBlock && other.pos == pos

        override fun hashCode(): Int = pos.hashCode()

        override fun toString(): String = "pos = $pos"
    }

    private inner class LyingBlock(val firstPosition: Position, val secondPosition: Position) : Block {
        override val isStand: Boolean = false

        override val positions: List<Position> by lazy { listOf(firstPosition, secondPosition) }

        override fun cover(position: Position): Boolean = firstPosition == position || secondPosition == position

        override fun canMove(direction: Direction, state: Map<Char, BridgeState>): Boolean {
            val newPos = getNewPositions(direction) ?: return false
            if (newPos.first == newPos.second) {
                val pos = newPos.first
                if (myPosition2Char[pos] == '.') {
                    return false
                }

                return !isOnClosedBridge(pos, state)
            } else {
                return !isOnClosedBridge(newPos.first, state) && !isOnClosedBridge(newPos.second, state)
            }
        }

        override fun move(direction: Direction, state: Map<Char, BridgeState>): Block {
            assert(canMove(direction, state), { "cannot make a turn: $this -> $direction" })
            val newPos = getNewPositions(direction)!!
            if (newPos.first == newPos.second) {
                return StandingBlock(newPos.first)
            } else {
                return LyingBlock(newPos.first, newPos.second)
            }
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

    private fun isOnClosedBridge(pos: Position, state: Map<Char, BridgeState>): Boolean {
        val char = myPosition2Char[pos] ?: return false
        return state[char] == BridgeState.CLOSED
    }

    private inner class GameState(private var myBlock: Block = StandingBlock(myStart),
                                  private var bridges: MutableMap<Char, BridgeState> = mutableMapOf()) {
        val block: Block
            get() = myBlock

        fun canMove(direction: Direction): Boolean = myBlock.canMove(direction, bridges)

        fun move(direction: Direction) {
            if (!myBlock.canMove(direction, bridges)) {
                myBlock = StandingBlock(myStart)
                bridges = LinkedHashMap(myDefaultBridgeState)
            } else {
                myBlock = myBlock.move(direction, bridges)
                myBlock.positions.forEach { handleSwitch(it) }
            }
        }

        fun bridgeState(char: Char): BridgeState? = bridges[char]

        override fun equals(other: Any?): Boolean {
            if (other is GameState) {
                return myBlock == other.myBlock && bridges == other.bridges
            }

            return false
        }

        override fun hashCode(): Int {
            return Objects.hash(myBlock, bridges)
        }

        fun copy(): GameState = GameState(myBlock, LinkedHashMap(bridges))

        private fun handleSwitch(pos: Position) {
            val switch = myPosition2Switch[pos] ?: return
            if (switch.type == SwitchType.FULL_BLOCK && !myBlock.isStand) {
                return
            }

            val state = bridges[switch.bridge.name]!!
            when (switch.action) {
                SwitchAction.OPEN -> bridges[switch.bridge.name] = BridgeState.OPENED
                SwitchAction.CLOSE -> bridges[switch.bridge.name] = BridgeState.CLOSED
                SwitchAction.CHANGE -> when (state) {
                    BridgeState.OPENED -> bridges[switch.bridge.name] = BridgeState.CLOSED
                    BridgeState.CLOSED -> bridges[switch.bridge.name] = BridgeState.OPENED
                }
            }
        }
    }
}

fun createGame(board: String, bridgesInfo: BridgesInfo? = null): Game {
    return GameImpl(board, bridgesInfo)
}
