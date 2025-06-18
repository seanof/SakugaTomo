package com.seanof.sakugatomo.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.seanof.sakugatomo.ui.theme.SakugaTomoTheme
import com.seanof.sakugatomo.util.Const.EMPTY

@Composable
fun SearchableInput(
    searchText: String,
    onValueChange: (String) -> Unit) {
    Row {
        TextField(modifier = Modifier.fillMaxWidth(),
            value = searchText,
            onValueChange = onValueChange,
            singleLine = true,
            leadingIcon = { Icon(
                imageVector = Icons.Default.Search,
                contentDescription = EMPTY
            ) },
            placeholder = { Text(text = "Search via tags") }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SearchableInputPreview() {
    SakugaTomoTheme {
        SearchableInput(EMPTY) {}
    }
}

/** Usage;
 *
SearchableInput(searchText = search, onValueChange = {
    search = it
    viewModel.fetchSakugaPosts(
        SakugaTomoViewModel.FetchType.SEARCH,
        it
    )
})
var textFieldState by remember { mutableStateOf(EMPTY) }
 *
 **/
