package views.graphview

import views.UIConstants
import java.awt.*
import java.awt.geom.Rectangle2D
import javax.swing.*

class AreaPicker : JPanel() {

    var squareZoomFactor = 1

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

class ParametersPanel : JPanel() {
    init {
        layout = BoxLayout(this, BoxLayout.Y_AXIS)

        val densitySlider = JSlider(0, 100, 20)
        densitySlider.paintLabels = true
        densitySlider.majorTickSpacing = 20
        densitySlider.value = 0
        add(densitySlider)

        add(Box.createVerticalStrut(UIConstants.spaceBetweenContentInParametersRequester))

        val densityLabel = JLabel("Плотность графа: 0%")
        add(densityLabel)
    }
}

class GeneratingGraphParametersRequestPane : JPanel() {

    init {
        layout = BorderLayout()

        val areaPicker = AreaPicker()
        areaPicker.preferredSize = Dimension(UIConstants.areaPickerScreenWidth, UIConstants.areaPickedScreenHeight)

        add(areaPicker)

        add(ParametersPanel(), BorderLayout.EAST)
    }
}