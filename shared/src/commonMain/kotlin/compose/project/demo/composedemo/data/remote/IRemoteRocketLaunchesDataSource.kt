package compose.project.demo.composedemo.data.remote

import compose.project.demo.composedemo.domain.entity.RocketLaunch
import kotlinx.coroutines.flow.Flow

interface IRemoteRocketLaunchesDataSource {
    fun latestLaunches(): Flow<List<RocketLaunch>>
}