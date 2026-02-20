package compose.project.demo.composedemo.data.repository

import compose.project.demo.composedemo.data.local.ILocalRocketLaunchesDataSource
import compose.project.demo.composedemo.data.remote.IRemoteRocketLaunchesDataSource
import compose.project.demo.composedemo.domain.entity.RocketLaunch
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach

class RocketLaunchesRepository(
    private val localRocketLaunchesDataSource: ILocalRocketLaunchesDataSource,
    private val remoteRocketLaunchesDataSource: IRemoteRocketLaunchesDataSource,
    private val defaultDispatcher: CoroutineDispatcher,
) : IRocketLaunchesRepository {
    override val latestLaunches: Flow<List<RocketLaunch>> =
        remoteRocketLaunchesDataSource
            .latestLaunches()
            .onEach { launches -> // Executes on the default dispatcher
                localRocketLaunchesDataSource.clearAndCreateLaunches(launches)
            }
            // flowOn affects the upstream flow ↑
            .flowOn(defaultDispatcher)
            // the downstream flow ↓ is not affected
            // If an error happens, emit the last cached values
            .catch { exception -> // Executes in the consumer's context
                val cachedLaunches = localRocketLaunchesDataSource.getAllLaunches()
                if (cachedLaunches.isNotEmpty()) {
                    emit(cachedLaunches)
                }
            }
}