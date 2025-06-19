package com.seanof.sakugatomo.data.db

import android.content.Context
import androidx.room.Room
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@dagger.Module
@InstallIn(SingletonComponent::class)
object SakugaDatabaseModule {

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): SakugaTomoAppDatabase {
        return Room.databaseBuilder(
            context,
            SakugaTomoAppDatabase::class.java,
            "sakugaTomoAppDatabase"
        ).fallbackToDestructiveMigration().build()
    }

    @Singleton
    @Provides
    fun provideSakugaPostDao(db: SakugaTomoAppDatabase) = db.sakugaPostDao()
}