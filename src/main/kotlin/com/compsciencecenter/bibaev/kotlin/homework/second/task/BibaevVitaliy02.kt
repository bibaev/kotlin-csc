package taxiPark.task

import taxiPark.Driver
import taxiPark.Passenger
import taxiPark.TaxiPark

/*
Если вы не знаете, с какой стороны подступиться к заданию,
можно начать с части про коллекции в kotlin-koans
http://try.kotlinlang.org/#/Kotlin%20Koans/Collections/Introduction/Task.kt.
Там простые задачки на использование конкретных функций.
Их сдавать не надо, там есть ответы.
Домашнее задание по уровню сложности похоже на последние задания в koans,
когда нужно применить несколько функций.
*/

val badCombinations = setOf("bu", "ba", "be")
val vowelLetters = "aeiou"
/*
Задание #0.
Строка (из прописных букв английского алфавита) называется красивой, если выполнены ВСЕ следующие условия:
1. она содержит по крайней мере три гласных буквы (гласные буквы: "aeiou")
2. она содержит по крайней мере одну сдвоенную букву (например, ss в строке "klsst")
3. она не содержит сочетания (подстроки) "bu", "ba" и "be" (даже если они являются частью предыдущих условий)
Напишите функцию, которая проверяет, что строка красивая.
Желательно, чтобы функция была небольшого размера и понятная. Четырех строк достаточно :)
В TestNiceStrings есть примеры с комментариями.
 */
fun String.isNice(): Boolean {
    return badCombinations.all { s -> !this.contains(s) } &&
            this.count { ch -> vowelLetters.contains(ch) } >= 3 &&
            (1 until length).any { ix -> this[ix - 1] == this[ix] }
}

/*
Для всех остальных заданий используются классы TaxiPark, Driver, Passenger и Order, объявленные в файле TaxiPark.kt.
Функции должны быть маленькие и понятные. Можно объявлять столько дополнительных функций, сколько потребуется.
Цель: понятный код. Оптимизировать его пока не надо.
*/

// Задание #1.
// Найти водителей, которые не выполнили ни одного заказа
fun TaxiPark.findFakeDrivers(): Collection<Driver> =
        allDrivers.filter { driver -> !orders.any { order -> driver == order.driver } }

// Задание #2.
// Найти всех клиентов, у которых больше заданного числа поездок
fun TaxiPark.findFaithfulPassengers(minTrips: Int): List<Passenger> =
        allPassengers.filter { client -> orders.count { order -> order.passengers.contains(client) } > minTrips }


// Задание #3.
// Найти всех пассажиров, которых данный водитель возил больше одного раза
fun TaxiPark.findFrequentPassengers(driver: Driver): List<Passenger> =
        allPassengers.filter { client ->
            orders.count { order ->
                order.driver == driver && order.passengers.contains(client)
            } > 1
        }

// Задание #4.
// Найти пассажиров, которые большую часть поездок осуществили со скидками
fun TaxiPark.findSmartPassengers(): Collection<Passenger> =
        allPassengers.filter { client ->
            val all = orders.filter { order -> order.passengers.contains(client) }
            val discount = all.count { order -> order.discount != null && order.discount > 0 }
            discount > (all.size - discount)
        }

// Задание #5.
// Найти самый частый интервал поездок среди 0-9 минут, 10-19 минут, 20-29 минут и т.д.
// Если нет заказов - вернуть null.
fun TaxiPark.findTheMostFrequentTripDuration(): IntRange? {
    fun getRange(duration: Int): IntRange = (duration - (duration % 10)) until (duration - (duration % 10) + 10)

    val maxIntervalEntry = orders.groupBy { order -> getRange(order.duration) }.maxBy { entry -> entry.value.size }
    return maxIntervalEntry?.key
}

// Задание #6.
// Узнать: правда ли, что 20% водителей приносят 80% прибыли?
fun TaxiPark.checkParetoPrinciple(): Boolean {
    val totalProfit = orders.sumByDouble { order -> order.cost }
    val topDriverCount = (allDrivers.size * 0.2).toInt()

    val topDriversProfit = orders.groupBy({ order -> order.driver }, { value -> value.cost })
            .mapValues { entry -> entry.value.sum() }
            .values.sortedDescending()
            .subList(0, topDriverCount).sum()
    return totalProfit * 0.8 < topDriversProfit
}
