package ru.geekbrains.nasaapp.model

data class NotesData(
    val id: Int,
    val type: Int,
    val name: String? = "Имя",
    val contacts: String? = "Контакты",
    val title: String? = "Заголовок",
    val description: String? = "Заметка"
)