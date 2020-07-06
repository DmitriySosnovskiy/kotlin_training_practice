package views.graphview

import views.UIConstants
import kotlin.math.*

class GraphMathProvider {

    data class DoubleCoordinate (
        val x: Double = 0.0,
        val y: Double = 0.0
    ) {

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

    fun isPointInsideEdgeRectangle(edge: UIEdge, pointCoordinate: Coordinate) : Boolean {
        val x1 = edge.sourceNode.coordinate.x
        val y1 = edge.sourceNode.coordinate.y

        val x2 = edge.endNode.coordinate.x
        val y2 = edge.endNode.coordinate.y

        //ax + by + c = 0 - уравнение прямой
        val a = y1-y2
        val b = x2-x1
        val c = x1*y2-x2*y1

        val x0 = pointCoordinate.x
        val y0 = pointCoordinate.y

        val distanceToLine = abs(a*x0 + b*y0 + c) / sqrt(a.toDouble().pow(2) + b.toDouble().pow(2))

        if(distanceToLine <= UIConstants.edgeWidth) {
            val maxX = if(x1 >= x2) x1 else x2
            val maxY = if(y1 >= y2) y1 else y2
            val minX = if(x1 >= x2) x2 else x1
            val minY = if(y1 >= y2) y2 else y1

            if(x0 in minX..maxX && y0 in minY..maxY) { return true }
        }

        return false
    }

    fun calculateEdgeWeightTextPosition(edge: UIEdge): Coordinate {
        val x1 = edge.sourceNode.coordinate.x
        val y1 = edge.sourceNode.coordinate.y

        val x2 = edge.endNode.coordinate.x
        val y2 = edge.endNode.coordinate.y

        val centerPoint = DoubleCoordinate((x1+x2)/2.0, (y1+y2)/2.0)

        val lineVector = DoubleCoordinate(x1 - centerPoint.x, y1 - centerPoint.y)

        val normalToLineVector =
            if (lineVector.x == 0.0)
                DoubleCoordinate(-1.0, 0.0)
            else
                DoubleCoordinate(-1 * abs(lineVector.y/lineVector.x), 1.0)

        val unitNormalToLineVector = normaliseVector(normalToLineVector)

        val textOffsetVector = unitNormalToLineVector * UIConstants.edgeWeightTextOffsetHeight

        return (centerPoint + textOffsetVector).toInt()
    }

    //Используется уравнение окружности
    fun isPointInsideNodeCircle(pointCoordinate: Coordinate, node: UINode) : Boolean {
        return (((pointCoordinate.x - node.coordinate.x).toDouble()).pow(2.0) +
                ((pointCoordinate.y - node.coordinate.y).toDouble()).pow(2.0)
                <= node.radius.toDouble().pow(2.0))
    }

    fun calculateEdgeArrow(edge: UIEdge) : UIEdgeArrow {
        val x1 = edge.sourceNode.coordinate.x
        val y1 = edge.sourceNode.coordinate.y

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

        when(determineRelativeQuarter(edge.endNode.coordinate, edge.sourceNode.coordinate))
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

    //Возвращает единичный вектор по заданному
    private fun normaliseVector(vector: DoubleCoordinate) : DoubleCoordinate {
        val vectorLen = sqrt(vector.x.pow(2) + vector.y.pow(2))

        return DoubleCoordinate(vector.x / vectorLen, vector.y / vectorLen)
    }

    //Определяет четверть в которой находится relativePoint по отношению к centerPoint
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