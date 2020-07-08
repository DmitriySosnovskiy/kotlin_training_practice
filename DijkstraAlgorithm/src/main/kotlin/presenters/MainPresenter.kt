package presenters

import models.Edge
import models.Graph
import models.SnapshotKeeper
import models.Snapshot
import views.graphview.*
import javax.swing.JOptionPane
import java.io.File
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.StandardOpenOption

interface GraphView {
    fun update()
    fun displayDijkstraAlgorithmResult(result: String)
    fun setAlgorithmRunningFlag(isAlgorithmRunning: Boolean)
}

class DijkstraAlgorithmController(){
    var snapshotKeeper : SnapshotKeeper = SnapshotKeeper()
    var startNode : Int = -1
    var currentStep : Int = 0
    var answer:String = ""
    fun initStart(startNode:Int,snapshots : SnapshotKeeper, answer:String){
        this.startNode = startNode-1
        this.snapshotKeeper = snapshots
        this.answer = answer
        currentStep = -1
    }

    fun getNextStep():Snapshot?{
        if (currentStep>=snapshotKeeper.getSize()-1) return null
        currentStep++
        return snapshotKeeper.getSnapshot(currentStep)
    }

    fun getPreviousStep():Snapshot?{
        if (currentStep<=0) return null
        currentStep--
        return snapshotKeeper.getSnapshot(currentStep)
    }

    fun getLast():Snapshot?{
        return snapshotKeeper.getSnapshot(snapshotKeeper.getSize()-1)
    }

    fun isNextStepPossible():Boolean{
        if(currentStep>=snapshotKeeper.getSize()-1) return false
        return true
    }
    fun isPreviousStepPossible():Boolean{
        if(currentStep<=0) return false
        return true
    }


}


class MainPresenter(
    private val graphView: GraphView
) : EventSubscriber {

    init {
        BroadcastPresenter.registerSubscriber(this)
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
                    val logEvent = Event.LogEvent("Алгоритм запущен")
                    BroadcastPresenter.generateEvent(logEvent)
                    graphView.setAlgorithmRunningFlag(true)
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

                val logEvent = Event.LogEvent("Выполнена очистка сцены")
                BroadcastPresenter.generateEvent(logEvent)
            }

            is Event.NextStep->{
               //val logEvent = Event.LogEvent("Выполнен переход на следующий шаг алгоритма")
               // BroadcastPresenter.generateEvent(logEvent)
                nextStep()
            }

            is Event.PreviousStep->{
               // val logEvent = Event.LogEvent("Выполнен переход на предыдущий шаг алгоритма")
               // BroadcastPresenter.generateEvent(logEvent)
                previousStep()
            }
            is Event.DownloadGraph->{
                val logEvent = Event.LogEvent("Загрузка графа из файла ${event.fileName}")
                BroadcastPresenter.generateEvent(logEvent)
                downloadGraph(event.fileName)
            }
            is Event.SaveGraph->{
                val logEvent = Event.LogEvent("Сохранение графа в файл ${event.fileName}")
                BroadcastPresenter.generateEvent(logEvent)
                saveGraph(event.fileName)
            }
            is Event.EndAlgorithm-> {
                val logEvent = Event.LogEvent("Окончание алгоритма")
                BroadcastPresenter.generateEvent(logEvent)
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

    fun startAlgorithm(startNode:Int){ //где хранить конечный и начальный узел


        //проверка на существование вершины в массиве ребер

        var flag = false

        for(e in edges){
            if (nodes.indexOf(e.sourceNode) == startNode || nodes.indexOf(e.endNode) == startNode){
                flag = true
                break
            }
        }
        if (!flag) {
            val logEvent = Event.LogEvent("Данная вершина не имеет ребер")
            BroadcastPresenter.generateEvent(logEvent)
            graphView.setAlgorithmRunningFlag(false)
            for(n in nodes){
                n.reset()
            }
            graphView.update()
            return
        }


        val gr:ArrayList<Edge> = ArrayList<Edge>()
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

    private fun snapshotToMap(snap:Snapshot):HashMap<Int,List<String>>{
        val list = snap.getAllInfo().substring(1,snap.getAllInfo().length-1).split("), (")
        val double = HashMap<Int,List<String>>(list.size)
        for (e in list){
            double[list.indexOf(e)] = e.split(",")
        }
        return double
    }

    private fun updateAllNodes(snapMap:HashMap<Int,List<String>>){
        // snapMap[0][0] - текущий узел
        // snapMap[1+] - список из 3 элементов, где элемент с индексом 0 - номер вершины, 1 - текущее лучшее расстояние до нее, 2 - номер вершины, из которого пришли в текущую
        nodes[snapMap[0]!![0].toInt()].isActive = true

        for (i in 2..snapMap.size-1){
            nodes[snapMap[i]!![0].toInt()].bestWay = snapMap[i]!![1]
            nodes[snapMap[i]!![0].toInt()].nodeFrom = snapMap[i]!![2]
        }
    }
    private fun getLogs(snapMap:HashMap<Int,List<String>>):String{

        val logs = StringBuilder("")
        val indexCurNode = snapMap[0]!![0].toInt()
        val isRelax = snapMap[1]!![0].toBoolean()

        logs.append("Текущий узел: ${indexCurNode+1}\n")
        if (isRelax){
            logs.append("Произошла релаксация\n")
        }
        else{
            logs.append("Не произошла релаксация\n")
        }

        logs.append("Лучший путь до узла: ${snapMap[indexCurNode+2]!![1].toInt()}\n")
        logs.append("Предыдущий узел: ${snapMap[indexCurNode+2]!![2].toInt()}")

        return logs.toString()
    }


    fun nextStep(){

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
        val snapMap = snapshotToMap(dijkstraAlgorithmController.getNextStep()?:return)
        //обновляем состояния узлов
        updateAllNodes(snapMap)

        val logEvent = Event.LogEvent(getLogs(snapMap))
        BroadcastPresenter.generateEvent(logEvent)

        //перерисовываем
        graphView.update()
    }

    fun previousStep(){

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

        val snapMap = snapshotToMap(dijkstraAlgorithmController.getPreviousStep()?:return)
        updateAllNodes(snapMap)


        val logEvent = Event.LogEvent(getLogs(snapMap))
        BroadcastPresenter.generateEvent(logEvent)

        graphView.update()

    }
    fun finishAlgorithm(){
        for(n in nodes){
            n.reset()
        }
        val snapMap = snapshotToMap(dijkstraAlgorithmController.getLast()!!)
        updateAllNodes(snapMap)
        graphView.displayDijkstraAlgorithmResult(dijkstraAlgorithmController.answer)
        graphView.update()
    }





    private fun convertEdgeInfoinArrayList(info_ :String) : ArrayList<List<String>>?{
        val string = info_.replace(" ","")
        val reg = (Regex("^(\\(\\d+,\\d+,\\d+\\),)*\\(\\d+,\\d+,\\d+\\)\$"))
        if (!string.matches(reg)) return null

        val match = Regex("(\\d+,\\d+,\\d+)").findAll(string)

        val list = ArrayList<List<String>>()
        for(e in match){
            val temp = e.destructured.toList().toString()
            list.add(temp.substring(1,temp.length-1).split(","))
        }
        return list
    }

    private fun convertNodeInfoinArrayList(info_ :String) : ArrayList<List<String>>?{
        val string = info_.replace(" ","")
        val reg = (Regex("^(\\(\\d+,\\d+\\),)*\\(\\d+,\\d+\\)\$"))
        if (!string.matches(reg)) return null

        val match = Regex("(\\d+,\\d+)").findAll(string)

        val list = ArrayList<List<String>>()
        for(e in match){
            val temp = e.destructured.toList().toString()
            list.add(temp.substring(1,temp.length-1).split(","))
        }
        return list
    }




    fun downloadGraph(fileName:String){
        val inputStream: InputStream = File(fileName).inputStream()

        val allInfo = mutableListOf<String>()
        inputStream.bufferedReader().forEachLine { allInfo.add(it) }

        if(allInfo.size !in 1..2) return  //если в файле больше, чем нужно строк

        val nodeInfoList = (convertNodeInfoinArrayList(allInfo[0].replace(" ",""))) ?: return //ошибка

        var edgeInfoList : ArrayList<List<String>>? = null

        if (allInfo.size==2) //нет информации о ребрах
            edgeInfoList = (convertEdgeInfoinArrayList(allInfo[1].replace(" ",""))) ?: return //ошибка

        //инициализируем UI граф

        nodes.clear()
        edges.clear()
        dualEdges.clear()
        //инициализируем вершины
        for (n in nodeInfoList){
            addNode(UINode(Coordinate(n[0].toInt(),n[1].toInt())))
        }
        if(edgeInfoList!=null) //если есть информация о ребрах
            for (e in edgeInfoList) {
                addEdge(UIEdge(nodes[e[0].toInt()], nodes[e[1].toInt()], e[2]))
            }

        graphView.update()

    }

    fun saveGraph(fileName:String){
        //создаем граф из строки
        val graphAsString = StringBuilder("")
        for (n in nodes)
            graphAsString.append("(${n.toString()}), ")

        if(graphAsString.isEmpty())
            return  //нет узлов

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

        //Записываем в файл
        val resultFile = File(fileName)
        Files.write(resultFile.toPath(), graphAsString.toString().toByteArray(), StandardOpenOption.CREATE)

    }
}
