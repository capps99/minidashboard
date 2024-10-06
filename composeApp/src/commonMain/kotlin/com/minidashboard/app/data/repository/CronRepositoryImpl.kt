package com.minidashboard.app.data.repository

import com.minidashboard.app.domain.repository.CronRepository
import com.minidashboard.db.cron.Cron

class CronRepositoryImpl(

): CronRepository {

    override fun insert(uuid: String, setup: String, active: Boolean) {
        TODO("Not yet implemented")
    }

    override fun list(): List<Cron> {
        TODO("Not yet implemented")
    }

}