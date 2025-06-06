package com.nhm.distributor.screens.main.setLocation

import androidx.lifecycle.ViewModel
import com.nhm.distributor.networking.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SetLocationVM @Inject constructor(private val repository: Repository): ViewModel() {

}