package models

interface Memento{
    fun getStringVertex() :String
    fun getCurrentVertex():Int
    fun getAllInfo():String
}

class Snapshot (graph: HashMap<Int, Vertex>, private val currentVertex: Int): Memento
{
    private val vertexAsString = StringBuilder("")

    init{
        for(v in graph) {
            vertexAsString.append(v.toString()+", ")
        }
        vertexAsString.delete(vertexAsString.length - 2, vertexAsString.length).append("")
    }

    override fun getStringVertex(): String {
        return vertexAsString.toString()
    }

    override fun getCurrentVertex(): Int {
        return currentVertex
    }
    override fun getAllInfo(): String{
        return ("cur vertex = $currentVertex,\n${vertexAsString.toString()}")
    }

}

class SnapshotKeeper(){
    private var snapshotArray = emptyArray<Snapshot>()
    private var arraySize :Int = 0
    fun putSnapshot(new:Snapshot){
        snapshotArray+=new
        arraySize++
    }
    fun getSnapshot(index:Int):Snapshot?{
        if (index<0||index>=arraySize) return null
        return snapshotArray[index]
    }
    fun getSize():Int{
        return arraySize
    }
}