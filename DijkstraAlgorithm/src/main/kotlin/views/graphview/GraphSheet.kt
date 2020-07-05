package views.graphview

import views.UIConstants
import java.awt.*
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import java.awt.event.MouseMotionListener
import java.awt.geom.Ellipse2D
import javax.swing.JMenuItem
import javax.swing.JPanel
import javax.swing.JPopupMenu
import javax.swing.SwingUtilities

interface GraphViewObserver {
    fun onSheetDragged(offsetX: Int, offsetY: Int)
}

class GraphSheet : JPanel(), MouseListener, MouseMotionListener {

    init {
        background = Color.WHITE

        addMouseListener(this)
        addMouseMotionListener(this)
    }

    var sheetDraggingObserver: GraphViewObserver? = null

    private val nodes = ArrayList<UINode>()
    private val edges = ArrayList<UIEdge>()

    private val mathProvider = GraphMathProvider()

    private var currentGraphViewState: GraphViewState = GraphViewState.DefaultState
        set(newGraphViewState) {
            when(newGraphViewState)
            {
                GraphViewState.DefaultState -> {
                    resetCursorToArrow()
                }

                is GraphViewState.SheetMovingState -> {
                    setCursorHand()
                }

                is GraphViewState.NodeDraggingState -> {
                    setCursorHand()
                }
            }
            field = newGraphViewState
        }


    override fun paintComponent(graphics: Graphics) {
        super.paintComponent(graphics)

        val graphics2D = graphics as Graphics2D

        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)

        when(currentGraphViewState)
        {
            is GraphViewState.CreatingEdgeState, is GraphViewState.CreatingEdgeAndSheetMovingState -> {
                val currentCreatingEdgeState: GraphViewState.CreatingEdgeState =
                    if(currentGraphViewState is GraphViewState.CreatingEdgeState)
                        currentGraphViewState as GraphViewState.CreatingEdgeState
                    else (currentGraphViewState as GraphViewState.CreatingEdgeAndSheetMovingState).creatingEdgeState

                drawBuildingEdge(currentCreatingEdgeState.creatingEdge, graphics2D)
            }
        }


        //Сначала рисуем рёбра, чтобы вершины были сверху них.

        edges.forEach {
            drawEdge(it, graphics2D)
        }

        var nodeIndex = 1
        nodes.forEach {
            drawNode(it, nodeIndex, graphics2D)
            nodeIndex++
        }
    }

    private fun drawNode(node: UINode, nodeNumber: Int, panelGraphics: Graphics2D) {
        val strokeCircle = Ellipse2D.Double((node.coordinate.x-node.radius).toDouble(),
            (node.coordinate.y-node.radius).toDouble(),
            (2*node.radius).toDouble(),
            (2*node.radius).toDouble())

        val graphicsCircle = Ellipse2D.Double((node.coordinate.x-UIConstants.circleRadius).toDouble(),
            (node.coordinate.y-UIConstants.circleRadius).toDouble(),
            (2*UIConstants.circleRadius).toDouble(),
            (2*UIConstants.circleRadius).toDouble())

        val nodeNumberString = nodeNumber.toString()
        panelGraphics.font = UIConstants.textFont
        val fontMetrics = panelGraphics.fontMetrics
        val textWidth = fontMetrics.getStringBounds(nodeNumberString, panelGraphics).width.toInt()
        val textHeight = fontMetrics.getStringBounds(nodeNumberString, panelGraphics).height.toInt()

        panelGraphics.color = UIConstants.nodeStrokeFillColor
        panelGraphics.fill(strokeCircle)

        panelGraphics.color = UIConstants.nodeFillColor
        panelGraphics.fill(graphicsCircle)

        panelGraphics.color = UIConstants.textColor
        panelGraphics.drawString(nodeNumberString,
            node.coordinate.x - textWidth/2,
            node.coordinate.y + textHeight/4)

    }

    private fun drawEdge(edge: UIEdge, panelGraphics: Graphics2D)
    {
        panelGraphics.stroke = BasicStroke((edge.width/2).toFloat())
        panelGraphics.color = UIConstants.edgeColor
        panelGraphics.drawLine(edge.soureNode.coordinate.x,
            edge.soureNode.coordinate.y,
            edge.endNode.coordinate.x,
            edge.endNode.coordinate.y)

        val arrow = mathProvider.calculateEdgeArrow(edge)

        panelGraphics.drawLine(arrow.point2.x,
            arrow.point2.y,
            arrow.point1.x,
            arrow.point1.y)

        panelGraphics.drawLine(arrow.point3.x,
            arrow.point3.y,
            arrow.point1.x,
            arrow.point1.y)
    }

    private fun drawBuildingEdge(buildingEdge: UIBuildingEdge, panelGraphics: Graphics2D)
    {
        panelGraphics.stroke = BasicStroke((buildingEdge.width/2).toFloat())
        panelGraphics.color = UIConstants.edgeColor
        panelGraphics.drawLine(buildingEdge.startCoordinate.x,
            buildingEdge.startCoordinate.y,
            buildingEdge.endCoordinate.x,
            buildingEdge.endCoordinate.y)
    }

    override fun mouseReleased(mouseEvent: MouseEvent) {
        when(currentGraphViewState)
        {
            is GraphViewState.CreatingEdgeState -> {
                //ничего не делаем, т.к. mouseClick обрабатывается ПОСЛЕ mouseReleased
            }

            is GraphViewState.CreatingEdgeAndSheetMovingState -> {
                currentGraphViewState = (currentGraphViewState as GraphViewState.CreatingEdgeAndSheetMovingState)
                    .creatingEdgeState
            }

            else -> {
                currentGraphViewState = GraphViewState.DefaultState
            }

        }
    }

    override fun mouseEntered(mouseEvent: MouseEvent) { }

    //Нажатие любой кнопки мышки (и колёсика)
    override fun mouseClicked(mouseEvent: MouseEvent) {
        when(mouseEvent.clickCount) {

            //одиночное нажатие
            1 -> {
                when(mouseEvent.button) {
                    //Правая кнопка
                    MouseEvent.BUTTON3 -> {
                        when(currentGraphViewState)
                        {
                            is GraphViewState.DefaultState -> {
                                val node: UINode? = findNodeUnderMouse(Coordinate(mouseEvent.x, mouseEvent.y))
                                node ?: return

                                createAndShowPopupMenu(mouseEvent, node)
                            }

                        }
                    }

                    //Левая кнопка
                    MouseEvent.BUTTON1 -> {

                        when(currentGraphViewState)
                        {
                            is GraphViewState.CreatingEdgeState -> {
                                val node: UINode? = findNodeUnderMouse(Coordinate(mouseEvent.x, mouseEvent.y))
                                if(node == null) {
                                    currentGraphViewState = GraphViewState.DefaultState
                                    repaint()
                                    return
                                }

                                val currentCreatingEdgeState = currentGraphViewState as GraphViewState.CreatingEdgeState

                                addEdge(currentCreatingEdgeState.sourceNode, node)

                                currentGraphViewState = GraphViewState.DefaultState
                            }
                        }
                    }

                }
            }

            //Двойной клик
            2 -> {
                //Левая кнопка
                if(mouseEvent.button == MouseEvent.BUTTON1)
                    if(currentGraphViewState is GraphViewState.DefaultState)
                        addNode(mouseEvent.x, mouseEvent.y)
            }
        }
    }

    override fun mouseExited(mouseEvent: MouseEvent) { }

    override fun mousePressed(mouseEvent: MouseEvent) { }

    override fun mouseMoved(mouseEvent: MouseEvent) {
        if(findNodeUnderMouse(Coordinate(mouseEvent.x, mouseEvent.y)) != null)
        {
            setCursorHand()

        }
        else resetCursorToArrow()

        updateCreatingEdge(Coordinate(mouseEvent.x, mouseEvent.y))
    }

    override fun mouseDragged(mouseEvent: MouseEvent) {
        val dragCoordinate = Coordinate(mouseEvent.x, mouseEvent.y)

        if(SwingUtilities.isRightMouseButton(mouseEvent)){
            if (currentGraphViewState == GraphViewState.DefaultState ||
                currentGraphViewState is GraphViewState.SheetMovingState ||
                currentGraphViewState is GraphViewState.CreatingEdgeState ||
                currentGraphViewState is GraphViewState.CreatingEdgeAndSheetMovingState)
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
            GraphViewState.DefaultState, is GraphViewState.CreatingEdgeState -> {

                currentGraphViewState =
                    if(currentGraphViewState is GraphViewState.DefaultState)
                        GraphViewState.SheetMovingState(dragCoordinate)
                    else
                        GraphViewState.CreatingEdgeAndSheetMovingState(
                            currentGraphViewState as GraphViewState.CreatingEdgeState,
                            GraphViewState.SheetMovingState(dragCoordinate))

            }

            is GraphViewState.SheetMovingState, is GraphViewState.CreatingEdgeAndSheetMovingState -> {

                val currentSheetMovingState: GraphViewState.SheetMovingState =
                    if (currentGraphViewState is GraphViewState.SheetMovingState)
                        currentGraphViewState as GraphViewState.SheetMovingState
                    else (currentGraphViewState as GraphViewState.CreatingEdgeAndSheetMovingState).sheetMovingState

                val offsetX = dragCoordinate.x - currentSheetMovingState.draggingStartPoint.x
                val offsetY = dragCoordinate.y - currentSheetMovingState.draggingStartPoint.y

                sheetDraggingObserver?.onSheetDragged(offsetX, offsetY)
                updateCreatingEdge(dragCoordinate)
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
                val node: UINode? = findNodeUnderMouse(dragCoordinate)

                if(node == null) {
                    currentGraphViewState = GraphViewState.EmptyDraggingState
                    return
                }

                val coordinateOffset = node.coordinate - dragCoordinate
                currentGraphViewState = GraphViewState.NodeDraggingState(coordinateOffset, node)
            }

        }
    }

    private fun findNodeUnderMouse(cursorCoordinate: Coordinate): UINode? {
        nodes.forEach() {
            if(mathProvider.isPointInsideNodeCircle(cursorCoordinate, it))
            {
                return it
            }
        }
        return null
    }

    private fun addNode(x: Int, y: Int) {
        val node = UINode(Coordinate(x, y))
        nodes.add(node)
        repaint()
    }

    private fun removeNode(removableNode: UINode){
        nodes.remove(removableNode)
        repaint()
    }

    private fun addEdge(sourceNode: UINode, endNode: UINode) {
        edges.add(UIEdge(sourceNode, endNode))
        repaint()
    }

    private fun updateCreatingEdge(newEndCoordinate: Coordinate) {
        when(currentGraphViewState) {
            is GraphViewState.CreatingEdgeState, is GraphViewState.CreatingEdgeAndSheetMovingState -> {
                val currentCreatingEdgeState: GraphViewState.CreatingEdgeState =
                    if (currentGraphViewState is GraphViewState.CreatingEdgeState)
                        currentGraphViewState as GraphViewState.CreatingEdgeState
                    else (currentGraphViewState as GraphViewState.CreatingEdgeAndSheetMovingState).creatingEdgeState

                currentCreatingEdgeState.creatingEdge.endCoordinate = newEndCoordinate
                repaint()
            }
        }
    }

    private fun setCursorHand() = run { cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR) }
    private fun resetCursorToArrow() = run { cursor = Cursor.getDefaultCursor() }

    private fun createAndShowPopupMenu(sourceMouseEvent: MouseEvent, affectedNode: UINode) {
        val popupMenu = JPopupMenu()

        val addEdgeItem = JMenuItem("Добавить ребро")
        addEdgeItem.addActionListener {
            currentGraphViewState = GraphViewState.CreatingEdgeState(affectedNode)
        }
        popupMenu.add(addEdgeItem)

        val removeNodeItem = JMenuItem("Удалить вершину")
        removeNodeItem.addActionListener { removeNode(affectedNode) }
        popupMenu.add(removeNodeItem)

        popupMenu.show(sourceMouseEvent.component, sourceMouseEvent.x, sourceMouseEvent.y)
    }
}