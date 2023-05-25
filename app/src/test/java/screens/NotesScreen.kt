package screens

import android.annotation.SuppressLint
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.List
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.tooling.preview.Preview
import com.topic2.android.notes.domain.model.NoteModel
import com.topic2.android.notes.routing.Screen
import kotlinx.coroutines.launch
import ui.components.AppDrawer
import ui.components.Note

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable

fun NotesScreen(
    viewModel: MainViewModel
){
    val notes: List<NoteModel> by viewModel
        .notesNotInTrash
        .observeAsState(listOf())

    val scaffoldState:ScaffoldState = rememberScaffoldState()

    val coroutineScope = rememberCoroutineScope()

    Scaffold(topBar = {
        TopAppBar(
            title = {
                Text(
                    text = "Notes",
                    color = MaterialTheme.colors.onPrimary
                )
            },
            navigationIcon = {
                IconButton(
                    onClick = {
                        coroutineScope.launch {
                            scaffoldState.drawerState.open()
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.List,
                        contentDescription = "Drawer Button"
                    )
                }
            }
        )
    },
        scaffoldState= scaffoldState,
        drawerContent = {
            AppDrawer(currentScreen = Screen.Notes, closeDrawerAction = {
                coroutineScope.launch {
                    scaffoldState.drawerState.close()
                }
            }
            )
        },
        content = {
            if (notes.isNotEmpty()){
                NotesList(notes = notes, onNoteCheckedChange = {
                    viewModel.onNoteCheckedChange(it)
                }, onNoteClick = {viewModel.onNoteClick(it) }
                )
            }
        },
        floatingActionButtonPosition = FabPosition.End,
        floatingActionButton = {
            FloatingActionButton(
                onClick = {viewModel.onCreateNewNoteClick()},
                contentColor = MaterialTheme.colors.background,
                content = {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Add Note Button"
                    )
                })

        }
    )
}
@Composable
private fun NotesList(
    notes: List<NoteModel>,
    onNoteCheckedChange: (NoteModel)->Unit,
    onNoteClick: (NoteModel)->Unit
){
    LazyColumn{
        items(count = notes.size){noteIndex ->
            val note =notes[noteIndex]
            Note(
                note = note,
                onNoteClick = onNoteClick,
                onNoteCheckedChange= onNoteCheckedChange
            )
        }
    }
}
@Preview
@Composable

private fun NoteListPreview(){
    NotesList(notes = listOf(
        NoteModel(1, "Note 1", "Content 1", null),
        NoteModel(2, "Note 2", "Content 2", false),
        NoteModel(3, "Note 3", "Content 3", true)
    ),
        onNoteCheckedChange = {},
        onNoteClick = {})
}
@Composable
fun rememberScaffoldState(
    drawerState: DrawerState= rememberDrawerState(DrawerValue.Closed),
    snackbarHostState: SnackbarHostState = remember{ SnackbarHostState()}
): ScaffoldState = remember{
    ScaffoldState(drawerState, snackbarHostState)
}

@Composable
fun rememberDrawerState(
    initialValue: DrawerValue,
    confirmStateChange: (DrawerValue)-> Boolean = {true}
): DrawerState{
    return rememberSaveable(saver = DrawerState.Saver(confirmStateChange)){
        DrawerState(initialValue,confirmStateChange)
    }


}