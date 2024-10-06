package com.minidashboard.app.domain.repository

import com.minidashboard.db.cron.Cron

interface CronRepository {
    fun insert(
        uuid: String,
        setup: String,
        active: Boolean
    )

    fun list(): List<Cron>
}