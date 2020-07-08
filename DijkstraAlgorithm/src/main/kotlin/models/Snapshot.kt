package models

interface Memento{
    fun getStringVertex() :String
    fun getCurrentVertex():Int
    fun getAllInfo():String
    fun getRelax():Boolean
}

class Snapshot (graph: HashMap<Int, Vertex>, private val currentVertex: Int,private val relax:Boolean): Memento
{
    private val vertexAsString = StringBuilder("")

    init{
        for(v in graph) {
            vertexAsString.append(v.value.toString()+", ")
        }
        vertexAsString.delete(vertexAsString.length - 2, vertexAsString.length).append("")
    }

    override fun getStringVertex(): String {
        return vertexAsString.toString()
    }

    override fun getCurrentVertex(): Int {
        return currentVertex
    }
    override fun getRelax():Boolean {
        return relax
    }
    override fun getAllInfo(): String{
        return ("($currentVertex), ($relax), ${vertexAsString.toString()}")
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

