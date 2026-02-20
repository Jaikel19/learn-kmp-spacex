package compose.project.demo.composedemo.data.local

import compose.project.demo.composedemo.domain.entity.Links
import compose.project.demo.composedemo.domain.entity.Patch
import compose.project.demo.composedemo.domain.entity.RocketLaunch

// shared/src/commonMain/kotlin/compose/project/demo/composedemo/data/local/LocalRocketLaunchesDataSource.kt
class LocalRocketLaunchesDataSource(database: AppDatabase) : ILocalRocketLaunchesDataSource {
    private val dbQuery = database.appDatabaseQueries

    override fun getAllLaunches(): List<RocketLaunch> {
        return dbQuery.selectAllLaunchesInfo(::mapLaunchSelecting).executeAsList()
    }

    private fun mapLaunchSelecting(
        flightNumber: Long,
        missionName: String,
        details: String?,
        launchSuccess: Boolean?,
        launchDateUTC: String,
        patchUrlSmall: String?,
        patchUrlLarge: String?,
        articleUrl: String?,
    ): RocketLaunch {
        return RocketLaunch(
            flightNumber = flightNumber.toInt(),
            missionName = missionName,
            details = details,
            launchDateUTC = launchDateUTC,
            launchSuccess = launchSuccess,
            links =
                Links(
                    patch = Patch(small = patchUrlSmall, large = patchUrlLarge),
                    article = articleUrl,
                ),
        )
    }

    override fun clearAndCreateLaunches(launches: List<RocketLaunch>) {
        dbQuery.transaction {
            dbQuery.removeAllLaunches()
            launches.forEach { launch ->
                dbQuery.insertLaunch(
                    flightNumber = launch.flightNumber.toLong(),
                    missionName = launch.missionName,
                    details = launch.details,
                    launchSuccess = launch.launchSuccess ?: false,
                    launchDateUTC = launch.launchDateUTC,
                    patchUrlSmall = launch.links.patch?.small,
                    patchUrlLarge = launch.links.patch?.large,
                    articleUrl = launch.links.article,
                )
            }
        }
    }
}