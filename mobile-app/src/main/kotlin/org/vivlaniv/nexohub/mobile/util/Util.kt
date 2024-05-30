package org.vivlaniv.nexohub.mobile.util

import androidx.compose.ui.graphics.Color
import java.util.NavigableMap

fun valueColor(value: Float, colorMap: NavigableMap<Float, Color>): Color {
    val (lk, lv) = colorMap.floorEntry(value)!!
    val (lr, lg, lb, la) = lv
    val (hk, hv) = colorMap.ceilingEntry(value)!!
    val (hr, hg, hb, ha) = hv
    if (hk == lk) {
        return lv
    }
    val coef = (value - lk) / (hk - lk)
    return Color(
        lr + (hr - lr) * coef,
        lg + (hg - lg) * coef,
        lb + (hb - lb) * coef,
        la + (ha - la) * coef
    )
}