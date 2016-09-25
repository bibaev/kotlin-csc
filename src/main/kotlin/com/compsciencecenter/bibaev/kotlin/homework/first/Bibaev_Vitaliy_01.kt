package com.compsciencecenter.bibaev.kotlin.homework.first

import java.util.*

val CHARACTER_COUNT = 4
val VALID_CHARACTERS = ('A'..'F').asIterable().toList()

data class Score(val positions: Int, val letters: Int)

fun playMastermind(
        secret: String = generateSecret(),
        player: Player = RealPlayer()
) {
    var complete: Boolean
    do {
        val guess = player.guess()
        if (secret.length == guess.length && guess.all { ch -> ch in VALID_CHARACTERS }) {
            val result = score(secret, guess)
            complete = result.positions == CHARACTER_COUNT
            player.receiveEvaluation(complete, result.positions, result.letters)
        } else {
            player.incorrectInput(guess)
            complete = true
        }
    } while (!complete)
}

fun generateSecret(differentLetters: Boolean = true): String {
    val random = Random()
    val resultBuilder = StringBuilder()
    val chars = ArrayList(VALID_CHARACTERS)

    for (i in 0 until CHARACTER_COUNT) {
        val ix: Int = (random.nextDouble() * chars.size).toInt()
        resultBuilder.append(chars[ix])
        if (differentLetters) {
            chars.removeAt(ix)
        }
    }

    return resultBuilder.toString()
}

fun score(secret: String, guess: String): Score {
    val notMatchedPositions: List<Int> = (0 until guess.length).filter { it -> secret[it] != guess[it] }.toList()
    val positions = guess.length - notMatchedPositions.size

    val secretNotMatchedChars: String = secret.slice(notMatchedPositions)
    val guessNotMatchedChars: String = guess.slice(notMatchedPositions)

    var letters = 0
    for (ch in HashSet<Char>(guessNotMatchedChars.toList())) {
        letters += Math.min(secretNotMatchedChars.count { x -> ch == x }, guessNotMatchedChars.count({ x -> ch == x }))
    }

    return Score(positions, letters)
}
