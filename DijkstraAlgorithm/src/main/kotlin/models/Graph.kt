package models

import java.util.*
import kotlin.collections.HashMap

class Edge(val v1: Int, val v2: Int, val dist: Int){
    override fun toString() = "($v1, $v2, $dist)"
}

class Vertex(val name: Int) : Comparable<Vertex> {
    var dist = Int.MAX_VALUE  //по умолчанию у всех вершин метка бесконечности
    var previous: Vertex? = null //вершина из которой мы перешли в текущую, нужно для восстановления пути
    val neighbours = HashMap<Vertex, Int>() //все соседи с путями до них

    fun printPath() {
        if (this == previous) {
            print(name)
        }                                                  //вывод вершины, из которой мы пришли в данную
        else if (previous == null) {
            print("$name(unreached)")
        }
        else {
            previous!!.printPath()
            print(" -> $name($dist)")
        }
    }

    override fun compareTo(other: Vertex): Int {
        if (dist == other.dist) return name.compareTo(other.name)
        return dist.compareTo(other.dist)
    }

    override fun toString() :String{
        if (dist == Int.MAX_VALUE)
            return "(-, ${previous?.name})"
        return "($dist, ${previous?.name})"
    }

}

class Graph(
    val edges: List<Edge>  //список ребер
) {
    private val graph = HashMap<Int, Vertex>(edges.size)
    private val snapshotKeeper :SnapshotKeeper? = SnapshotKeeper()

    init {
        //инициализируем ребра
        for (e in edges) {
            if (!graph.containsKey(e.v1))
                graph.put(e.v1, Vertex(e.v1))
            if (!graph.containsKey(e.v2))
                graph.put(e.v2, Vertex(e.v2))
        }

        for (e in edges) {
            graph[e.v1]!!.neighbours.put(graph[e.v2]!!, e.dist)
        }
    }


    //подготавливаем граф и заполняем бинарную кучу
    fun dijkstra(startName: Int) {
        if (!graph.containsKey(startName)) {
            println("Graph doesn't contain start vertex '$startName'")
            return
        }
        val source = graph[startName]
        val q = TreeSet<Vertex>() //работает только если реализован интерфейс comparable

        // set-up vertices
        for (v in graph.values) {
            v.previous = if (v == source) source else null
            v.dist = if (v == source)  0 else Int.MAX_VALUE
            q.add(v)
        }
        this.makeSnapshot(startName)
        dijkstra(q)
    }

    private fun dijkstra(q: TreeSet<Vertex>) {
        while (!q.isEmpty()) {

            val u = q.pollFirst()

            if (u!!.dist == Int.MAX_VALUE) break


            for (a in u.neighbours) {
                val v = a.key

                val alternateDist = u.dist + a.value
                if (alternateDist < v.dist) {

                    //делаем здесь снимки
                    q.remove(v)
                    v.dist = alternateDist
                    v.previous = u
                    q.add(v)
                    this.makeSnapshot(v.name)
                    //надо как-то залочить снимки?
                }
            }
        }
    }


    fun printPath(endName: Int) {
        if (!graph.containsKey(endName)) {
            println("Graph doesn't contain end vertex '$endName'")
            return
        }
        graph[endName]!!.printPath()
        println()
        for (v in graph.values) {
            v.printPath()
            println()
        }
        println()
    }

    private fun makeSnapshot(vertex:Int){
        snapshotKeeper!!.putSnapshot(Snapshot(graph,vertex))
    }

    fun getSnapshotHistory() :SnapshotKeeper?{
        return snapshotKeeper
    }

    fun getEdgesAsString(): String {
        val edgesAsString = StringBuilder("")
        for (e in edges) {
            edgesAsString.append(e.toString()+", ")
        }
        edgesAsString.delete(edgesAsString.length - 2, edgesAsString.length).append("")
        return edgesAsString.toString()
    }

}
