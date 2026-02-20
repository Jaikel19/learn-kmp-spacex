package compose.project.demo.composedemo.data.repository

import compose.project.demo.composedemo.data.local.ILocalRocketLaunchesDataSource
import compose.project.demo.composedemo.data.remote.IRemoteRocketLaunchesDataSource
import compose.project.demo.composedemo.domain.entity.Links
import compose.project.demo.composedemo.domain.entity.Patch
import compose.project.demo.composedemo.domain.entity.RocketLaunch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import dev.mokkery.mock
import dev.mokkery.every
import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.verify
import dev.mokkery.verify.VerifyMode.Companion.exactly

class RocketLaunchesRepositoryTest {

    private val localDataSource = mock<ILocalRocketLaunchesDataSource>()
    private val remoteDataSource = mock<IRemoteRocketLaunchesDataSource>()

    @Test
    fun `latestLaunches should fetch from remote and save to local`() = runTest {
        // Arrange
        val remoteLaunches =
            listOf(
                RocketLaunch(
                    flightNumber = 1,
                    missionName = "Falcon 1",
                    launchDateUTC = "2006-03-24T22:30:00.000Z",
                    details = null,
                    launchSuccess = false,
                    links = Links(Patch(null, null), null),
                )
            )

        every { remoteDataSource.latestLaunches() } returns flowOf(remoteLaunches)
        every { localDataSource.clearAndCreateLaunches(remoteLaunches) } returns Unit

        val repository =
            RocketLaunchesRepository(
                localRocketLaunchesDataSource = localDataSource,
                remoteRocketLaunchesDataSource = remoteDataSource,
                defaultDispatcher = Dispatchers.Unconfined,
            )

        // Act
        val result = repository.latestLaunches.first()

        // Assert
        assertEquals(remoteLaunches, result)
        verify(mode = exactly(1)) { localDataSource.clearAndCreateLaunches(remoteLaunches) }
    }

    @Test
    fun `latestLaunches should return cached data when remote fails`() = runTest {
        // Arrange
        val cachedLaunches =
            listOf(
                RocketLaunch(
                    flightNumber = 2,
                    missionName = "Cached Mission",
                    launchDateUTC = "2024-01-01T00:00:00Z",
                    details = null,
                    launchSuccess = true,
                    links = Links(Patch(null, null), null),
                )
            )
        every { remoteDataSource.latestLaunches() } throws Exception("Remote error")
        every { localDataSource.getAllLaunches() } returns cachedLaunches

        val repository =
            RocketLaunchesRepository(
                localRocketLaunchesDataSource = localDataSource,
                remoteRocketLaunchesDataSource = remoteDataSource,
                defaultDispatcher = Dispatchers.Unconfined,
            )

        // Act
        val result = repository.latestLaunches.first()

        // Assert
        assertEquals(cachedLaunches, result)
        verify(mode = exactly(1)) { localDataSource.getAllLaunches() }
    }
}