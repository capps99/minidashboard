package com.minidashboard.app.presentation.home

import androidx.lifecycle.ViewModel
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.MutableStateFlow

sealed interface HomeState {
    data object Initial: HomeState
    data class Data(val message: String): HomeState
}

sealed interface HomeActions {
    data object TapButton: HomeActions
    data object TapMonitor: HomeActions
}

class HomeViewModel : ViewModel(){

    var state = MutableStateFlow(HomeState.Initial)
        private set

    fun processAction(action : HomeActions){
        when (action){
            HomeActions.TapButton -> {
                Napier.d { "HomeViewModel - Button tapped!" }
            }

            HomeActions.TapMonitor ->  Napier.d { "HomeViewModel - TapMonitor tapped!" }
        }
    }

}