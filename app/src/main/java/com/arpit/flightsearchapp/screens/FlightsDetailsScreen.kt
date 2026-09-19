package com.arpit.flightsearchapp.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.arpit.flightsearchapp.data.Airport
import com.arpit.flightsearchapp.data.Favorite
import com.arpit.flightsearchapp.ui.theme.FlightSearchAppTheme
import com.arpit.flightsearchapp.viewmodel.FlightSearchUiState
import com.arpit.flightsearchapp.viewmodel.FlightSearchViewModel

/**
 * Stateful Composable connected to ViewModel.
 */
@Composable
fun FlightsDetailsScreen(
    modifier: Modifier = Modifier,
    viewModel: FlightSearchViewModel = viewModel(factory = FlightSearchViewModel.Factory)
) {
    val uiState by viewModel.uiState.collectAsState()

    FlightSearchContent(
        uiState = uiState,
        onSearchQueryChange = { viewModel.onSearchQueryChanged(it) },
        onAirportSelect = { viewModel.onAirportSelected(it) },
        onAddFavorite = { dep, dest -> viewModel.addFavorite(dep, dest) },
        onRemoveFavorite = { viewModel.removeFavorite(it) },
        modifier = modifier
    )
}

/**
 * Stateless UI Content Composable. Easy to test and preview!
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlightSearchContent(
    uiState: FlightSearchUiState,
    onSearchQueryChange: (String) -> Unit,
    onAirportSelect: (Airport) -> Unit,
    onAddFavorite: (String, String) -> Unit,
    onRemoveFavorite: (Favorite) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Flights") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // Search Input Field
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = onSearchQueryChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Search by airport name or IATA code") },
                placeholder = { Text("e.g. DEL, Mumbai, Heathrow") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Scrollable Content List
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                // SECTION A: Search Results from Room DB
                if (uiState.searchQuery.isNotBlank()) {
                    item {
                        Text(
                            text = "Airports Found (${uiState.searchResults.size})",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (uiState.searchResults.isEmpty()) {
                        item {
                            Text(
                                text = "No airports found matching '${uiState.searchQuery}'",
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    } else {
                        items(uiState.searchResults, key = { "search_${it.id}" }) { airport ->
                            AirportCard(
                                airport = airport,
                                isSelected = uiState.selectedDeparture?.id == airport.id,
                                onSelect = { onAirportSelect(airport) }
                            )
                        }
                    }

                    item { Spacer(modifier = Modifier.height(12.dp)) }
                }

                // SECTION B: Destinations for Selected Departure Airport
                if (uiState.selectedDeparture != null) {
                    item {
                        Text(
                            text = "Destinations from ${uiState.selectedDeparture.iataCode} (${uiState.selectedDeparture.name})",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    items(uiState.destinations, key = { "dest_${it.id}" }) { destination ->
                        val isFavorited = uiState.favorites.any {
                            it.departureCode == uiState.selectedDeparture.iataCode &&
                                    it.destinationCode == destination.iataCode
                        }

                        DestinationCard(
                            departureCode = uiState.selectedDeparture.iataCode,
                            destination = destination,
                            isFavorite = isFavorited,
                            onToggleFavorite = {
                                if (isFavorited) {
                                    val fav = uiState.favorites.find {
                                        it.departureCode == uiState.selectedDeparture.iataCode &&
                                                it.destinationCode == destination.iataCode
                                    }
                                    if (fav != null) onRemoveFavorite(fav)
                                } else {
                                    onAddFavorite(
                                        uiState.selectedDeparture.iataCode,
                                        destination.iataCode
                                    )
                                }
                            }
                        )
                    }

                    item { Spacer(modifier = Modifier.height(12.dp)) }
                }

                // SECTION C: Saved Favorites from Room DB
                item {
                    Text(
                        text = "Saved Favorite Routes (${uiState.favorites.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (uiState.favorites.isEmpty()) {
                    item {
                        Text(
                            text = "No saved favorite routes yet. Search an airport and tap the heart icon to save routes!",
                            color = MaterialTheme.colorScheme.outline,
                            fontSize = 14.sp
                        )
                    }
                } else {
                    items(uiState.favorites, key = { "fav_${it.id}" }) { favorite ->
                        FavoriteCard(
                            favorite = favorite,
                            onDelete = { onRemoveFavorite(favorite) }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Card displaying an Airport
 */
@Composable
fun AirportCard(
    airport: Airport,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(10.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = airport.iataCode,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.padding(horizontal = 8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = airport.name,
                    fontWeight = FontWeight.Medium,
                    fontSize = 15.sp
                )
                Text(
                    text = "${airport.passengers} passengers",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}

/**
 * Card displaying a Destination Airport
 */
@Composable
fun DestinationCard(
    departureCode: String,
    destination: Airport,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "$departureCode  ➜  ${destination.iataCode}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = destination.name,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = onToggleFavorite) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Toggle Favorite",
                    tint = if (isFavorite) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}

/**
 * Card displaying a saved Favorite route
 */
@Composable
fun FavoriteCard(
    favorite: Favorite,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.4f)
        )
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${favorite.departureCode}  ✈  ${favorite.destinationCode}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.tertiary
                )
                Text(
                    text = "Saved Route ID: ${favorite.id}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Remove Favorite",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

// ==================== PREVIEWS ====================

@Preview(showBackground = true, name = "1. Initial State (With Favorites)")
@Composable
fun FlightSearchContentPreviewInitial() {
    FlightSearchAppTheme {
        FlightSearchContent(
            uiState = FlightSearchUiState(
                searchQuery = "",
                favorites = listOf(
                    Favorite(id = 1, departureCode = "DEL", destinationCode = "BOM"),
                    Favorite(id = 2, departureCode = "BLR", destinationCode = "MAA")
                )
            ),
            onSearchQueryChange = {},
            onAirportSelect = {},
            onAddFavorite = { _, _ -> },
            onRemoveFavorite = {}
        )
    }
}

@Preview(showBackground = true, name = "2. Search Results & Selected Departure")
@Composable
fun FlightSearchContentPreviewSearching() {
    val delAirport = Airport(id = 1, iataCode = "DEL", name = "Indira Gandhi International Airport", passengers = 69200000)
    val bomAirport = Airport(id = 2, iataCode = "BOM", name = "Chhatrapati Shivaji Maharaj International Airport", passengers = 48800000)
    val blrAirport = Airport(id = 3, iataCode = "BLR", name = "Kempegowda International Airport", passengers = 33300000)

    FlightSearchAppTheme {
        FlightSearchContent(
            uiState = FlightSearchUiState(
                searchQuery = "DEL",
                searchResults = listOf(delAirport, bomAirport),
                selectedDeparture = delAirport,
                destinations = listOf(bomAirport, blrAirport),
                favorites = listOf(
                    Favorite(id = 1, departureCode = "DEL", destinationCode = "BOM")
                )
            ),
            onSearchQueryChange = {},
            onAirportSelect = {},
            onAddFavorite = { _, _ -> },
            onRemoveFavorite = {}
        )
    }
}
