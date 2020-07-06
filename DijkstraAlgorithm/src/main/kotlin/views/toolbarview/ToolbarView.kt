package views.toolbarview

import presenters.Event
import presenters.ToolbarPresenter
import presenters.ToolbarView
import views.UIConstants
import java.awt.Color
import java.awt.Dimension
import javax.swing.*
import javax.swing.border.EmptyBorder

class ToolbarView : JPanel(), ToolbarView {

    private val toolbarPresenter = ToolbarPresenter(this)

    init {
        border = EmptyBorder(0, 10, 0,0)
        layout = BoxLayout(this, BoxLayout.Y_AXIS)

        val btn1 = JButton("Запустить алгоритм")
        btn1.addActionListener { toolbarPresenter.handleEvent(Event.StartAlgorithm)}
        add(btn1)
        add(Box.createVerticalStrut(UIConstants.spaceBetweenButtonsInToolbar))


        val btn2 = JButton("Сохранить граф")
        btn2.addActionListener {
            val fileChooser = JFileChooser()


        }
        add(btn2)
        add(Box.createVerticalStrut(UIConstants.spaceBetweenButtonsInToolbar))

        val btn3 = JButton("Загрузить граф")
        btn3.addActionListener {

        }
        add(btn3)
        add(Box.createVerticalStrut(UIConstants.spaceBetweenButtonsInToolbar))

        val btn4 = JButton("Очистить сцену")
        btn4.addActionListener {

        }
        add(btn4)
        add(Box.createVerticalStrut(UIConstants.spaceBetweenButtonsInToolbar))

        val btn5 = JButton("Предыдущий шаг алгоритма")
        btn5.addActionListener {

        }
        add(btn5)
        add(Box.createVerticalStrut(UIConstants.spaceBetweenButtonsInToolbar))

        val btn6 = JButton("Следующий шаг алгоритма")
        btn6.addActionListener {

        }
        add(btn6)
        add(Box.createVerticalStrut(UIConstants.spaceBetweenButtonsInToolbar))

        val btn7 = JButton("Сгенерировать граф")
        btn7.addActionListener {

        }
        add(btn7)
        add(Box.createVerticalStrut(UIConstants.spaceBetweenButtonsInToolbar))

        val logTextView = JTextField()
        logTextView.isEditable = false
        logTextView.size = Dimension(this.width, (this.height * 0.9).toInt())
        logTextView.background = Color.WHITE

        add(logTextView)
    }

    override fun lockSaveAndDownload() {

    }

    override fun lockStart() {
    }
}