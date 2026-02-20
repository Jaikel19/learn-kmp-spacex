package compose.project.demo.composedemo.di.modules

import compose.project.demo.composedemo.presentation.rocketLaunch.RocketLaunchViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val presentationModule = module {
    // Define aquí las dependencias de tu capa de presentación
    viewModel { RocketLaunchViewModel(get()) }
}