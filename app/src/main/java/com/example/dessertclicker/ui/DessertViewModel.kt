package com.example.dessertclicker.ui

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import com.example.dessertclicker.R
import com.example.dessertclicker.data.Datasource.dessertList
import com.example.dessertclicker.data.DessertUiState
import com.example.dessertclicker.model.Dessert
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class DessertViewModel:ViewModel() {
    private val _uiState = MutableStateFlow(DessertUiState())
    val uiState:StateFlow<DessertUiState> = _uiState.asStateFlow()

    /**
     * Determine which dessert to show.
     */
    fun determineDessertToShow(desserts: List<Dessert>, dessertsSold: Int) {
        for (i in desserts.indices) {
            if (dessertsSold >= desserts[i].startProductionAmount) {
                _uiState.update { currentState ->
                    currentState.copy(
                        currentDessertIndex = i,
                        currentDessertImg = dessertList[i].imageId,
                        currentDessertPrice = dessertList[i].price
                    )
                }
            } else {
                // The list of desserts is sorted by startProductionAmount. As you sell more desserts,
                // you'll start producing more expensive desserts as determined by startProductionAmount
                // We know to break as soon as we see a dessert who's "startProductionAmount" is greater
                // than the amount sold.
                break
            }
        }
    }

    fun updateState(newRevenue:Int, newDessertsSold:Int) {
        _uiState.update { currentState ->
            currentState.copy(
                revenue = newRevenue,
                dessertsSold = newDessertsSold
            )
        }
    }

        /**
         * Share desserts sold information using ACTION_SEND intent
         */
        fun shareSoldDessertsInformation(intentContext: Context, dessertsSold: Int, revenue: Int) {
            val sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(
                    Intent.EXTRA_TEXT,
                    intentContext.getString(R.string.share_text, dessertsSold, revenue)
                )
                type = "text/plain"
            }

            val shareIntent = Intent.createChooser(sendIntent, null)

            try {
                ContextCompat.startActivity(intentContext, shareIntent, null)
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(
                    intentContext,
                    intentContext.getString(R.string.sharing_not_available),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
}