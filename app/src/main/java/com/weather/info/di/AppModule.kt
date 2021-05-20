package com.weather.info.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.google.firebase.auth.FirebaseAuth
import com.weather.info.BuildConfig
import com.weather.info.data.pref.AppPreferenceManager
import com.weather.info.data.pref.PreferenceInfo
import com.weather.info.data.pref.PreferenceSource
import com.weather.info.data.remote.service.ApiService
import com.weather.info.data.room.dao.HistoryDao
import com.weather.info.data.room.db.WeatherDb
import com.weather.info.utils.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Singleton
    @Provides
    internal fun bindContext(application: Application): Context = application


    @Singleton
    @Provides
    internal fun getPreferenceSource(appPreferenceManager: AppPreferenceManager): PreferenceSource {
        return appPreferenceManager
    }

    @Singleton
    @Provides
    @PreferenceInfo
    internal fun providePreferenceName(): String {
        return Constants.PREF_NAME
    }

    @Singleton
    @Provides
    fun provideApiService(): ApiService {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL_WEB)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            //.addCallAdapterFactory(LiveDataCallAdapterFactory())
            .build()
            .create(ApiService::class.java)
    }

    @Singleton
    @Provides
    fun provideDb(app: Application): WeatherDb {
        return Room
            .databaseBuilder(app, WeatherDb::class.java, Constants.DB_NAME)
            .fallbackToDestructiveMigration()
            .build()
    }

    @Singleton
    @Provides
    fun provideHistoryDao(db: WeatherDb): HistoryDao {
        return db.historyDao()
    }


    @Singleton
    @Provides
    internal fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

}