package com.example.citygame

import kotlin.math.ceil
import kotlin.math.log2
import kotlin.random.Random

class City(private val delay: Long, private val newCitizenProbability: Double,
           private val deathHandler : () -> Unit , private val newBuilingHandler : (Building) -> Unit) : Thread() {
    var population = 2
    var food = 10
    var wood = 0
    var clay = 0
    var iron = 200
    var alive = true
    var buildings = mutableMapOf<Building,Int>(Building.GATHERERS_HUT to 0,Building.SAWMILL to 0)
    //food,wood,clay,iron


    override fun run() {
        startLife()
    }
    private fun startLife() {
        while (alive) {
            Thread.sleep(delay)
//            food -= log2(population.toDouble()).toInt()
            food -= ceil(log2(population.toDouble())).toInt()
            for ((name,count) in buildings) {
                var list = name.amountOfProducedGoods
                food += list[0] * count
                wood += list[1] * count
                clay += list[2] * count
                iron += list[3] * count
            }
            if (Random.nextDouble() < newCitizenProbability && food > population*2) {
                population++
            }
            if (food <= 0) {
                alive = false
                deathHandler()
            }
        }
    }

    fun buildSomething(building: Building) {
        buildings[building] = buildings.getValue(building).inc()
        wood -= building.costs[1]
        clay -= building.costs[2]
        iron -= building.costs[3]
        newBuilingHandler(building)
    }

    fun canBuild(building: Building) : Boolean {
        return (wood >= building.costs[1]
                && (clay >= building.costs[2]
                && (iron >= building.costs[3])))
    }
}

enum class Building {
    GATHERERS_HUT {
        override val costs = listOf(0,10,0,0)
        override val amountOfProducedGoods = listOf(7,0,0,0)
    },
    SAWMILL {
        override val costs = listOf(0,200,150,170)
        override val amountOfProducedGoods = listOf(0,20,0,0)
    };

    abstract val amountOfProducedGoods: List<Int>
    abstract val costs: List<Int>
}
