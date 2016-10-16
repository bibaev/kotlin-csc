package com.compsciencecenter.bibaev.kotlin.homework.four._3GameOfFifteen

/*
This function should return the parity of the permutation.
true - the permutation is even
false - the permutation is odd
https://en.wikipedia.org/wiki/Parity_of_a_permutation

If the game of fifteen is started with the wrong parity, you can't get the correct result
  (numbers sorted in the right order, empty cell at last).
Thus the initial permutation should be correct.
 */
fun countParity(permutation: List<Int>): Boolean {
    val inversions = permutation
            .mapIndexed { ix, elem -> permutation.subList(ix + 1, permutation.size).count { it < elem } }.sum()
    return inversions == 0 || inversions % 2 == 0
}
