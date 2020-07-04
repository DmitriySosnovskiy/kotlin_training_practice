package views.graphview

import java.awt.*
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import java.awt.event.MouseMotionListener
import java.awt.geom.Ellipse2D
import javax.swing.JPanel
import kotlin.math.pow


class GraphView : JPanel(), MouseListener, MouseMotionListener {

    init {
        background = Color.WHITE



        addMouseListener(this)
        addMouseMotionListener(this)
    }

    private val nodes = ArrayList<UINode>()
    private var currentGraphViewState: GraphViewStates = GraphViewStates.DefaultState

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
        currentGraphViewState = GraphViewStates.DefaultState
    }

    override fun mouseEntered(mouseEvent: MouseEvent) {

    }

    //Нажатие мышки
    override fun mouseClicked(mouseEvent: MouseEvent) {
        when(mouseEvent.clickCount) {

            //одиночное нажатие
            1 -> {

            }

            //Двойной клик
            2 -> {
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

        when(currentGraphViewState)
        {
            is GraphViewStates.NodeDraggingState -> {
                val draggableState = currentGraphViewState as GraphViewStates.NodeDraggingState

                draggableState.draggingNode.coordinate = dragCoordinate + draggableState.draggingOffset
            }

            is GraphViewStates.MovingDraggedMouseState -> {
            }

            is GraphViewStates.DefaultState -> {
                val node: UINode? = findClickedNode(dragCoordinate)
                if(node == null) {
                    currentGraphViewState = GraphViewStates.MovingDraggedMouseState
                    return
                }

                println(dragCoordinate.x )
                println(dragCoordinate.y )

                val coordinateOffset = node.coordinate - dragCoordinate
                currentGraphViewState = GraphViewStates.NodeDraggingState(coordinateOffset, node)
            }

        }

        repaint()
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