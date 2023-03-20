package ru.megboyzz.hexapp.ui.viewmodel

enum class ServiceStatus(private val message: String) {

    DISABLED("сервис выключен"),

    STARTING("запуск сервиса"),

    ENABLED("сервис работает"),

    DISABLING("выключение сервиса"),

    APP_NOT_INSTALLED("приложение содержащее сервис не установлено"),

    READING_MODE("сервис работает в режиме чтения"),

    LOADING_INFO("Загрузка информации");


    override fun toString() = message

}