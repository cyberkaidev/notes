package com.cyberkaidev.notes.ui.view.shared

import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.style.TextOverflow

@Composable
fun ListItemView(title: String, subTitle: String) {
    ListItem(
        headlineContent = {
            Text(
                title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        supportingContent = {
            Text(
                subTitle,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
    )
}