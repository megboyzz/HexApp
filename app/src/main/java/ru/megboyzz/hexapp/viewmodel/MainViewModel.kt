package ru.megboyzz.hexapp.viewmodel

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.launch
import ru.megboyzz.hexapp.HexServiceReceiver
import ru.megboyzz.hexapp.util.HexServiceConnection
import ru.megboyzz.hexapp.util.leaveReadingModeAction
import ru.megboyzz.hexapp.util.toggleReadingModeAction


class MainViewModel(private val app: Application) : AndroidViewModel(app) {

    val serviceStatus = MutableStateFlow(ServiceStatus.LOADING_INFO)

    val isRunServiceButtonEnabled = MutableStateFlow(false)

    val isReadButtonEnabled = MutableStateFlow(false)

    val hexList = MutableStateFlow(listOf<String>())

    private val connection = HexServiceConnection()

    private val serviceComponent = ComponentName("ru.megboyzz.hexapp.service", "ru.megboyzz.hexapp.service.HexService")

    init{
        Log.i("ViewModel", "ViewModel::init")
        createAndRegisterAllNeedsForHexServiceBroadcastReceiver()
        checkAppInstalledAndService()
        checkServiceStatus()
        calculateButtonsState()
    }

    private fun checkAppInstalledAndService(){
        viewModelScope.launch(Dispatchers.IO) {
            val context = app.applicationContext
            val pm = context.packageManager

            //Сначала спрашиваем у андроида есть ли у нас приложение сервис
            kotlin.runCatching {
                pm.getPackageInfo("ru.megboyzz.hexapp.service", 0)
            }
                //Если нет то говорим нет
                .onFailure { emitStatus(ServiceStatus.APP_NOT_INSTALLED) }
                .onSuccess {}
        }
    }

    private fun calculateButtonsState(){
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
                ServiceStatus.TOGGLING_READING_MODE, // <- это добавилось
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


    private fun createAndRegisterAllNeedsForHexServiceBroadcastReceiver(){

        val intentFilter = IntentFilter()
        intentFilter.addAction("ru.megboyzz.hexapp.action.RESULT_STATUS")
        intentFilter.addAction("ru.megboyzz.hexapp.action.ON_NEW_HEX")

        val hexServiceReceiver = HexServiceReceiver()

        hexServiceReceiver.setHandlers(
            onNewHexAction = {
                             viewModelScope.launch(Dispatchers.IO) {
                                 hexList.emit(hexList.value + it)
                             }
            },
            onStatusResult = {
                viewModelScope.launch(Dispatchers.IO) { emitStatus(it) }
            }
        )

        app.registerReceiver(hexServiceReceiver, intentFilter)

    }
    fun checkServiceStatus(){
        viewModelScope.launch(Dispatchers.IO) {
            emitStatus(ServiceStatus.STOPPED)
        }
    }

    override fun onCleared() {
        super.onCleared()
        Log.i("onCleared", "hehe")
        stopService()
    }



    //При кажом изменении статуса, нужно и изменять состояние кнопок
    private suspend fun emitStatus(status: ServiceStatus){
        serviceStatus.emit(status)
        calculateButtonsState()
    }

    fun startService(){
        viewModelScope.launch(Dispatchers.IO) {
            emitStatus(ServiceStatus.STARTING)
            //delay(1000)

            val intent = Intent()
            intent.component = serviceComponent
            app.startForegroundService(intent)

            app.bindService(intent, connection, Context.BIND_AUTO_CREATE)

            emitStatus(ServiceStatus.STARTED)
        }
    }

    fun stopService(){
        viewModelScope.launch(Dispatchers.IO) {
            emitStatus(ServiceStatus.STOPPING)
            //delay(1000)

            val intent = Intent()
            intent.component = serviceComponent
            app.unbindService(connection)
            app.stopService(intent)


            emitStatus(ServiceStatus.STOPPED)
        }
    }

    fun toggleIntoReadingMode(){
        viewModelScope.launch(Dispatchers.IO) {
            emitStatus(ServiceStatus.TOGGLING_READING_MODE)
            //delay(1000)
            val intent = Intent()
            intent.action = toggleReadingModeAction
            intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES)
            app.sendBroadcast(intent)
            emitStatus(ServiceStatus.READING_MODE)
        }
    }

    fun leaveFormReadingMode(){
        viewModelScope.launch(Dispatchers.IO) {
            emitStatus(ServiceStatus.LEAVE_READING_MODE)
            //delay(1000)
            val intent = Intent()
            intent.action = leaveReadingModeAction
            intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES)
            app.sendBroadcast(intent)
            emitStatus(ServiceStatus.STARTED)
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