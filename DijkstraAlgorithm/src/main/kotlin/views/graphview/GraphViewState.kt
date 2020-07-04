package views.graphview

sealed class GraphViewState {
    object DefaultState : GraphViewState()
    object DeletingObjectState : GraphViewState()
    object AddingNodeState : GraphViewState()
    class NodeDraggingState(val draggingOffset: Coordinate, val draggingNode: UINode) : GraphViewState()
    class SheetMovingState(val draggingStartPoint: Coordinate) : GraphViewState()
    object EmptyDraggingState : GraphViewState()
}