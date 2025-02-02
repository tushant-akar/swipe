package com.tushant.swipe.di

import com.tushant.swipe.data.db.ProductDatabase
import com.tushant.swipe.data.db.SyncWorker
import com.tushant.swipe.data.remote.RetrofitInstance
import com.tushant.swipe.data.repository.ProductRepository
import com.tushant.swipe.viewModel.ProductViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single { RetrofitInstance.api }
    single { ProductDatabase.getDatabase(androidContext()) }
    single { get<ProductDatabase>().productDao() }
    single { ProductRepository(get(), get()) }
    viewModel { ProductViewModel(get()) }
    factory { SyncWorker(androidContext(), get()) }
}