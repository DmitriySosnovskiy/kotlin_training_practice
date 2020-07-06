package models

import java.util.*
import kotlin.collections.HashMap

// Класс ребра (v, v, w)
class Edge(val v1: Int, val v2: Int, val dist: Int){
    override fun toString() = "($v1, $v2, $dist)"
}

/* Класс вершины (v)
 * Наследуется от интерфейса Comparable для
 * вершины имеют общий порядок размещения в TreeSet,
 * который зависит от веса, при совпадении весов
 * вершины сортируются по названию
 */

class Vertex(val name: Int) : Comparable<Vertex> {

    var dist = Int.MAX_VALUE  // Первоначальная дистанция для всех ребер равна inf
    var previous: Vertex? = null // Вершина, из которой мы перешли в текущую
    val neighbours = HashMap<Vertex, Int>() // Хэш-таблица для хранения пар хар. соседей (v, w)

    /* Функция вывода кратчайшего пути
     * если достигнута начльная вершина, она выводится
     * если до начальной верщины невозможно добраться, то соотв. сообщение
     * иначе, рекуррентно выводится путь в виде -> v(w)
     */

    fun printPath(){
        when(previous){
            this -> print(name)
            null -> print("До вершины $name невозможно добраться")
            else -> {
                previous!!.printPath()
                print(" -> $name($dist)")
            }
        }
    }

    override fun compareTo(other: Vertex): Int {
        if (dist == other.dist) return name.compareTo(other.name)
        return dist.compareTo(other.dist)
    }

    override fun toString() :String{
        var pr :String = (previous?.name).toString()
        if (previous == null)
            pr = "n"
        if (dist == Int.MAX_VALUE)
            return "(-, $pr)"
        return "($dist, $pr)"
    }

}

// Класс графа (список ребер)
class Graph(private val edges: List<Edge>){
    // Хэш-таблица вершин (имя, объект), построенная из списка ребер
    private val graph = HashMap<Int, Vertex>(edges.size)

    private val snapshotKeeper: SnapshotKeeper = SnapshotKeeper()

    // Инициализация всех вершин и соседей
    init {
        // Проход для инициализации всех вершин
        for (e in edges) {
            // Если текущей вершины еще нет в graph, то добавить;
            // ключ - имя верщины, значение - объект вершина
            if (!graph.containsKey(e.v1))
                graph.put(e.v1, Vertex(e.v1))
            if (!graph.containsKey(e.v2))
                graph.put(e.v2, Vertex(e.v2))
        }

        // Проход для записи всех соседей
        for (e in edges)
            // Кладет в таблицу соседей (имя соседа, вес ребра до него)
            graph[e.v1]!!.neighbours.put(graph[e.v2]!!, e.dist)

            /* Промежуточный вывод хэш-таблицы соседей вершин
             * print("key = ${e.v2}  value = ")
             * println(graph[e.v1]!!.neighbours.getValue(graph[e.v2]!!))
             */
    }

    /* Запускает алгоритм Дейкстры,
     * используя указанную исходную вершину;
     * устанавливает характеристики графа,
     * заполняет treeSet
     */

    fun dijkstra(startName: Int) {
        if (!graph.containsKey(startName)) {
            println("Граф не содержит стартовую вершину '${startName}'")
            return
        }

        val source: Vertex? = graph[startName] // Объект исходная вершина
        val treeSet: TreeSet<Vertex> = TreeSet<Vertex>() // Отсортированная коллекция вершин в виде бинарной кучи

        // Установка характеритик вершин
        for (v in graph.values) {
            v.previous = if (v == source) source else null
            v.dist = if (v == source) 0 else Int.MAX_VALUE
            treeSet.add(v)
        }
        this.makeSnapshot(startName)
        dijkstra(treeSet)
    }

    // Запускается алгоритм Дейкстры для бинарной кучи вершин
    private fun dijkstra(treeSet: TreeSet<Vertex>) {
        while (!treeSet.isEmpty()) {

            /* В currV кладется вершина с кратчайшей дистанцией;
             * в начале алгоритма это начальная вершина,
             * т.к. в source dist = 0, в ост. inf
             */
            val currV = treeSet.pollFirst()

            // Если расстояние inf, то можно пропустить вершины,
            // т.к. они недостижимы
            if (currV!!.dist == Int.MAX_VALUE)
                break

            // Проверка расстояний до каждого соседа
            for (ngb in currV.neighbours) {

                // Текущий сосед
                val currentNgb = ngb.key
                // Пересчитываем расстояние до текущего соседа
                val alternateDist = currV.dist + ngb.value

                // Если новое расстояние < текущего,
                // то меняем в treeSet расстояние до вершины
                if (alternateDist < currentNgb.dist) {

                    // Делаем здесь снимки
                    treeSet.remove(currentNgb)
                    currentNgb.dist = alternateDist
                    currentNgb.previous = currV
                    treeSet.add(currentNgb)
                    this.makeSnapshot(currentNgb.name)
                    // Надо как-то залочить снимки?
                }
            }
        }
    }

    // Печатает путь от начальной вершины до заданной конечной
    fun printPath(endName: Int) {

        if (!graph.containsKey(endName)) {
            println("Граф не содержит конечную вершину '${endName}'")
            return
        }

        graph[endName]!!.printPath()
        println()

        // Печатает путь от начальной вершины до каждой другой
        /*
         * for (v in graph.values) {
         *     v.printPath()
         *     println()
         * }
         * println()
         */
    }

    /*   Пример использования:
     *   val GRAPH:List<Edge> = listOf(
     *       Edge("a", "b", 7),
     *       Edge("a", "c", 9),
     *       Edge("b", "c", 1)
     *   )
     *
     *   const val START = "a"
     *   const val END = "c"
     *
     *   with (Graph(GRAPH)){
     *       dijkstra(START)
     *       printPath(END)
     *   }
     */

    private fun makeSnapshot(vertex:Int){
        snapshotKeeper.putSnapshot(Snapshot(graph,vertex))
    }

    fun getSnapshotHistory() :SnapshotKeeper{
        return snapshotKeeper
    }

    fun getEdgesAsString(): String {
        val edgesAsString = StringBuilder("")
        for (e in edges)
            edgesAsString.append("${e.toString()}, ")

        edgesAsString.delete(edgesAsString.length - 2, edgesAsString.length).append("")
        return edgesAsString.toString()
    }
}
