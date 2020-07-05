package views.graphview

import javafx.scene.text.FontWeight
import views.UIConstants

class UIEdge(
    var sourceNode: UINode,
    var endNode: UINode,
    var weight: String
) {
    val width = UIConstants.edgeWidth
}