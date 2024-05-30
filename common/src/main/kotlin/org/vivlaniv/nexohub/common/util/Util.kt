package org.vivlaniv.nexohub.common.util

fun colorToRGB(color: Int): Triple<Int, Int, Int> =
    Triple((color shr 16) and 0xFF, (color shr 8) and 0xFF,  color and 0xFF)

const val colorMask = 0x00FFFFFF

fun RGBToColor(red: Int, green: Int, blue: Int): Int =
    (((red shl 16) + (green shl 8) + blue) and colorMask)