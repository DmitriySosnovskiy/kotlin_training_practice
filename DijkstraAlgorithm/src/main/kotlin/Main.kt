import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.text.FontWeight
import javafx.stage.Stage
import tornadofx.*

class HelloWorldApp : Application(){

    override fun start(primaryStage: Stage?) {
        var root : Parent = FXMLLoader.load(javaClass.classLoader.getResource("MainView.fxml"))
        var scene = Scene(root)

        primaryStage?.scene = scene
        primaryStage?.show()
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            launch<HelloWorldApp>()
        }
    }
}
