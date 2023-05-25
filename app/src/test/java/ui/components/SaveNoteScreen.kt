package ui.components

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.topic2.android.notes.R
import com.topic2.android.notes.domain.model.ColorModel
import com.topic2.android.notes.domain.model.NEW_NOTE_ID
import com.topic2.android.notes.domain.model.NoteModel
import com.topic2.android.notes.routing.NotesRouter
import com.topic2.android.notes.routing.Screen
import com.topic2.android.notes.util.fromHex
import kotlinx.coroutines.launch
import screens.MainViewModel

@Composable
@ExperimentalMaterialApi
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
fun SaveNoteScreen(viewModel: MainViewModel){
    val noteEntry: NoteModel by viewModel.noteEntry.observeAsState(NoteModel())
    val colors: List<ColorModel> by viewModel.colors
        .observeAsState(listOf())
    val bottomDrawerState: BottomDrawerState = rememberBottomDrawerState(BottomDrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()
    val moveNoteToTrashDialogShownState: MutableState<Boolean> = rememberSaveable {
        mutableStateOf(false)
    }
    BackHandler(
        onBack = {
            if(bottomDrawerState.isOpen){
                coroutineScope.launch{ bottomDrawerState.close()}
            } else{
                NotesRouter.navigateTo(Screen.Notes)
            }
        }
    )
    Scaffold(topBar = {
        val isEditingMode: Boolean = noteEntry.id != NEW_NOTE_ID
        SaveNoteTopAppBar(
            isEditingMode = isEditingMode,
            onBackClick = { NotesRouter.navigateTo(Screen.Notes)
            },
            onSaveNoteClick ={
                viewModel.saveNote(noteEntry)
            },
            onOpenColorPickerClick = {},
            onDeleteNoteClick = {
                viewModel.moveNoteToTrash(noteEntry)
            }
        )
    },
        content = {
            SaveNoteContent(
                note = noteEntry,
                onNoteChange = {updateNoteEntry->
                    viewModel.onNoteEntryChange(updateNoteEntry)
                }
            )
        }
    )
}

@Composable
private fun ColorPicker(
    colors: List<ColorModel>,
    onColorSelect: (ColorModel)->Unit)
{
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = "Color picker",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(8.dp)
        )
        LazyColumn(modifier = Modifier.fillMaxWidth()){
            items(
                colors.size
            ){
                    itemIndex ->
                val color = colors[itemIndex]
                ColorItem(color = color, onColorSelect = onColorSelect )
            }
        }
    }
}

@Composable
fun ColorItem(
    color: ColorModel,
    onColorSelect: (ColorModel)->Unit
){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                onClick = { onColorSelect(color) }
            )
    ){
        NoteColor(modifier = Modifier
            .padding(10.dp), color= Color.fromHex(color.hex), size = 80.dp,
            border = 2.dp
        )
        Text(
            text = color.name,
            fontSize = 22.sp,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .align(CenterVertically)
        )
    }
}

@Preview
@Composable

fun ColorItemPreview(){
    ColorItem(ColorModel.DEFAULT){}
}

@Preview
@Composable
fun ColorPickerPreview(){
    ColorPicker(colors = listOf(
        ColorModel.DEFAULT,
        ColorModel.DEFAULT,
        ColorModel.DEFAULT
    ) ){}
}




