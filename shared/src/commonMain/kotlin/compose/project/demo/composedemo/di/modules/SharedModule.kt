package compose.project.demo.composedemo.di.modules

import org.koin.dsl.module

val sharedModule = module {
    // Define aquí las dependencias compartidas
    // caso especial para dependencias específicas de plataforma llamándolo como función
    includes(dataModule, domainModule, presentationModule, networkModule, platformModule())
}