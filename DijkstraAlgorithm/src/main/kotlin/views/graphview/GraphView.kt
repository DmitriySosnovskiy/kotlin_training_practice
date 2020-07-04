package views.graphview

import java.awt.*
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import java.awt.event.MouseMotionListener
import java.awt.geom.Ellipse2D
import javax.swing.JPanel
import javax.swing.SwingUtilities
import kotlin.math.pow

interface GraphViewObserver {
    fun onSheetDragged(offsetX: Int, offsetY: Int)
}

class GraphView : JPanel(), MouseListener, MouseMotionListener {

    init {
        background = Color.WHITE
        addMouseListener(this)
        addMouseMotionListener(this)
    }

    var sheetDraggingObserver: GraphViewObserver? = null

    private val nodes = ArrayList<UINode>()
    private var currentGraphViewState: GraphViewState = GraphViewState.DefaultState
        set(newGraphViewState) {
            when(newGraphViewState)
            {
                GraphViewState.DefaultState -> {

                }

                is GraphViewState.SheetMovingState -> {

                }

                is GraphViewState.NodeDraggingState -> {

                }
            }
            field = newGraphViewState
        }



    override fun paintComponent(graphics: Graphics) {
        super.paintComponent(graphics)

        val graphics2D = graphics as Graphics2D

        nodes.forEach {
            val graphicsCircle = Ellipse2D.Double((it.coordinate.x-it.radius).toDouble(),
                (it.coordinate.y-it.radius).toDouble(),
                (2*it.radius).toDouble(),
                (2*it.radius).toDouble())

            graphics2D.fill(graphicsCircle)
        }
    }

    override fun mouseReleased(mouseEvent: MouseEvent) {
        currentGraphViewState = GraphViewState.DefaultState
    }

    override fun mouseEntered(mouseEvent: MouseEvent) {

    }

    //Нажатие мышки
    override fun mouseClicked(mouseEvent: MouseEvent) {
        when(mouseEvent.clickCount) {

            //одиночное нажатие
            1 -> {
                when(mouseEvent.button) {
                    MouseEvent.BUTTON3 -> {

                    }
                }
            }

            //Двойной клик
            2 -> {
                if(mouseEvent.button == MouseEvent.BUTTON1)
                    addNode(mouseEvent.x, mouseEvent.y)
            }
        }
    }

    override fun mouseExited(mouseEvent: MouseEvent) {

    }

    override fun mousePressed(mouseEvent: MouseEvent) {

    }

    override fun mouseMoved(mouseEvent: MouseEvent) {

    }

    override fun mouseDragged(mouseEvent: MouseEvent) {
        val dragCoordinate = Coordinate(mouseEvent.x, mouseEvent.y)

        if(SwingUtilities.isRightMouseButton(mouseEvent)){
            if (currentGraphViewState == GraphViewState.DefaultState ||
                currentGraphViewState is GraphViewState.SheetMovingState)
                onRightMouseButtonDragging(dragCoordinate)
        }

        if(SwingUtilities.isLeftMouseButton(mouseEvent)) {
            if (currentGraphViewState == GraphViewState.DefaultState ||
                    currentGraphViewState is GraphViewState.NodeDraggingState)
                onLeftMouseButtonDragging(dragCoordinate)
        }

        repaint()
    }

    private fun onRightMouseButtonDragging(dragCoordinate: Coordinate){
        when(currentGraphViewState)
        {
            GraphViewState.DefaultState -> {
                currentGraphViewState = GraphViewState.SheetMovingState(dragCoordinate)
            }

            is GraphViewState.SheetMovingState -> {
                val currentSheetMovingState = currentGraphViewState as GraphViewState.SheetMovingState
                val offsetX = dragCoordinate.x - currentSheetMovingState.draggingStartPoint.x
                val offsetY = dragCoordinate.y - currentSheetMovingState.draggingStartPoint.y

                sheetDraggingObserver?.onSheetDragged(offsetX, offsetY)
            }
        }

    }

    private fun onLeftMouseButtonDragging(dragCoordinate: Coordinate) {
        when(currentGraphViewState)
        {
            is GraphViewState.NodeDraggingState -> {
                val draggableState = currentGraphViewState as GraphViewState.NodeDraggingState

                draggableState.draggingNode.coordinate = dragCoordinate + draggableState.draggingOffset
            }

            is GraphViewState.DefaultState -> {
                val node: UINode? = findClickedNode(dragCoordinate)

                if(node == null) {
                    currentGraphViewState = GraphViewState.EmptyDraggingState
                    return
                }

                val coordinateOffset = node.coordinate - dragCoordinate
                currentGraphViewState = GraphViewState.NodeDraggingState(coordinateOffset, node)
            }

        }
    }

    private fun findClickedNode(clickedPoint: Coordinate): UINode? {
        nodes.forEach() {
            if(isPointInsideNodeCircle(clickedPoint, it))
            {
                return it
            }
        }

        return null
    }

    private fun isPointInsideNodeCircle(pointCoordinate: Coordinate, node: UINode) : Boolean {
        return (((pointCoordinate.x - node.coordinate.x).toDouble()).pow(2.0) +
                ((pointCoordinate.y - node.coordinate.y).toDouble()).pow(2.0)
                <= node.radius.toDouble().pow(2.0))
    }

    private fun addNode(x: Int, y: Int) {
        val node = UINode(Coordinate(x, y))
        nodes.add(node)
        repaint()
    }
}