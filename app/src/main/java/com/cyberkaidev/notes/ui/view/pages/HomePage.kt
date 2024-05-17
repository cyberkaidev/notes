package com.cyberkaidev.notes.ui.view.pages

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.cyberkaidev.notes.model.HomePageModel
import com.cyberkaidev.notes.ui.theme.NotesTheme
import com.cyberkaidev.notes.ui.view.shared.ListItemView
import kotlin.math.roundToInt


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePage(params: HomePageModel) {
    NotesTheme {
        var searchValue by remember { mutableStateOf("") }
        var searchActive by remember { mutableStateOf(false) }
        var bottomBarOffsetHeightPx by remember { mutableFloatStateOf(0f) }

        val animatePadding: Dp by animateDpAsState( if (searchActive) 0.dp else 16.dp )

        val sizeFloatingButton = 70.dp
        val bottomBarHeightPx = with(LocalDensity.current) { (sizeFloatingButton + sizeFloatingButton).roundToPx().toFloat() }

        val nestedScrollConnection = remember {
            object : NestedScrollConnection {
                override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                    val delta = available.y
                    val newOffset = bottomBarOffsetHeightPx + delta
                    bottomBarOffsetHeightPx = newOffset.coerceIn(-bottomBarHeightPx, 0f)
                    return Offset.Zero
                }
            }
        }

        Scaffold(
            Modifier.nestedScroll(nestedScrollConnection),
            floatingActionButton = {
                FloatingActionButton(
                    modifier = Modifier
                        .height(sizeFloatingButton)
                        .width(sizeFloatingButton)
                        .offset { IntOffset(x = 0, y = -bottomBarOffsetHeightPx.roundToInt()) },
                    onClick = { params.onNavigate.navigate("new-note") },
                    elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation()
                ) {
                    Icon(Icons.Filled.Add, "Localized description")
                }
            },
        ) { innerPadding ->
            Surface {
                SearchBar(
                    query = searchValue,
                    onQueryChange = { searchValue = it },
                    onSearch = { searchActive = false },
                    active = searchActive,
                    onActiveChange = { searchActive = it },
                    placeholder = { Text("Search") },
                    trailingIcon = {
                        if (searchActive) {
                            IconButton(
                                onClick = { searchValue = "" }
                            ) {
                                Icon(Icons.Filled.Clear, contentDescription = null)
                            }
                        }
                    },
                    leadingIcon = {
                        if (searchActive) {
                            IconButton(
                                onClick = { searchActive = false }
                            ) {
                                Icon(Icons.Filled.ArrowBack, contentDescription = null)
                            }
                        } else {
                            Icon(Icons.Filled.Search, contentDescription = null)
                        }
                    },
                    modifier = Modifier
                        .padding(horizontal = animatePadding)
                        .fillMaxWidth(),
                ) {
                    params.notes.forEach {
                        if (
                            searchValue.isNotBlank() and
                            searchActive and
                            it.title.contains(searchValue)
                        ) {
                            ListItemView(
                                title = it.title,
                                subTitle = it.subTitle
                            )
                        }
                    }
                }
                LazyColumn(
                    modifier = Modifier.padding(top = 100.dp),
                    contentPadding = PaddingValues(bottom = innerPadding.calculateBottomPadding() / 2)
                ) {
                    items(params.notes.size) { index ->
                        ListItemView(
                            title = params.notes[index].title,
                            subTitle = params.notes[index].subTitle
                        )
                    }
                }
            }
        }
    }
}