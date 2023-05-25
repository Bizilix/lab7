package com.topic2.android.notes.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.topic2.android.notes.data.repository.Repository
import com.topic2.android.notes.domain.model.NoteModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**

Модель просмотра, используемая для хранения глобального состояния приложения.*
Эта модель просмотра используется для всех экранов.
 */
class MainViewModel(private val repository: Repository) : ViewModel() {
    val notesNotInTrash: LiveData<List<NoteModel>> by lazy{
        repository.getAllNotesInTrash()
    }

    fun onCreateNewNoteClick(){

    }

    fun onNoteClick(note:NoteModel){

    }

    fun onNoteCheckedChange(
        note:NoteModel
    ){
        viewModelScope.launch(
            Dispatchers.Default
        ){
            repository.insertNote(note)
        }
    }

}
