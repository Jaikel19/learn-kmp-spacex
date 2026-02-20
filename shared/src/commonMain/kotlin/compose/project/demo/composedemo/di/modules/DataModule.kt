package compose.project.demo.composedemo.di.modules

import compose.project.demo.composedemo.data.local.DriverFactory
import compose.project.demo.composedemo.data.local.ILocalRocketLaunchesDataSource
import compose.project.demo.composedemo.data.local.LocalRocketLaunchesDataSource
import compose.project.demo.composedemo.data.remote.IRemoteRocketLaunchesDataSource
import compose.project.demo.composedemo.data.remote.RemoteRocketLaunchesDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import org.koin.dsl.module
import compose.project.demo.composedemo.data.local.AppDatabase
import compose.project.demo.composedemo.data.repository.IRocketLaunchesRepository
import compose.project.demo.composedemo.data.repository.RocketLaunchesRepository

val dataModule = module {
    single<IRemoteRocketLaunchesDataSource> { RemoteRocketLaunchesDataSource(get(), Dispatchers.IO) }
    single { get<DriverFactory>().createDriver() }
    single { AppDatabase(get()) }
    single { get<AppDatabase>().appDatabaseQueries }
    single<ILocalRocketLaunchesDataSource> { LocalRocketLaunchesDataSource(get()) }
    single<IRocketLaunchesRepository> {
        RocketLaunchesRepository(
            get(),
            get(),
            Dispatchers.Default
        )
    }
}