package org.vivlaniv.nexohub.widgets

import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextFieldWithSelection(options: List<String>, onTextChanged: (String) -> Unit) {

    var field by remember { mutableStateOf(TextFieldValue()) }
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        TextField(
            label = { Text(text = "Room") },
            value = field,
            onValueChange = {
                field = it
                onTextChanged(it.text)
            },
            modifier = Modifier.menuAnchor(),
            trailingIcon = {
                Icon(
                    imageVector = Icons.Filled.run { if (expanded) ArrowDropUp else ArrowDropDown },
                    contentDescription = null
                )
            }
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { label ->
                DropdownMenuItem(
                    onClick = {
                        field = TextFieldValue(label)
                        expanded = false
                        onTextChanged(label)
                    }
                ) {
                    Text(text = label)
                }
            }
        }
    }
}