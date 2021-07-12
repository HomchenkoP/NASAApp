package ru.geekbrains.nasaapp.viewmodel

import ru.geekbrains.nasaapp.model.ApodResponseDTO

sealed class MainState {
    data class Success(val data: ApodResponseDTO) : MainState()
    data class Error(val error: Throwable) : MainState()
    data class Loading(val progress: Int?) : MainState()
}