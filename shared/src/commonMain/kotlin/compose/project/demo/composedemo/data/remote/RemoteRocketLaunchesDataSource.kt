package compose.project.demo.composedemo.data.remote

import compose.project.demo.composedemo.domain.entity.RocketLaunch
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class RemoteRocketLaunchesDataSource(
    private val httpClient: HttpClient,
    private val ioDispatcher: CoroutineDispatcher,
) : IRemoteRocketLaunchesDataSource {
    override fun latestLaunches(): Flow<List<RocketLaunch>> =
        flow {
            val latestLaunches =
                httpClient.get("https://api.spacexdata.com/v5/launches").body<List<RocketLaunch>>()
            emit(latestLaunches)
        }
            .flowOn(ioDispatcher)
}