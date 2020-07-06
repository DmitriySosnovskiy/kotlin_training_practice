package presenters

import models.Edge
import models.SnapshotKeeper
import views.graphview.UIEdge
import views.graphview.UINode

interface ViewObserver{
    fun update()
}

class MainPresenter(
    val graphView: GraphView
):ViewObserver {

    private val nodes = ArrayList<UINode>()
    private val edges = ArrayList<UIEdge>()

    private var snapshotKeeper : SnapshotKeeper? = SnapshotKeeper()

    fun addNode(new:UINode){
        if (new !in nodes)
            nodes.add(new)
    }
    fun addEdge(new:UIEdge){
        if (new !in edges)
            edges.add(new)
    }
    fun deleteEdge(deleted:UIEdge){
        edges.remove(deleted)
    }
    fun deleteNode(deleted:UINode){
        if (deleted in nodes){
            for (e in edges){
                if (e.sourceNode ==deleted || e.endNode == deleted)
                    edges.remove(e)
            }
            nodes.remove(deleted)
        }
    }

    fun startAlgorithm(){
        val GRAPH:ArrayList<Edge> = ArrayList<Edge>()
        for (e in edges){
            //GRAPH.add(Edge(e.sourceNode,e.endNode,e.weight.toInt()))
        }
    }

    // принимает какой-то флаг (мб enum)
    override fun update(){
        TODO("")
    }
}

enum class Commands() {
    ADD_NODE,
    ADD_EDGE,
    DELETE_NODE,
    DELETE_EDGE,
    START_ALGORYTHM

}