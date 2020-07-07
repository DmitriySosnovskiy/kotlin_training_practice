package views.graphview

import java.awt.GridLayout
import javax.swing.JComboBox
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTextField

class TwoNumbersRequestPane(numbersRange: ArrayList<Int>) : JPanel() {

    val startNodeNumber = JComboBox(numbersRange.toArray())

    val endNodeNumber = JComboBox(numbersRange.toArray())

    init {
        layout = GridLayout(2, 2)

        val startNodeTextField = JLabel("Начальная вершина: ")

        val endNodeTextField = JLabel("Конечная вершина: ")

        add(startNodeTextField)
        add(startNodeNumber)
        add(endNodeTextField)
        add(endNodeNumber)
    }
}