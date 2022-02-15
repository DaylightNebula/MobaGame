package daylightnebula.mobagamegdx.physics

import com.badlogic.gdx.math.Circle
import com.badlogic.gdx.math.Intersector
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector3
import kotlin.math.abs

class BoxCollider(val width: Float, val height: Float, val depth: Float) {

    var myPos: Vector3? = null
    var xzRect: Rectangle? = null
    var xyRect: Rectangle? = null
    var yzRect: Rectangle? = null
    
    fun intersects(myPos: Vector3, boxPos: Vector3, box: BoxCollider): Boolean {
        // if needed, regenerate rectangles
        if (myPos != this.myPos) {
            this.myPos = myPos
            xzRect = Rectangle(myPos.x - (width / 2), myPos.z - (depth / 2), width, depth)
            xyRect = Rectangle(myPos.x - (width / 2), myPos.y, width, height)
            yzRect = Rectangle(myPos.y, myPos.z - (depth / 2), height, depth)
        }

        // generate there rectangles
        val xzRect2 = Rectangle(boxPos.x - (box.width / 2), boxPos.z - (box.depth / 2), box.width, box.depth)
        val xyRect2 = Rectangle(boxPos.x - (box.width / 2), boxPos.y, box.width, box.height)
        val yzRect2 = Rectangle(boxPos.y, boxPos.z - (box.depth / 2), box.height, box.depth)

        // return if all intersect
        return xzRect!!.overlaps(xzRect2) && xyRect!!.overlaps(xyRect2) && yzRect!!.overlaps(yzRect2)
    }

    fun intersects(myPos: Vector3, point: Vector3, radius: Float): Boolean {
        // if needed, regenerate rectangles
        if (myPos != this.myPos) {
            this.myPos = myPos
            xzRect = Rectangle(myPos.x - (width / 2), myPos.z - (depth / 2), width, depth)
            xyRect = Rectangle(myPos.x - (width / 2), myPos.y, width, height)
            yzRect = Rectangle(myPos.y, myPos.z - (depth / 2), height, depth)
        }

        // generate circles
        val xzCircle = Circle(point.x, point.z, radius)
        val xyCircle = Circle(point.x, point.y, radius)
        val yzCircle = Circle(point.y, point.z, radius)

        // return true if all intersect
        return Intersector.overlaps(xzCircle, xzRect) && Intersector.overlaps(xyCircle, xyRect) && Intersector.overlaps(yzCircle, yzRect)
    }
 }
