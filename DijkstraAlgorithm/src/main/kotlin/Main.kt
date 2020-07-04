import views.MainWindow
import java.awt.Dimension
import javax.swing.JFrame

class Main {
    companion object{
        @JvmStatic
        fun main(args: Array<String>) {
            val frame = JFrame()
            frame.size = Dimension(1000, 600)

            frame.add(MainWindow())
            frame.isVisible = true
        }
    }
}