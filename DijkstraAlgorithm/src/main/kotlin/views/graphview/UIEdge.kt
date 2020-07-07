package views.graphview

import views.UIConstants

class UIEdge(
    var sourceNode: UINode,
    var endNode: UINode,
    var weight: String
) {
    val width = UIConstants.edgeWidth



}

data class UIDualEdge(val edge1 : UIEdge, val edge2 : UIEdge)