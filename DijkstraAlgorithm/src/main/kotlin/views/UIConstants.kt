package views

import java.awt.Color
import java.awt.Font

object UIConstants {
    const val circleRadius = 30
    const val circleStrokeWidth = 3
    const val spaceBetweenButtonsInToolbar = 5
    const val graphScreenSheetWidth = 3000
    const val graphScreenSheetHeight = 3000
    const val horizontalScrollIncrement = 20
    const val verticalScrollIncrement = 20
    const val edgeWidth = 6
    const val arrowHeight = 10
    const val arrowWidth = 10
    const val edgeWeightTextOffsetHeight = 10

    const val dualEdgeRotationAngleInGrads = Math.PI / 4

    val nodeFillColor = Color(106, 233, 114)
    val nodeActiveFillColor = Color(255, 0, 0)
    val nodeStrokeFillColor = Color(112, 112, 112)
    val textColor = Color(112, 112, 112)
    val edgeColor = Color(112, 112, 112)

    val nodeTextFont = Font("Segoe UI", Font.PLAIN, 40)
    val edgeWeightTextFont = Font("Segoe UI", Font.PLAIN, 20)
}