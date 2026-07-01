package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SettingsRepository(private val settingDao: SettingDao) {
    val allSettings: Flow<Map<String, String>> = settingDao.getAllSettings()
        .map { list -> list.associate { it.key to it.value } }

    suspend fun saveSetting(key: String, value: String) {
        settingDao.insertSetting(Setting(key, value))
    }
}
