package presenters

import models.Edge
import models.Graph
import views.graphview.*
import javax.swing.JOptionPane

interface GraphView {
    fun update()
    fun displayDijkstraAlgorithmResult(result: String)
    fun setAlgorithmRunningFlag(isAlgorithmRunning: Boolean)
}

class MainPresenter(
    private val graphView: GraphView
) : EventSubscriber {

    init {
        BroadcastPresenter.registerSubscriber(this)
    }
    private fun printLogs(log:String){
        val logEvent = Event.LogEvent(log)
        BroadcastPresenter.generateEvent(logEvent)
    }

    fun onAlgorithmEndConfirmed() {
        nodes.forEach { it.reset() }
        graphView.setAlgorithmRunningFlag(false)
        graphView.update()
        BroadcastPresenter.generateEvent(Event.AfterAlgorithmEnded)
    }

    override fun handleEvent(event: Event) {
        when (event) {
            is Event.OnStartAlgorithm -> {

                val node: Int? = requestStartNodeNumber()

                if (node != null) {
                    printLogs("Алгоритм запущен")
                    graphView.setAlgorithmRunningFlag(true)
                    BroadcastPresenter.generateEvent(Event.AfterAlgorithmStarted)
                    startAlgorithm(node)
                    graphView.update()
                }
                else {
                    return
                }
            }
            is Event.Clear -> {
                nodes.clear()
                dualEdges.clear()
                edges.clear()
                graphView.setAlgorithmRunningFlag(false)
                graphView.update()
                printLogs("Выполнена очистка сцены")
            }

            is Event.NextStep->{
                nextStep()
            }

            is Event.PreviousStep->{
                previousStep()
            }
            is Event.DownloadGraph->{
                printLogs("Загрузка графа из файла ${event.fileName}")
                downloadGraph(event.fileName)
            }
            is Event.SaveGraph->{
                printLogs("Сохранение графа в файл ${event.fileName}")
                saveGraph(event.fileName)
            }
            is Event.EndAlgorithm-> {
                printLogs("Окончание алгоритма")
                finishAlgorithm()
            }
        }
    }

    private fun requestStartNodeNumber() : Int? {
        val numbers = ArrayList<Int>()
        for(i in 1..nodes.size)
            numbers.add(i)
        val optionsHolder = EndNodeRequestPane(numbers)
        val responseCode =
            JOptionPane.showConfirmDialog(null, optionsHolder, "Исходные данные", JOptionPane.OK_CANCEL_OPTION)

        return when (responseCode) {
            //Нажата "Ок"
            0 -> {
                if (numbers.size == 0) null
                else numbers[optionsHolder.startNodeNumber.selectedIndex]
            }
            else -> {
                null
            }
        }
    }

    val nodes = ArrayList<UINode>()
    val edges = ArrayList<UIEdge>()
    val dualEdges = ArrayList<UIDualEdge>()
    private val dijkstraAlgorithmController = DijkstraAlgorithmController()

    fun addNode(new:UINode){
        nodes.add(new)
        graphView.update()
    }

    fun addEdge(new:UIEdge){
        for (e in edges) {
            if (new.sourceNode == e.sourceNode && new.endNode == e.endNode)
                return
        }

        for(e in edges)
        {
            if(new.sourceNode == e.endNode && new.endNode == e.sourceNode) {
                dualEdges.add(UIDualEdge(new, e))
                edges.remove(e)
                graphView.update()
                return
            }
        }
        edges.add(new)
        graphView.update()
    }

    fun deleteEdge(deleted:UIEdge){

        val isDeleted = edges.remove(deleted)
        if(!isDeleted)
        {
            dualEdges.forEach {
                if(it.edge1 == deleted){
                    edges.add(it.edge2)
                    dualEdges.remove(it)
                    graphView.update()
                    return
                }
                if(it.edge2 == deleted)
                {
                    edges.add(it.edge1)
                    dualEdges.remove(it)
                    graphView.update()
                    return
                }
            }
        }
        else graphView.update()
    }

    fun deleteNode(deleted:UINode){

        val removableEdges = ArrayList<UIEdge>()
        for (e in edges){
            if (e.sourceNode == deleted || e.endNode == deleted)
                removableEdges.add(e)
        }
        edges.removeAll(removableEdges)

        val removableDualEdges = ArrayList<UIDualEdge>()
        dualEdges.forEach {
            if(it.edge1.sourceNode == deleted || it.edge2.sourceNode == deleted) {
                removableDualEdges.add(it)
            }
        }
        dualEdges.removeAll(removableDualEdges)

        nodes.remove(deleted)
        graphView.update()
    }

    private fun isNodeHaveEdges(startNode:Int):Boolean{
        for(e in edges)
            if (nodes.indexOf(e.sourceNode) == startNode || nodes.indexOf(e.endNode) == startNode)
                return true

        for(e in dualEdges)
            if (nodes.indexOf(e.edge1.sourceNode) == startNode || nodes.indexOf(e.edge1.endNode) == startNode || nodes.indexOf(e.edge2.sourceNode) == startNode || nodes.indexOf(e.edge2.endNode) == startNode)
                return true

        return false
    }

    private fun startAlgorithm(startNode:Int){ //где хранить конечный и начальный узел
        //проверка на существование вершины в массиве ребер
        if (!isNodeHaveEdges(startNode)){
            printLogs("Данная вершина не имеет ребер")
            onAlgorithmEndConfirmed()
            return
        }

        val gr= ArrayList<Edge>()
        for (e in edges){
            gr.add(Edge(nodes.indexOf(e.sourceNode),nodes.indexOf(e.endNode),e.weight.toInt()))
        }
        for (e in dualEdges){
            gr.add(Edge(nodes.indexOf(e.edge1.sourceNode),nodes.indexOf(e.edge1.endNode),e.edge1.weight.toInt()))
            gr.add(Edge(nodes.indexOf(e.edge2.sourceNode),nodes.indexOf(e.edge2.endNode),e.edge2.weight.toInt()))
        }

        val graph = Graph(gr)
        graph.dijkstra(startNode-1) //прогнали алгоритм
        dijkstraAlgorithmController.initStart(startNode,graph.getSnapshotHistory(),graph.getPath(startNode)) // здесь принимаю ответ
    }

    private fun updateAllNodes(snapMap:HashMap<Int,List<String>>){
        // snapMap[0][0] - текущий узел
        // snapMap[1+] - список из 3 элементов, где элемент с индексом 0 - номер вершины, 1 - текущее лучшее расстояние до нее, 2 - номер вершины, из которого пришли в текущую
        nodes[snapMap[0]!![0].toInt()].isActive = true

        for (i in 2 until snapMap.size){
            nodes[snapMap[i]!![0].toInt()].bestWay = snapMap[i]!![1]
            nodes[snapMap[i]!![0].toInt()].nodeFrom = snapMap[i]!![2]
        }
    }
    private fun getLogs(snapMap:HashMap<Int,List<String>>):String{

        val logs = StringBuilder("")
        val indexCurNode = snapMap[0]!![0].toInt()
        val isRelax = snapMap[1]!![0].toBoolean()

        logs.append("Ребро: (${snapMap[indexCurNode+2]!![2].toInt()},${indexCurNode+1})\n")

        if (isRelax){
            logs.append("Произошла релаксация\n")
        }
        else{
            logs.append("Не произошла релаксация\n")
        }
        var prevBestWay = ""
        val previousSnapMap = dijkstraAlgorithmController.getPreviousSnap()?.toMap()
        if (previousSnapMap!=null){
            prevBestWay = previousSnapMap[indexCurNode+2]!![1]
        }
        logs.append("Лучший расстояние до узла: ${snapMap[indexCurNode+2]!![1].toInt()}(было $prevBestWay)")
        return logs.toString()
    }

    private fun nextStep(){
        if (!dijkstraAlgorithmController.isNextStepPossible()){
            val logEvent = Event.LogEvent("Следующий шаг невозможен")
            BroadcastPresenter.generateEvent(logEvent)
            return
        }

        val event = Event.LogEvent("Выполнен следующий шаг")
        BroadcastPresenter.generateEvent(event)

        for(n in nodes){
            n.reset()
        }

        val snapMap = dijkstraAlgorithmController.getNextStep()?.toMap() ?:return

        updateAllNodes(snapMap)

        val logEvent = Event.LogEvent(getLogs(snapMap))
        BroadcastPresenter.generateEvent(logEvent)

        graphView.update()
    }

    private fun previousStep(){
        if (!dijkstraAlgorithmController.isPreviousStepPossible()){
            val logEvent = Event.LogEvent("Предыдущий шаг невозможен")
            BroadcastPresenter.generateEvent(logEvent)
            return
        }

        val event = Event.LogEvent("Выполнен предыдущий шаг")
        BroadcastPresenter.generateEvent(event)

        for(n in nodes){
            n.reset()
        }

        val snapMap = dijkstraAlgorithmController.getPreviousStep()?.toMap() ?:return
        updateAllNodes(snapMap)

        val logEvent = Event.LogEvent(getLogs(snapMap))
        BroadcastPresenter.generateEvent(logEvent)

        graphView.update()

    }

    private fun finishAlgorithm(){
        for(n in nodes){
            n.reset()
        }
        val snapMap = dijkstraAlgorithmController.getLast()?.toMap() ?:return
        updateAllNodes(snapMap)
        graphView.displayDijkstraAlgorithmResult(dijkstraAlgorithmController.answer)
        graphView.update()
    }

    private fun downloadGraph(fileName:String) {
        val fileHandler = GraphFileHandler(fileName)
        val graphInfo = fileHandler.downloadGraphInfo() ?: return //ошибка в чтении файла

        nodes.clear()
        edges.clear()
        dualEdges.clear()

        //graphInfo.first - информация о вершинах, graphInfo.second - информация о ребрах
        //инициализируем вершины
        for (n in graphInfo.first!!) {
            addNode(UINode(Coordinate(n[0].toInt(), n[1].toInt())))
        }
        //инициализируем ребра
        if (graphInfo.second != null) //если есть информация о ребрах
            for (e in graphInfo.second!!) {
                addEdge(UIEdge(nodes[e[0].toInt()], nodes[e[1].toInt()], e[2]))
            }
        graphView.update()
    }

    override fun toString():String{
        //создаем граф из строки
        val graphAsString = StringBuilder("")
        for (n in nodes)
            graphAsString.append("($n), ")

        if(graphAsString.isEmpty())
            return  ""//нет узлов

        graphAsString.delete(graphAsString.length - 2, graphAsString.length).append("")
        graphAsString.append("\n")
        for (e in edges)
            graphAsString.append("(${nodes.indexOf(e.sourceNode)}, ${nodes.indexOf(e.endNode)}, ${e.weight}), ")
        for (e in dualEdges){
            graphAsString.append("(${nodes.indexOf(e.edge1.sourceNode)}, ${nodes.indexOf(e.edge1.endNode)}, ${e.edge1.weight}), ")
            graphAsString.append("(${nodes.indexOf(e.edge2.sourceNode)}, ${nodes.indexOf(e.edge2.endNode)}, ${e.edge2.weight}), ")
        }
        if(!(edges.isEmpty() && dualEdges.isEmpty()))
            graphAsString.delete(graphAsString.length - 2, graphAsString.length).append("")
        return graphAsString.toString()
    }

    private fun saveGraph(fileName:String){
        val graphAsString = this.toString()
        if (graphAsString.isEmpty()){
            return
        }
        //Записываем в файл
        val fileHandler = GraphFileHandler(fileName)
        fileHandler.saveGraphInfo(graphAsString)
    }
}
