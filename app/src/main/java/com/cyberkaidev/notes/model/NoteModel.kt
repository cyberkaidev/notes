package com.cyberkaidev.notes.model

import java.util.UUID

data class NoteModel(
    val uuid: UUID,
    val title: String,
    val subTitle: String,
)