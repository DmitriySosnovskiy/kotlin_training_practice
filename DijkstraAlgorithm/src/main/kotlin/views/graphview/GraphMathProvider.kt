package views.graphview

import javafx.beans.property.DoublePropertyBase
import views.UIConstants
import kotlin.math.*

class GraphMathProvider {

    data class DoubleCoordinate (
        val x: Double = 0.0,
        val y: Double = 0.0
    ) {

        constructor(coordinate: Coordinate) : this(coordinate.x.toDouble(), coordinate.y.toDouble() )

        operator fun minus(anotherCoordinate: DoubleCoordinate) : DoubleCoordinate {
            return DoubleCoordinate(x - anotherCoordinate.x, y - anotherCoordinate.y)
        }

        operator fun plus(anotherCoordinate: DoubleCoordinate) : DoubleCoordinate {
            return DoubleCoordinate(x + anotherCoordinate.x, y + anotherCoordinate.y)
        }

        operator fun times(multiplier: Int): DoubleCoordinate {
            return DoubleCoordinate(x * multiplier, y * multiplier)
        }

        fun toInt() : Coordinate {
            return Coordinate(x.toInt(), y.toInt())
        }
    }

    //Используется уравнение окружности
    fun isPointInsideNodeCircle(pointCoordinate: Coordinate, node: UINode) : Boolean {
        return (((pointCoordinate.x - node.coordinate.x).toDouble()).pow(2.0) +
                ((pointCoordinate.y - node.coordinate.y).toDouble()).pow(2.0)
                <= node.radius.toDouble().pow(2.0))
    }

    fun calculateEdgeArrow(edge: UIEdge) : UIEdgeArrow {
        val x1 = edge.soureNode.coordinate.x
        val y1 = edge.soureNode.coordinate.y

        val x2 = edge.endNode.coordinate.x
        val y2 = edge.endNode.coordinate.y

        val len1 = abs(x1-x2)
        val len2 = abs(y1-y2)

        val hypotenuseLen = sqrt(len1.toDouble().pow(2) + len2.toDouble().pow(2))

        val angleSin: Double = len2 / hypotenuseLen
        val angleCos: Double = sqrt(1 - angleSin.pow(2))

        val radius = edge.endNode.radius

        val xOffset = radius * angleCos
        val yOffset = radius * angleSin

        var point1 = DoubleCoordinate()

        when(determineRelativeQuarter(edge.endNode.coordinate, edge.soureNode.coordinate))
        {
            1 -> {
                point1 = DoubleCoordinate(x2 + xOffset, y2 + yOffset)
            }

            2 -> {
                point1 = DoubleCoordinate(x2-xOffset, y2 + yOffset)
            }

            3 -> {
                point1 = DoubleCoordinate(x2-xOffset, y2 - yOffset)
            }

            4 -> {
                point1 = DoubleCoordinate(x2+xOffset, y2 - yOffset)
            }
        }

        val mainLineVector = DoubleCoordinate(x1.toDouble(), y1.toDouble()) - point1
        val mainLineUnitVector = normaliseVector(mainLineVector)

        val widthOffsetVector = mainLineUnitVector * UIConstants.arrowWidth

        val widthPoint = point1 + widthOffsetVector
        
        //перпендикулярный вектор
        val heightVector = if(mainLineUnitVector.x == 0.0)
            DoubleCoordinate(1.0, 0.0)
        else
            DoubleCoordinate(
                -1 * (mainLineUnitVector.y/mainLineUnitVector.x), 1.0
            )

        val heightUnitVector = normaliseVector(heightVector)

        val heightOffsetVector = heightUnitVector * UIConstants.arrowHeight

        val point2 = widthPoint + heightOffsetVector
        val point3 = widthPoint - heightOffsetVector

        return UIEdgeArrow(point1.toInt(), point2.toInt(), point3.toInt())
    }

    private fun normaliseVector(vector: DoubleCoordinate) : DoubleCoordinate {
        val vectorLen = sqrt(vector.x.pow(2) + vector.y.pow(2))

        return DoubleCoordinate(vector.x / vectorLen, vector.y / vectorLen)
    }


    private fun determineRelativeQuarter(centerPoint: Coordinate, relativePoint: Coordinate) : Int
    {
        val centerX = centerPoint.x
        val centerY = centerPoint.y

        val x = relativePoint.x
        val y = relativePoint.y

        if (x >= centerX && y >= centerY) return 1
        if (x < centerX && y >= centerY) return 2
        if (x <= centerX && y < centerY) return 3
        return if (x > centerX && y < centerY) 4
        else 1
    }

}