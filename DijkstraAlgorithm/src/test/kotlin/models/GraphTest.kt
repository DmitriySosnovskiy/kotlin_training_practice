package models

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.function.Executable
import java.util.List
import java.util.*
import javax.xml.stream.events.StartDocument
import kotlin.collections.HashMap
import kotlin.properties.Delegates
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.TestFactory

internal class GraphTest() {

    lateinit var GRAPH: kotlin.collections.List<Edge>
    var START: Int = 0
    var END: Int = 0

    @org.junit.jupiter.api.BeforeEach
    fun setUp(){
        GRAPH = listOf(
            Edge(1, 2, 7),
            Edge(1, 3, 9),
            Edge(2, 3, 1)
        )
        START = 1
        END = 3
    }

    @org.junit.jupiter.api.AfterEach
    fun tearDown() {
        if(GRAPH.isNotEmpty())
            GRAPH = listOf(
                Edge(1, 1, 0)
            )
    }

    @Test
    fun testCorrectEdges() {
        with(Graph(GRAPH)){
            dijkstra(START)
            assertTrue(edges::isNotEmpty)
            assertEquals(3, edges.size)
        }
    }

    @Test
    fun testCorrectGraph() {
        with(Graph(GRAPH)){
            dijkstra(START)
            assertTrue(graph::isNotEmpty)
            assertEquals(2, graph[1]!!.neighbours.size)
            assertEquals(1, graph[2]!!.neighbours.size)
            assertEquals(0, graph[3]!!.neighbours.size)
            assertTrue{
                graph[1]!!.dist < Int.MAX_VALUE && graph[1]!!.dist >= 0
                graph[2]!!.dist < Int.MAX_VALUE && graph[2]!!.dist >= 0
                graph[3]!!.dist < Int.MAX_VALUE && graph[3]!!.dist >= 0
            }
            assertEquals(0, graph[1]!!.dist)
            assertEquals(8, graph[3]!!.dist) //поменялось минимальное расстояние а процессе алгоритма
            assertEquals(2, graph[3]!!.previous?.name)
            assertEquals(1, graph[2]!!.previous?.name)
        }
    }
}


internal class IncorrectGraphTest() {

    lateinit var GRAPH: kotlin.collections.List<Edge>
    var START: Int = 0
    var END: Int = 0

    @org.junit.jupiter.api.BeforeEach
    fun setUp(){
        START = 1
        END = 3
    }

    @Test
    fun testCorrectDist() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            Edge(1, 2, -3)
        }
        assertEquals("Dist < 0", exception.message)
    }

    @Test
    fun testCorrectVertex() {
        val exceptionVertex1 = assertThrows(IllegalArgumentException::class.java) {
            Edge(1, -2, 3)
        }
        val exceptionVertex2 = assertThrows(IllegalArgumentException::class.java) {
            Edge(-1, 2, 3)
        }
        assertEquals("Value < 0", exceptionVertex1.message)
        assertEquals("Value < 0", exceptionVertex2.message)
    }

    @Test
    fun testCorrectStartVertex() {
        GRAPH = listOf(
            Edge(1, 2, 7),
            Edge(1, 3, 9),
            Edge(2, 3, 1)
        )
        START = -1
        END = 3
        with(Graph(GRAPH)) {
            dijkstra(START)
            assertEquals("Граф не содержит стартовую вершину '${START}'", testOutput)
        }
    }

    @Test
    fun testCorrectEndVertex() {
        GRAPH = listOf(
            Edge(1, 2, 7),
            Edge(1, 3, 9),
            Edge(2, 3, 1)
        )
        START = 1
        END = 4
        with(Graph(GRAPH)) {
            printPath(END)
            assertEquals("Граф не содержит конечную вершину '${END}'", testOutput)
        }
    }
}


internal class IncorrectAutoGraphTest() {

    lateinit var GRAPH: kotlin.collections.List<Edge>
    var START: Int = 0
    var END: Int = 0

    @org.junit.jupiter.api.BeforeEach
    fun setUp(){
        GRAPH = listOf(
            Edge(1, 2, 7),
            Edge(1, 3, 9),
            Edge(2, 3, 1)
        )
    }

    @TestFactory
    fun autoTestCorrectEndVertex() = listOf(
        -1 to "Граф не содержит конечную вершину '-1'",
         2 to "",
         6 to "Граф не содержит конечную вершину '6'"
    ).map { (input, expected) ->
        dynamicTest("Конечная вершина $input: $expected") {
            with(Graph(GRAPH)) {
                START = 1
                printPath(input)
                assertEquals(expected, testOutput)
                testOutput = ""
            }
        }
    }

    @TestFactory
    fun autoTestCorrectStartVertex() = listOf(
        -11 to "Граф не содержит стартовую вершину '-11'",
        1 to "",
        66 to "Граф не содержит стартовую вершину '66'"
    ).map { (input, expected) ->
        dynamicTest("Стартовая вершина $input: $expected") {
            with(Graph(GRAPH)) {
                END = 3
                dijkstra(input)
                assertEquals(expected, testOutput)
                testOutput = ""
            }
        }
    }
}