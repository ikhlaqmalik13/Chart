/*
 * Copyright (C) 2023 Miłosz Moczkowski
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mmoczkowski.chart

import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.mmoczkowski.chart.MarkerScope.Companion.relativeOffset
import com.mmoczkowski.chart.cache.api.Cache
import com.mmoczkowski.chart.provider.api.Tile
import com.mmoczkowski.chart.provider.api.TileCoords
import com.mmoczkowski.chart.provider.api.TileProvider

@Composable
fun Chart(
    modifier: Modifier = Modifier,
    state: ChartState = rememberChartState(),
    success: @Composable (TileCoords, ImageBitmap) -> Unit = { _, image ->
        DefaultSuccessTile(image)
    },
    error: @Composable () -> Unit = {
        DefaultErrorTile()
    },
    loading: @Composable () -> Unit = {
        DefaultLoadingTile()
    },
    provider: TileProvider,
    cache: Cache<TileCoords, ImageBitmap>
) {
    ChartContent(
        modifier = modifier,
        state = state,
        success = success,
        error = error,
        loading = loading,
        markers = emptyList<Nothing>(),
        markerPosition = { LatLng.NullIsland },
        marker = { /* no-op */ },
        provider = provider,
        cache = cache
    )
}

@Composable
fun <T> Chart(
    modifier: Modifier = Modifier,
    state: ChartState = rememberChartState(),
    success: @Composable (TileCoords, ImageBitmap) -> Unit = { _, image ->
        DefaultSuccessTile(image)
    },
    error: @Composable () -> Unit = {
        DefaultErrorTile()
    },
    loading: @Composable () -> Unit = {
        DefaultLoadingTile()
    },
    markers: List<T>,
    markerPosition: (T) -> LatLng,
    marker: @Composable MarkerScope.(T) -> Unit = { DefaultMarker() },
    provider: TileProvider,
    cache: Cache<TileCoords, ImageBitmap>
) {
    ChartContent(
        modifier = modifier,
        state = state,
        success = success,
        error = error,
        loading = loading,
        markers = markers,
        markerPosition = markerPosition,
        marker = marker,
        provider = provider,
        cache = cache
    )
}

@Composable
private fun <T> ChartContent(
    modifier: Modifier = Modifier,
    state: ChartState,
    success: @Composable (TileCoords, ImageBitmap) -> Unit,
    error: @Composable () -> Unit,
    loading: @Composable () -> Unit,
    markers: List<T>,
    markerPosition: (T) -> LatLng,
    marker: @Composable MarkerScope.(T) -> Unit,
    provider: TileProvider,
    cache: Cache<TileCoords, ImageBitmap>
) {
    val transformableState = rememberTransformableState { _, offsetDelta, _ ->
        state.focus = shiftFocus(
            currentFocus = state.focus,
            zoom = state.zoom,
            offsetDelta = offsetDelta,
            tileSize = provider.tileSize
        )
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .transformable(transformableState)
    ) {
        val visibleTiles: List<Tile> by derivedStateOf {
            getVisibleTiles(
                viewportSize = IntSize(constraints.maxWidth, constraints.maxHeight),
                totalTilesCount = state.totalTilesCount,
                zoom = state.zoom,
                tileSize = provider.tileSize,
                offset = state.offset
            )
        }

        ChartLayer(
            modifier = Modifier
                .fillMaxSize()
                .offset(x = -maxWidth / 2, -maxHeight / 2),
            tiles = visibleTiles,
            success = success,
            error = error,
            loading = loading,
            provider = provider,
            cache = cache
        )
        MarkerLayer(
            modifier = Modifier.fillMaxSize(),
            state = state,
            markers = markers,
            markerPosition = markerPosition,
            marker = marker,
        )
    }
}

@Composable
fun DefaultSuccessTile(image: ImageBitmap) {
    Image(
        bitmap = image,
        contentScale = ContentScale.FillBounds,
        contentDescription = null,
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
fun DefaultErrorTile() {
    Surface(modifier = Modifier.fillMaxSize()) {
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = null,
            modifier = Modifier.requiredSize(32.dp)
        )
    }
}

@Composable
fun DefaultLoadingTile() {
    Surface(modifier = Modifier.fillMaxSize()) {
        CircularProgressIndicator(
            modifier = Modifier.wrapContentSize()
        )
    }
}

@Composable
fun DefaultMarker() {
    Icon(
        imageVector = Icons.Default.Place,
        contentDescription = null,
        modifier = Modifier
            .requiredSize(32.dp)
            .relativeOffset(Offset(0f, -0.5f))
    )
}
