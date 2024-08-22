package com.castlefrog.games.asg.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun GameView(
    infoClickListener: () -> Unit,
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(PaddingValues(16.dp)),
            ) {
                IconButton(
                    modifier = Modifier
                        .weight(0.1f, false)
                        .align(Alignment.CenterVertically),
                    onClick = infoClickListener,
                ) {
                    Icon(
                        imageVector = Icons.Filled.Info,
                        contentDescription = "Settings",
                        // tint = AppTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        },
    ) { innerPadding ->
        content(innerPadding)
    }
}

@Preview
@Composable
fun GameViewPreview() {
    GameView(
        {}
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
