package presenters

import models.Edge
import models.Graph
import models.SnapshotKeeper
import models.Snapshot
import views.graphview.UIEdge
import views.graphview.UINode

interface ToolBarView{
    fun HandleToolBar()
}

class DijkstraAlgorithmController(){
    var snapshotKeeper : SnapshotKeeper = SnapshotKeeper()
    var startNode : Int = -1
    var endNode : Int = -1
    var currentStep : Int = 0

    fun initStart(startNode:Int, endNode:Int, snapshots : SnapshotKeeper){
        this.startNode = startNode
        this.endNode = endNode
        this.snapshotKeeper = snapshots
    }

    fun getNextStep():Snapshot?{
        currentStep++
        return snapshotKeeper.getSnapshot(currentStep)
    }

    fun getPreviousStep():Snapshot?{
        if (currentStep<=0) return null
        currentStep--
        return snapshotKeeper.getSnapshot(currentStep)
    }


}

class MainPresenter(
    val graphView: GraphView
):ToolBarView {

    private val nodes = ArrayList<UINode>()
    private val edges = ArrayList<UIEdge>()

    private val dijkstraAlgorithmController = presenters.DijkstraAlgorithmController()

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

    fun startAlgorithm(startNode:UINode, endNode:UINode){ //где хранить конечный и начальный узел

        val gr:ArrayList<Edge> = ArrayList<Edge>()
        for (e in edges){
            gr.add(Edge(nodes.indexOf(e.sourceNode),nodes.indexOf(e.endNode),e.weight.toInt()))
        }

        val graph = Graph(gr)
        graph.dijkstra(nodes.indexOf(startNode)) //прогнали алгоритм
        dijkstraAlgorithmController.initStart(nodes.indexOf(startNode),nodes.indexOf(endNode),graph.getSnapshotHistory())

    }

    fun nextStep(){
        val curStep = dijkstraAlgorithmController.getNextStep()
        for (n in nodes){

        }
    }

    fun previousStep(){
        val curStep = dijkstraAlgorithmController.getPreviousStep()
        for (n in nodes){

        }
    }

    // принимает какой-то флаг (мб enum)
    override fun HandleToolBar(){
        TODO("")
    }
}
