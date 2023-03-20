package ru.megboyzz.hexapp.ui.viewmodel

enum class ServiceStatus(private val message: String) {

    STOPPED("сервис остановлен"),

    STARTING("запуск сервиса"),

    STARTED("сервис работает"),

    STOPPING("остановка сервиса"),

    APP_NOT_INSTALLED("приложение содержащее сервис не установлено"),

    TOGGLING_READING_MODE("переключение а режим чтения"),

    READING_MODE("сервис работает в режиме чтения"),

    LEAVE_READING_MODE("выход из режима чтения"),

    LOADING_INFO("загрузка информации");


    override fun toString() = message

}