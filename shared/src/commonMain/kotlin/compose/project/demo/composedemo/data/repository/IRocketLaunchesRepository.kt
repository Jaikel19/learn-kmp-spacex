package compose.project.demo.composedemo.data.repository

import compose.project.demo.composedemo.domain.entity.RocketLaunch
import kotlinx.coroutines.flow.Flow

interface IRocketLaunchesRepository {
    val latestLaunches: Flow<List<RocketLaunch>>
}