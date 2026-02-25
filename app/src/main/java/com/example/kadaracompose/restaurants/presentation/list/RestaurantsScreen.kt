package com.example.kadaracompose.restaurants.presentation.list

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

import com.example.kadaracompose.restaurants.domain.Restaurant
import com.example.kadaracompose.restaurants.presentation.Description
import com.example.kadaracompose.ui.theme.KadaracomposeTheme
import androidx.compose.ui.text.style.TextAlign
import coil.compose.AsyncImage
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)

@Composable
fun RestaurantsScreen(state: RestaurantsScreenState, onCreateClick: () -> Unit, onItemClick: (id: Int) -> Unit, onFavoriteClick: (id: Int, oldValue: Boolean) -> Unit) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
//    val focusRequester = FocusRequester()

    // icons to mimic drawer destinations
        val items = listOf(
            Icons.Default.AccountCircle,
            Icons.Default.Email,
            Icons.Default.Favorite,
        )

    val selectedItem = remember { mutableStateOf(items[0]) }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(drawerState) {
                Column(Modifier.verticalScroll(rememberScrollState())) {
                    Spacer(Modifier.height(12.dp))
                    items.forEach { item ->
                        NavigationDrawerItem(
                            icon = { Icon(item, contentDescription = null) },
                            label = { Text(item.name.substringAfterLast(".")) },
                            selected = item == selectedItem.value,
                            onClick = {
                                scope.launch { drawerState.close() }
                                selectedItem.value = item
                            },
                            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                        )
                    }
                }
            }
        },
    ) {


    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Restaurant",
                        style = MaterialTheme.typography.headlineSmall
                    )
                        },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            scope.launch {
                                drawerState.open()
                            }
                        }
                    ) {
                        Icon(
                            Icons.Filled.Menu,
                            contentDescription = "Open Drawer"
                        )
                    }
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onCreateClick() },
                modifier = Modifier.height(52.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Restaurant"
                )
            }
        }
    ) { paddingValues ->
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
            .padding(paddingValues),
    ){
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(
                vertical = 8.dp,
                horizontal = 8.dp
            )
        ) {
            items(state.restaurants) { restaurant ->
                RestaurantItem(restaurant,
                    onFavoriteClick = { id, oldValue ->
                        onFavoriteClick(id, oldValue) },
                    onItemClick = { id -> onItemClick(id) })
            }
        }
        if(state.isLoading)
            CircularProgressIndicator(Modifier.semantics {
                this.contentDescription = Description.RESTAURANTS_LOADING
            })
        if(state.error != null)
            Text(state.error)
    }
    }
}
}

@Composable
fun RestaurantItem(item: Restaurant, onFavoriteClick: (id: Int, oldValue: Boolean) -> Unit, onItemClick: (id: Int) -> Unit) {
    val icon = if (item.isFavorite)
        Icons.Filled.Favorite
    else
        Icons.Filled.FavoriteBorder
    Card(
        elevation = CardDefaults.elevatedCardElevation(),
        modifier = Modifier.padding(8.dp)
            .clickable { onItemClick(item.id) }
    ) {
        Column() {
            Box(Modifier.fillMaxSize()) {
                AsyncImage(
                    model = item.image,
                    contentDescription = "Network image",
                )
            }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(8.dp)
        ) {
            RestaurantIcon(Icons.Filled.Place, Modifier.weight(0.15f))
            RestaurantDetails(item.title, item.description, Modifier.weight(0.7f))
            RestaurantIcon(icon, Modifier.weight(0.15f)) {
                onFavoriteClick(item.id,item.isFavorite)
            }
        }
        }
    }
}

@Composable
fun RestaurantIcon(
    icon: ImageVector,
    modifier: Modifier,
    onClick: () -> Unit = {}
) {
    Image(
        imageVector = icon,
        contentDescription = "Restaurant icon",
        modifier = modifier
            .padding(8.dp)
            .clickable { onClick() },
        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
    )
}


@Composable
fun RestaurantDetails(title: String, description: String, modifier: Modifier, horizontalAlignment: Alignment.Horizontal = Alignment.Start, textAlign: TextAlign = TextAlign.Start ) {
    Column(
        modifier = modifier,
        horizontalAlignment = horizontalAlignment,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall
        )
        Text(
            text = description,
            style = MaterialTheme.typography.bodySmall,
            textAlign = textAlign,
            modifier = Modifier.width( 250.dp).padding(top = 10.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    KadaracomposeTheme {
        Box(Modifier.fillMaxSize()) {
//            Image(
//                painter = painterResource(id = R.drawable.wine_glass),
//                contentDescription = "My image",
//            )
            RestaurantDetails(
                title = "Title",
                description = "Description",
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            )
        }
    }
}