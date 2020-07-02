package ui

import javafx.scene.layout.BorderPane
import tornadofx.*

class MainView : View("My View") {
    override val root: BorderPane by fxml("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n\n<?import javafx.scene.control.Label?>\n<?import javafx.scene.layout.BorderPane?>\n\n\n<BorderPane xmlns=\"http://javafx.com/javafx/11.0.1\" xmlns:fx=\"http://javafx.com/fxml/1\">\n   <center>\n      <Label text=\"Бамс\" BorderPane.alignment=\"CENTER\" />\n   </center>\n</BorderPane>\n")
}
