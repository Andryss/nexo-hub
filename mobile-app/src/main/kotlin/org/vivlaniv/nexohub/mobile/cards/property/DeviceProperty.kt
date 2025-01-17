package org.vivlaniv.nexohub.mobile.cards.property

import androidx.compose.runtime.Composable
import org.vivlaniv.nexohub.common.PropertyInfo
import org.vivlaniv.nexohub.common.Type

@Composable
fun DeviceProperty(
    propertyInfo: PropertyInfo,
    putPropertyCallback: (Int) -> Unit,
    shouldRefresh: Int
) {
    when (propertyInfo.schema.type) {
        Type.BOOLEAN -> { BooleanDeviceProperty(propertyInfo, putPropertyCallback, shouldRefresh) }
        Type.PERCENT -> { PercentDeviceProperty(propertyInfo, putPropertyCallback, shouldRefresh) }
        Type.COLOR -> { ColorDeviceProperty(propertyInfo, putPropertyCallback, shouldRefresh) }
        Type.TEMPERATURE -> { TemperatureDeviceProperty(propertyInfo, putPropertyCallback, shouldRefresh) }
        Type.ENUM -> { EnumDeviceProperty(propertyInfo, putPropertyCallback, shouldRefresh) }
        Type.HUMIDITY -> { HumidityDeviceProperty(propertyInfo, putPropertyCallback, shouldRefresh) }
        else -> { TextFieldDeviceProperty(propertyInfo, putPropertyCallback, shouldRefresh) }
    }
}
