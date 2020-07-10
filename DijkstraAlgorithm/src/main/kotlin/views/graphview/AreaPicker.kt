package views.graphview

import views.UIConstants
import java.awt.*
import java.awt.geom.Rectangle2D
import javax.swing.*
import javax.swing.border.EmptyBorder
import javax.swing.text.NumberFormatter
import kotlin.math.sqrt

class AreaPicker : JPanel() {

    var squareZoomFactor = 3
    var minPointsAmount = 3

    val maxPointsAmount = UIConstants.areaPickerScreenWidth / UIConstants.areaChooseSquareSize

    fun update() = repaint()

    init {
        background = Color.WHITE
    }

    override fun paintComponent(graphics: Graphics) {
        super.paintComponent(graphics)

        val graphics2D = graphics as Graphics2D

        val square = Rectangle2D.Double(0.0, 0.0,
            squareZoomFactor * UIConstants.areaChooseSquareSize.toDouble(),
            squareZoomFactor * UIConstants.areaChooseSquareSize.toDouble())

        graphics2D.fill(square)
    }
}

class ParametersPanel(val areaPicker: AreaPicker) : JPanel() {

    val densitySlider = JSlider(1, 100)

    val spinnerModel = SpinnerNumberModel(3, 3, 100, 1)
    val verticesAmountSpinner = JSpinner(spinnerModel)

    init {
        border = EmptyBorder(10, 10, 10,10)

        layout = GridLayout(7, 1)

        densitySlider.paintLabels = true
        densitySlider.value = 1

        val densityLabel = JLabel("Плотность графа: 1%")
        densitySlider.addChangeListener {
            val value = (it.source as JSlider).value
            densityLabel.text = "Плотность графа: $value%"
        }
        add(densityLabel)

        add(densitySlider)

        add(JLabel("Количество вершин"))


        val txt = (verticesAmountSpinner.editor as JSpinner.NumberEditor).textField
        (txt.formatter as NumberFormatter).allowsInvalid = false


        add(verticesAmountSpinner)

        val coordinatesLabel = JLabel("Координаты покрытия: (${UIConstants.areaChooseCoordinateUnit*areaPicker.squareZoomFactor}; " +
                "${UIConstants.areaChooseCoordinateUnit*areaPicker.squareZoomFactor})")
        add(coordinatesLabel)

        verticesAmountSpinner.addChangeListener {
            areaPicker.minPointsAmount = (sqrt(verticesAmountSpinner.value.toString().toInt().toDouble()).toInt() + 2)
            if(areaPicker.squareZoomFactor < areaPicker.minPointsAmount) {
                areaPicker.squareZoomFactor = areaPicker.minPointsAmount
                coordinatesLabel.text = "Координаты покрытия: (${UIConstants.areaChooseCoordinateUnit*areaPicker.squareZoomFactor}; " +
                        "${UIConstants.areaChooseCoordinateUnit*areaPicker.squareZoomFactor})"
            }
            areaPicker.update()
        }

        val btnAreaMore = JButton("Больше")
        btnAreaMore.addActionListener {
            if(areaPicker.squareZoomFactor < areaPicker.maxPointsAmount) {
                areaPicker.squareZoomFactor++;
                coordinatesLabel.text = "Координаты покрытия: (${UIConstants.areaChooseCoordinateUnit*areaPicker.squareZoomFactor}; " +
                        "${UIConstants.areaChooseCoordinateUnit*areaPicker.squareZoomFactor})"
                areaPicker.update()
            }
        }
        add(btnAreaMore)

        val btnAreaLess = JButton("Меньше")
        btnAreaLess.addActionListener {
            if(areaPicker.squareZoomFactor > areaPicker.minPointsAmount) {
                areaPicker.squareZoomFactor--;
                coordinatesLabel.text = "Координаты покрытия: (${UIConstants.areaChooseCoordinateUnit*areaPicker.squareZoomFactor}; " +
                        "${UIConstants.areaChooseCoordinateUnit*areaPicker.squareZoomFactor})"
                areaPicker.update()
            }
        }
        add(btnAreaLess)
    }
}

class GeneratingGraphParametersRequestPane : JPanel() {

    val areaPicker = AreaPicker()
    val paramPanel = ParametersPanel(areaPicker)

    init {
        layout = BorderLayout()
        areaPicker.preferredSize = Dimension(UIConstants.areaPickerScreenWidth, UIConstants.areaPickedScreenHeight)

        add(areaPicker)

        add(paramPanel, BorderLayout.NORTH)
    }
}