package views.graphview

import views.UIConstants
import java.awt.Dimension
import javax.swing.JScrollPane
import javax.swing.ScrollPaneConstants

class GraphViewHolder : JScrollPane(GraphView()) {

    init {

        viewport.view.preferredSize = Dimension(UIConstants.graphScreenSheetWidth, UIConstants.graphScreenSheetHeight)

        horizontalScrollBarPolicy = ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS
        verticalScrollBarPolicy = ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS

        verticalScrollBar.unitIncrement = UIConstants.verticalScrollIncrement
        horizontalScrollBar.unitIncrement = UIConstants.horizontalScrollIncrement

        val graphView = GraphView()
        graphView.preferredSize = Dimension(70000, 5000)


    }
}