package com.suda.datetimewallpaper.view.weather.line

/**
 * Created by ghbha on 2016/5/16.
 */
class HazeLine(maxX: Int, maxY: Int) : BaseLine(maxX, maxY) {

    override fun resetRandom() {
        startX -= maxX
    }

    override fun initRandom() {
        deltaX = 1 + random.nextInt(5)
        startX = random.nextInt(maxX)
        startY = random.nextInt(maxY)
        stopX = startX + deltaX
    }

    public override fun rain() {
        if (outOfBounds())
            resetRandom()
        startX += deltaX
        stopX += deltaX
    }

    override fun outOfBounds(): Boolean {
        return getStartX() >= maxX
    }
}
