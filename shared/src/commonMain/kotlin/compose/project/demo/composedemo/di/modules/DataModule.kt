package compose.project.demo.composedemo.di.modules

import compose.project.demo.composedemo.data.remote.IRemoteRocketLaunchesDataSource
import compose.project.demo.composedemo.data.remote.RemoteRocketLaunchesDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import org.koin.dsl.module

val dataModule = module {
    single<IRemoteRocketLaunchesDataSource> {
        RemoteRocketLaunchesDataSource(
            get(),
            Dispatchers.IO
        )
    }
}