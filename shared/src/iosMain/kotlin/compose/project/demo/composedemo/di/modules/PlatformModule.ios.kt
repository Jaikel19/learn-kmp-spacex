package compose.project.demo.composedemo.di.modules

import compose.project.demo.composedemo.data.local.DriverFactory
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platformModule(): Module = module {
    // Define aquí tus dependencias específicas de iOS
    single { DriverFactory() }
}