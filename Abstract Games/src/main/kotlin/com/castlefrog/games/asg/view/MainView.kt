package com.castlefrog.games.asg.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainView(
    newGameClickListener: () -> Unit,
    settingsClickListener: () -> Unit,
    onSearchChangedListener: (String) -> Unit,
    onSearchListener: (String) -> Unit,
    onSearchActiveChangedListener: (Boolean) -> Unit,
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(PaddingValues(16.dp)),
            ) {
                SearchBar(
                    modifier = Modifier
                        .weight(1.0f, true)
                        .padding(PaddingValues(end = 8.dp)),
                    query = "",
                    onQueryChange = onSearchChangedListener,
                    onSearch = onSearchListener,
                    active = false,
                    onActiveChange = onSearchActiveChangedListener,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = "Search",
                            //tint = AppTheme.colorScheme.onPrimaryContainer,
                        )
                    },
                ) {
                }
                IconButton(
                    modifier = Modifier
                        .weight(0.1f, false)
                        .align(Alignment.CenterVertically),
                    onClick = settingsClickListener,
                ) {
                    Icon(
                        imageVector = Icons.Filled.Settings,
                        contentDescription = "Settings",
                        // tint = AppTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = newGameClickListener
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add Game")
            }
        },
    ) { innerPadding ->
        content(innerPadding)
    }
}

@Preview
@Composable
fun MainViewPreview() {
    MainView(
        {}, {}, {}, {}, {},
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color.Blue)
        ) {
            Text(
                modifier = Modifier.padding(it),
                text = "Content",
            )
        }
    }
}
