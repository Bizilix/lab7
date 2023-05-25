package ui.components

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.runtime.
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.res.painterResource
import com.topic2.android.notes.R
import com.topic2.android.notes.domain.model.NoteModel
import com.topic2.android.notes.routing.Screen
import kotlinx.coroutines.launch
import screens.MainViewModel

private const val NO_DIALOG = 1
private const val RESTORE_NOTES_DIALOG = 2
private const val PERMANENTLY_DELETE_DIALOG = 3

@SuppressLint(UnusedMaterialScaffoldPaddingParameter)
@Composable
@ExperimentalMaterialApi
fun TrashScreen(viewModel MainViewModel) {

    val notesInThrash ListNoteModel by viewModel.notesInTrash
    .observeAsState(listOf())

    val selectedNotes ListNoteModel by viewModel.selectedNotes
    .observeAsState(listOf())

    val dialogState MutableStateInt = rememberSaveable { mutableStateOf(NO_DIALOG) }

    val scaffoldState ScaffoldState = rememberScaffoldState()

    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            val areActionsVisible = selectedNotes.isNotEmpty()
            TrashTopAppBar(
                onNavigationIconClick = {
                    coroutineScope.launch { scaffoldState.drawerState.open() }
                },
                onRestoreNotesClick = { dialogState.value = RESTORE_NOTES_DIALOG },
                onDeleteNotesClick = { dialogState.value = PERMANENTLY_DELETE_DIALOG },
                areActionsVisible = areActionsVisible
            )
        },
        scaffoldState = scaffoldState,
        drawerContent = {
            AppDrawer(
                currentScreen = Screen.Trash,
                closeDrawerAction = {
                    coroutineScope.launch { scaffoldState.drawerState.close() }
                }
            )
        },
        content = {
            Content(
                notes = notesInThrash,
                onNoteClick = { viewModel.onNoteSelected(it) },
                selectedNotes = selectedNotes
            )

            val dialog = dialogState.value
            if (dialog != NO_DIALOG) {
                val confirmAction () - Unit = when (dialog) {
                    RESTORE_NOTES_DIALOG - {
                        {
                            viewModel.restoreNotes(selectedNotes)
                            dialogState.value = NO_DIALOG
                        }
                    }
                    PERMANENTLY_DELETE_DIALOG - {
                        {
                            viewModel.permanentlyDeleteNotes(selectedNotes)
                            dialogState.value = NO_DIALOG
                        }
                    }
                    else - {
                    {
                        dialogState.value = NO_DIALOG
                    }
                }
                }

                AlertDialog(
                    onDismissRequest = { dialogState.value = NO_DIALOG },
                    title = { Text(mapDialogTitle(dialog)) },
                    text = { Text(mapDialogText(dialog)) },
                    confirmButton = {
                        TextButton(onClick = confirmAction) {
                            Text(Confirm)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { dialogState.value = NO_DIALOG }) {
                            Text(Dismiss)
                        }
                    }
                )
            }
        }
    )
}

@Composable
