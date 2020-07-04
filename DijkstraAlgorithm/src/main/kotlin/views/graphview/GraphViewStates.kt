package views.graphview

sealed class GraphViewStates {
    object DefaultState : GraphViewStates()
    object DeletingObjectState : GraphViewStates()
    object AddingNodeState : GraphViewStates()
    class NodeDraggingState(val draggingOffset: Coordinate, val draggingNode: UINode) : GraphViewStates()
    object MovingDraggedMouseState : GraphViewStates()
}