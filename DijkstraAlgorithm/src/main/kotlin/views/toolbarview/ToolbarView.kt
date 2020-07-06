package views.toolbarview

import views.UIConstants
import java.awt.Color
import java.awt.Dimension
import javax.swing.*
import javax.swing.border.EmptyBorder

class ToolbarView : JPanel(){

    init {
        border = EmptyBorder(0, 10, 0,0)
        layout = BoxLayout(this, BoxLayout.Y_AXIS)

        val btn2 = JButton("Запустить алгоритм")
        add(btn2)
        add(Box.createVerticalStrut(UIConstants.spaceBetweenButtonsInToolbar))


        val btn4 = JButton("Сохранить граф")
        add(btn4)
        add(Box.createVerticalStrut(UIConstants.spaceBetweenButtonsInToolbar))

        val btn5 = JButton("Загрузить граф")
        add(btn5)
        add(Box.createVerticalStrut(UIConstants.spaceBetweenButtonsInToolbar))

        val btn6 = JButton("Очистить сцену")
        add(btn6)
        add(Box.createVerticalStrut(UIConstants.spaceBetweenButtonsInToolbar))

        val btn7 = JButton("Предыдущий шаг алгоритма")
        add(btn7)
        add(Box.createVerticalStrut(UIConstants.spaceBetweenButtonsInToolbar))

        val btn8 = JButton("Следующий шаг алгоритма")
        add(btn8)
        add(Box.createVerticalStrut(UIConstants.spaceBetweenButtonsInToolbar))

        val btn9 = JButton("Сгенерировать граф")
        add(btn9)
        add(Box.createVerticalStrut(UIConstants.spaceBetweenButtonsInToolbar))

        val logTextView = JTextField()
        logTextView.isEditable = false
        logTextView.size = Dimension(this.width, (this.height * 0.9).toInt())
        logTextView.background = Color.WHITE

        add(logTextView)
    }

}