package org.vivlaniv.nexohub.widgets

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Selector(
    options: List<String>,
    modifier: Modifier = Modifier,
    selected: String? = null,
    onItemSelected: (String) -> Unit = { },
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedText by remember(selected) { mutableStateOf(selected ?: options[0]) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        BasicTextField(
            value = selectedText,
            onValueChange = { },
            readOnly = true,
            modifier = Modifier
                .menuAnchor()
                .border(1.dp, Color.Black, RoundedCornerShape(8.dp))
                .height(40.dp)
                .padding(PaddingValues(horizontal = 8.dp))
        ) { innerTextField ->
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                innerTextField()
                Icon(
                    imageVector = Icons.Filled.run { if (expanded) ArrowDropUp else ArrowDropDown },
                    contentDescription = null
                )
            }
        }
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { label ->
                DropdownMenuItem(
                    onClick = {
                        selectedText = label
                        expanded = false
                        onItemSelected(label)
                    }
                ) {
                    Text(text = label)
                }
            }
        }
    }
}

@Preview
@Composable
fun SelectorPreview() {
    Selector(
        options = listOf("One", "Two", "Looooooong"),
        selected = "One"
    )
}