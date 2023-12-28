import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuBoxScope
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import org.jetbrains.compose.resources.ExperimentalResourceApi

@OptIn(ExperimentalResourceApi::class)
@Composable
fun App() {
    TextDropdownField(
        state = rememberDropdownMenuState(setOf("A","B","C"), excludeSelectedItem = true),
        readOnly = false,
        itemText = { it }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun<T> TextDropdownField(
    state: DropdownMenuState<T>,
    readOnly: Boolean = true,
    label: @Composable (() -> Unit)? = null,
    itemText: (T) -> String
) {
    ExposedDropdownMenuBox(
        expanded = state.expanded,
        onExpandedChange = state::onExpandedChange,
        content = state.boxContent(
            readOnly = readOnly,
            label = label,
            itemText = itemText
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
private fun<T> DropdownMenuState<T>.boxContent(
    readOnly: Boolean,
    label: @Composable (() -> Unit)?,
    itemText: (T) -> String
): @Composable ExposedDropdownMenuBoxScope.() -> Unit = {
    TextField(
        value = textInput,
        placeholder = { Text(itemText(selectedOption)) },
        onValueChange = ::onValueChange,
        modifier = Modifier.menuAnchor(),
        readOnly = readOnly,
        label = label,
        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
        colors = ExposedDropdownMenuDefaults.textFieldColors(),
    )

    ExposedDropdownMenu(
        expanded = expanded,
        onDismissRequest = ::onDismissRequest,
        content = {
            val options = if(excludeSelectedItem) (options - selectedOption) else options

            options.forEach { option ->
                DropdownMenuItem(
                    onClick = { onClick(option) },
                    text = { Text(itemText(option)) },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                )
            }
        }
    )
}

@Composable
fun<T> rememberDropdownMenuState(
    options: Set<T>,
    init: T = options.first(),
    excludeSelectedItem: Boolean = false
) = remember { DropdownMenuState(options, excludeSelectedItem, init) }

class DropdownMenuState<T>(
    val options: Set<T>,
    val excludeSelectedItem: Boolean,
    init: T
) {
    var expanded by mutableStateOf(false)
        private set
    var selectedOption by mutableStateOf(init)
        private set
    var textInput by mutableStateOf(TextFieldValue())
        private set

    fun onClick(value: T) {
        selectedOption = value
        expanded = false
    }

    fun onValueChange(value: TextFieldValue) {
        textInput = value
        expanded = true
    }

    fun onExpandedChange(isExpanded: Boolean) {
        expanded = isExpanded
    }

    fun onDismissRequest() {
        expanded = false
    }
}

expect fun getPlatformName(): String