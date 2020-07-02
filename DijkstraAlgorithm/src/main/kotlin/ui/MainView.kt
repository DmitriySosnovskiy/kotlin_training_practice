package ui

import javafx.scene.canvas.Canvas
import javafx.scene.canvas.GraphicsContext
import javafx.scene.layout.BorderPane
import presenters.GraphView
import presenters.MainPresenter
import tornadofx.*

class MainView : View("My View"), GraphView {
    override val root: BorderPane by fxml("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n\n<?import javafx.scene.control.Label?>\n<?import javafx.scene.layout.BorderPane?>\n\n\n<BorderPane xmlns=\"http://javafx.com/javafx/11.0.1\" xmlns:fx=\"http://javafx.com/fxml/1\">\n   <center>\n      <Label text=\"Бамс\" BorderPane.alignment=\"CENTER\" />\n   </center>\n</BorderPane>\n")

    private val canvasGraph: Canvas by fxid("canvas_graph")
    private val canvasContext: GraphicsContext = canvasGraph.graphicsContext2D


    private val presenter:MainPresenter = MainPresenter(this)

    init {
    }
}
