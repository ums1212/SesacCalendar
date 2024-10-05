package com.sesac.sesacscheduler.common

import android.app.Application

class SchedulerApplication : Application(){

    companion object{
        private lateinit var schedulerApplication: SchedulerApplication
        fun getSchedulerApplication() = schedulerApplication
    }

    override fun onCreate() {
        super.onCreate()
        schedulerApplication = this
    }

}

