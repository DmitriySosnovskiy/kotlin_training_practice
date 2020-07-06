package presenters

import models.Edge
import models.Graph
import models.SnapshotKeeper
import models.Snapshot
import views.graphview.UIEdge
import views.graphview.UINode

interface GraphView {
    fun update()
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
    private val graphView: GraphView
) : EventSubscriber {

    init {
        BroadcastPresenter.registerSubscriber(this)
    }

    override fun handleEvent(event: Event) {
    }

    val nodes = ArrayList<UINode>()
    val edges = ArrayList<UIEdge>()

    private val dijkstraAlgorithmController = DijkstraAlgorithmController()

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

    fun startAlgorithm(startNode:UINode, endNode:UINode){ //где хранить конечный и начальный узел

        val gr:ArrayList<Edge> = ArrayList<Edge>()
        for (e in edges){
            gr.add(Edge(nodes.indexOf(e.sourceNode),nodes.indexOf(e.endNode),e.weight.toInt()))
        }

        val graph = Graph(gr)
        graph.dijkstra(nodes.indexOf(startNode)) //прогнали алгоритм
        dijkstraAlgorithmController.initStart(nodes.indexOf(startNode),nodes.indexOf(endNode),graph.getSnapshotHistory())

    }

    private fun snapshotToMap(snap:Snapshot):HashMap<Int,List<String>>{

        val list = snap.getAllInfo().substring(1,snap.getAllInfo().length-1).split("), (")
        val double = HashMap<Int,List<String>>(list.size)
        for (e in list){
            double[list.indexOf(e)] = e.split(",")
        }
        return double
    }

    fun nextStep(){
        val snapMap = snapshotToMap(dijkstraAlgorithmController.getNextStep()!!)
        // snapMap[0][0] - текущий узел
        // snapMap[1+] - список из 3 элементов, где элемент с индексом 0 - номер вершины, 1 - текущее лучшее расстояние до нее, 2 - номер вершины, из которого пришли в текущую

    }

    fun previousStep(){
        val snapMap = snapshotToMap(dijkstraAlgorithmController.getPreviousStep()!!)

    }
}
