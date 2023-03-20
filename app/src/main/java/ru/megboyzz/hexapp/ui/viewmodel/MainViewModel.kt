package ru.megboyzz.hexapp.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch


class MainViewModel(application: Application) : AndroidViewModel(application) {

    val serviceStatus = MutableStateFlow(ServiceStatus.LOADING_INFO)

    val isRunServiceButtonEnabled = MutableStateFlow(false)

    val isReadButtonEnabled = MutableStateFlow(false)

    val hexList = MutableStateFlow(listOf<String>())


    init{
        checkAppInstalled()
        calculateButtonsState()
    }

    fun checkAppInstalled(){
        viewModelScope.launch(Dispatchers.IO) {
            val context = getApplication<Application>().applicationContext
            val pm = context.packageManager

            kotlin.runCatching {
                pm.getPackageInfo("ru.megboyzz.hexapp.service", 0)
            }
                .onFailure { serviceStatus.emit(ServiceStatus.APP_NOT_INSTALLED) }
                .onSuccess { serviceStatus.emit(ServiceStatus.DISABLED) }
        }
    }

    fun calculateButtonsState(){
        viewModelScope.launch(Dispatchers.IO) {

            when (serviceStatus.value){

                //Если приложение содержащее сервис
                //  не установлено или
                //  запускается или
                //  выключается или же
                //  загружается информация о нем
                // то запрещаем старотвать/выключать сервис,
                // а тем более включать режим чтения
                ServiceStatus.APP_NOT_INSTALLED,
                ServiceStatus.STARTING,
                ServiceStatus.DISABLING,
                ServiceStatus.LOADING_INFO -> {
                    isReadButtonEnabled.emit(false)
                    isRunServiceButtonEnabled.emit(false)
                }

                //В случае если сервис просто выключен
                // то нудно включить кнопку для запуска сервиса
                // но при этом конпка чтения не должна работать
                ServiceStatus.DISABLED -> {
                    isRunServiceButtonEnabled.emit(true)
                    isReadButtonEnabled.emit(false)
                }

                //В случае если сервис включен то можно и кнопку
                // включения режима чтения включить
                ServiceStatus.ENABLED -> {
                    isRunServiceButtonEnabled.emit(true)
                    isReadButtonEnabled.emit(true)
                }

                //Если работает режим чтения то нет смысла пользователю
                // завершать работу всего сервиса, поэтому оставим кнопку
                // включения/выключения сервиса выключенной
                ServiceStatus.READING_MODE -> {
                    isRunServiceButtonEnabled.emit(false)
                    isReadButtonEnabled.emit(true)
                }

                else -> {}
            }

        }
    }

    //Вырезать метод когда кнопки начнут работать нормально
    fun nextStatus(){
        viewModelScope.launch(Dispatchers.IO) {
            var next = serviceStatus.value.ordinal + 1
            if(next > ServiceStatus.values().size - 1) next = 0
            serviceStatus.emit(ServiceStatus.values()[next])
        }
    }

}

class MainViewModelFactory(private val application: Application) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java))
            return MainViewModel(application) as T
        throw  IllegalArgumentException("Unknown ViewModel Class")
    }

}