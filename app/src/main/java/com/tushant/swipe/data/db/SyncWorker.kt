package com.tushant.swipe.data.db

import android.content.Context
import android.util.Log
import androidx.work.*
import com.tushant.swipe.data.repository.ProductRepository
import kotlinx.coroutines.CancellationException
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SyncWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams), KoinComponent {

    private val repository: ProductRepository by inject()

    override suspend fun doWork(): Result {
        return try {
            repository.syncProducts(context = applicationContext)
            Result.success()
        } catch (e: CancellationException) {
            Log.w("SyncWorker", "Work cancelled")
            Result.retry()
        }
        catch (e: Exception) {
            Result.retry()
        }
    }

    companion object {
        private const val UNIQUE_WORK_NAME = "ProductSyncWorker"

        fun enqueueSyncWork(context: Context) {
            val workRequest = OneTimeWorkRequestBuilder<SyncWorker>()
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                )
                .build()

            WorkManager.getInstance(context)
                .enqueueUniqueWork(UNIQUE_WORK_NAME, ExistingWorkPolicy.KEEP, workRequest)
        }
    }
}
