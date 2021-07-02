package ru.geekbrains.nasaapp.viewmodel

import ru.geekbrains.nasaapp.BuildConfig
import ru.geekbrains.nasaapp.model.ApodResponseDTO
import ru.geekbrains.nasaapp.repository.retrofit.RemoteDataSource

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val NO_API_KEY = "Отсутствует NASA API Key"
private const val SERVER_ERROR = "Ошибка сервера"
private const val REQUEST_ERROR = "Ошибка запроса на сервер"
private const val CORRUPTED_DATA = "Неполные данные"

class MainViewModel(
    private val mainLiveData: MutableLiveData<MainState> = MutableLiveData(),
    private val dataSource: RemoteDataSource = RemoteDataSource()
) :
    ViewModel() {

    fun getLiveData(): LiveData<MainState> = mainLiveData

    fun getApod() {
        mainLiveData.value = MainState.Loading(null)
        val apiKey: String = BuildConfig.NASA_API_KEY
        if (apiKey.isBlank()) {
            MainState.Error(Throwable(NO_API_KEY))
        } else {
            dataSource.getPictureOfTheDay(apiKey, callBack)
        }
    }

    private val callBack = object :
        Callback<ApodResponseDTO> {

        override fun onResponse(
            call: Call<ApodResponseDTO>,
            response: Response<ApodResponseDTO>
        ) {
            if (response.isSuccessful && response.body() != null) {
                mainLiveData.value = checkResponse(response.body()!!)
            } else {
                val message = response.message()
                if (message.isNullOrEmpty()) {
                    mainLiveData.value = MainState.Error(Throwable(SERVER_ERROR))
                } else {
                    mainLiveData.value = MainState.Error(Throwable(message))
                }
            }
        }

        private fun checkResponse(responseEntity: ApodResponseDTO): MainState {
            return if (responseEntity == null || (responseEntity.url.isNullOrEmpty() && responseEntity.hdurl.isNullOrEmpty())) {
                MainState.Error(Throwable(CORRUPTED_DATA))
            } else {
                MainState.Success(responseEntity)
            }
        }

        override fun onFailure(call: Call<ApodResponseDTO>, t: Throwable) {
            mainLiveData.value = MainState.Error(Throwable(t.message ?: REQUEST_ERROR))
        }
    }
}