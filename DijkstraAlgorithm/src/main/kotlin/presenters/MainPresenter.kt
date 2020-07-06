package presenters

import models.Edge
import models.SnapshotKeeper
import views.graphview.UIEdge
import views.graphview.UINode

interface GraphView {
    fun update()
}


class MainPresenter(
    private val graphView: GraphView
) : EventSubscriber {

    init {
        BroadcastPresenter.registerSubscriber(this)
    }

    override fun handleEvent(event: Event) {
    }

    val nodes = ArrayList<UINode>()
    val edges = ArrayList<UIEdge>()

    private var snapshotKeeper : SnapshotKeeper? = SnapshotKeeper()

    fun addNode(new:UINode){
        nodes.add(new)
        graphView.update()
    }

    fun addEdge(new:UIEdge){
        edges.add(new)
        graphView.update()
    }

    fun deleteEdge(deleted:UIEdge){
        edges.remove(deleted)
        graphView.update()
    }

    fun deleteNode(deleted:UINode){

        val removableEdges = ArrayList<UIEdge>()
        for (e in edges){
            if (e.sourceNode == deleted || e.endNode == deleted)
                removableEdges.add(e)
        }

        edges.removeAll(removableEdges)
        nodes.remove(deleted)
        graphView.update()
    }

    fun startAlgorithm(){
        val GRAPH:ArrayList<Edge> = ArrayList<Edge>()
        for (e in edges){
            //GRAPH.add(Edge(e.sourceNode,e.endNode,e.weight.toInt()))
        }
    }
}

enum class Commands() {
    ADD_NODE,
    ADD_EDGE,
    DELETE_NODE,
    DELETE_EDGE,
    START_ALGORYTHM

}