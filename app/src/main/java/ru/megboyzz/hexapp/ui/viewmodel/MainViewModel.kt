package ru.megboyzz.hexapp.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
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
                .onFailure { emitStatus(ServiceStatus.APP_NOT_INSTALLED) }
                .onSuccess { emitStatus(ServiceStatus.STOPPED) }
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
                ServiceStatus.STOPPING,
                ServiceStatus.LOADING_INFO,
                ServiceStatus.TOGGLING_READING_MODE,
                ServiceStatus.LEAVE_READING_MODE -> {
                    isReadButtonEnabled.emit(false)
                    isRunServiceButtonEnabled.emit(false)
                }

                //В случае если сервис просто выключен
                // то нудно включить кнопку для запуска сервиса
                // но при этом конпка чтения не должна работать
                ServiceStatus.STOPPED -> {
                    isRunServiceButtonEnabled.emit(true)
                    isReadButtonEnabled.emit(false)
                }

                //В случае если сервис включен то можно и кнопку
                // включения режима чтения включить
                ServiceStatus.STARTED -> {
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


    //При кажом изменении статуса, нужно и изменять состояние кнопок
    private suspend fun emitStatus(status: ServiceStatus){
        serviceStatus.emit(status)
        calculateButtonsState()
    }

    fun startService(){
        viewModelScope.launch(Dispatchers.IO) {
            emitStatus(ServiceStatus.STARTING)
            delay(1000)
            emitStatus(ServiceStatus.STARTED)
            calculateButtonsState()
        }
    }

    fun stopService(){
        viewModelScope.launch(Dispatchers.IO) {
            emitStatus(ServiceStatus.STOPPING)
            delay(1000)
            emitStatus(ServiceStatus.STOPPED)
            calculateButtonsState()
        }
    }

    fun toggleIntoReadingMode(){
        viewModelScope.launch(Dispatchers.IO) {
            emitStatus(ServiceStatus.TOGGLING_READING_MODE)
            delay(1000)
            emitStatus(ServiceStatus.READING_MODE)
            calculateButtonsState()
        }
    }

    fun leaveFormReadingMode(){
        viewModelScope.launch(Dispatchers.IO) {
            emitStatus(ServiceStatus.LEAVE_READING_MODE)
            delay(1000)
            emitStatus(ServiceStatus.STARTED)
            calculateButtonsState()
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