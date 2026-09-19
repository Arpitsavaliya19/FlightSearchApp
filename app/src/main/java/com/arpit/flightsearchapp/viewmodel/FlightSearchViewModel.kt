package com.arpit.flightsearchapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.arpit.flightsearchapp.FlightSearchApplication
import com.arpit.flightsearchapp.data.Airport
import com.arpit.flightsearchapp.data.Favorite
import com.arpit.flightsearchapp.model.AirportRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * State representing the current UI data on screen.
 */
data class FlightSearchUiState(
    val searchQuery: String = "",
    val searchResults: List<Airport> = emptyList(),
    val selectedDeparture: Airport? = null,
    val destinations: List<Airport> = emptyList(),
    val favorites: List<Favorite> = emptyList()
)

class FlightSearchViewModel(
    private val airportRepository: AirportRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FlightSearchUiState())
    val uiState: StateFlow<FlightSearchUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null
    private var destinationJob: Job? = null

    init {
        observeFavorites()
    }

    /**
     * User typed in search box: search airports in Room DB.
     */
    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        searchJob?.cancel()

        if (query.isBlank()) {
            _uiState.update { it.copy(searchResults = emptyList(), selectedDeparture = null, destinations = emptyList()) }
        } else {
            searchJob = viewModelScope.launch {
                airportRepository.searchAirport(query).collect { airports ->
                    _uiState.update { currentState ->
                        currentState.copy(searchResults = airports)
                    }
                }
            }
        }
    }

    /**
     * User clicked on an airport: load destinations from Room DB.
     */
    fun onAirportSelected(airport: Airport) {
        _uiState.update { it.copy(selectedDeparture = airport) }
        destinationJob?.cancel()

        destinationJob = viewModelScope.launch {
            airportRepository.getDestination(airport.iataCode).collect { destinations ->
                _uiState.update { currentState ->
                    currentState.copy(destinations = destinations)
                }
            }
        }
    }

    /**
     * Add a route to Room DB favorites.
     */
    fun addFavorite(departureCode: String, destinationCode: String) {
        viewModelScope.launch {
            airportRepository.insertFavorite(
                Favorite(
                    departureCode = departureCode,
                    destinationCode = destinationCode
                )
            )
        }
    }

    /**
     * Remove a route from Room DB favorites.
     */
    fun removeFavorite(favorite: Favorite) {
        viewModelScope.launch {
            airportRepository.deleteFavorite(favorite)
        }
    }

    private fun observeFavorites() {
        viewModelScope.launch {
            airportRepository.getFavorites().collect { favorites ->
                _uiState.update { currentState ->
                    currentState.copy(favorites = favorites)
                }
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as FlightSearchApplication)
                FlightSearchViewModel(application.container.airportRepository)
            }
        }
    }
}
