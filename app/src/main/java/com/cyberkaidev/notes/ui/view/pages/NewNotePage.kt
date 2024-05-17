package com.cyberkaidev.notes.ui.view.pages

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.cyberkaidev.notes.R
import com.cyberkaidev.notes.model.NoteModel
import com.cyberkaidev.notes.ui.theme.NotesTheme
import com.cyberkaidev.notes.utils.RealSpeechToText
import com.cyberkaidev.notes.viewmodel.AppAction
import com.cyberkaidev.notes.viewmodel.AppViewModel
import com.cyberkaidev.notes.viewmodel.NotesViewModel
import kotlinx.coroutines.launch
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewNotePage(onNavigate: NavHostController, viewModel: NotesViewModel) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val scope = rememberCoroutineScope()
    var permission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        permission = granted
    }

    val speechModel = viewModel {
        val app = get(APPLICATION_KEY)!!
        val stt = RealSpeechToText(app.applicationContext)
        AppViewModel(stt)
    }

    NotesTheme {
        val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
        var title by remember { mutableStateOf("") }
        var subTitle by remember { mutableStateOf("") }
        var speechInitialized by remember { mutableStateOf(false) }
        val snackbarHostState = remember { SnackbarHostState() }
        val keyboardController = LocalSoftwareKeyboardController.current

        if (speechInitialized) subTitle = speechModel.state.display

        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState)
            },
            topBar = {
                MediumTopAppBar(
                    navigationIcon = {
                        IconButton(onClick = { onNavigate.popBackStack() }) {
                            Icon(Icons.Filled.ArrowBack, "Back")
                        }
                    },
                    title = {
                        Text(
                            "New note",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    scrollBehavior = scrollBehavior
                )
            },
        ) { innerPadding ->
            Surface(
                modifier = Modifier
                    .padding(innerPadding)
                    .pointerInput(Unit) {
                        detectTapGestures(onTap = {
                            keyboardController!!.hide()
                        })
                    }
            ) {
                Column(
                    Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        value = title,
                        onValueChange = {title = it} ,
                        label = { Text("Title") },
                        singleLine = true
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ){
                        OutlinedTextField(
                            modifier = Modifier
                                .width(configuration.screenWidthDp.dp - 60.dp)
                                .padding(start = 16.dp),
                            value = subTitle,
                            onValueChange = { subTitle = it}  ,
                            label = { Text("Details") },
                            maxLines = 8,
                        )
                        IconButton(
                            modifier = Modifier.padding(top = 10.dp, start = 5.dp),
                            onClick = {
                                if (permission) {
                                    speechInitialized = !speechInitialized
                                    if (speechInitialized) {
                                        speechModel.send(AppAction.StartRecord)
                                    } else {
                                        speechModel.send(AppAction.EndRecord)
                                    }
                                } else {
                                    launcher.launch(Manifest.permission.RECORD_AUDIO)
                                }
                            }
                        ) {
                            if (!speechInitialized) {
                                Icon(
                                    painter = painterResource(R.drawable.mic),
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            } else {
                                Icon(
                                    painter = painterResource(R.drawable.stop),
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                    TextButton(
                        onClick = {
                            viewModel.add(
                                NoteModel(
                                    uuid = UUID.randomUUID(),
                                    title = title,
                                    subTitle = subTitle
                                )
                            )
                            title = ""
                            subTitle = ""
                            scope.launch {
                                snackbarHostState.showSnackbar("Successfully added!")
                            }
                        }
                    ) {
                        Text("Add note")
                    }
                }
            }
        }
    }
}