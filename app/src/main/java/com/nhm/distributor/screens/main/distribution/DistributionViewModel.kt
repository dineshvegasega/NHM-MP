package com.nhm.distributor.screens.main.distribution

import androidx.lifecycle.ViewModel
import com.nhm.distributor.networking.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DistributionViewModel @Inject constructor(private val repository: Repository): ViewModel() {

    var name = ""
    var fatherHusbandType = 1
    var fatherHusband = ""
    var mother = ""
    var gender = ""
    var age = ""
    var height = ""
    var weight = ""
    var numberOfMembers = ""
    var numberOfChildren = ""
    var address = ""
    var dmcName = ""
    var block = ""
    var mobileNumber = ""
    var districtState = ""
    var cardTypeAPLBPL = ""

    var typeOfPatient = ""
    var patientCheckupDate = ""
    var hemoglobinLevelAge = ""
    var hemoglobinCheckupDate = ""
    var muktiID = ""
    var nakshayID = ""
    var aadhaarNumber = ""
    var business = ""
    var bankAccount = ""
    var bankIFSC = ""
    var treatmentSupporterName = ""
    var treatmentSupporterPost = ""
    var treatmentSupporterMobileNumber = ""
    var treatmentSupporterEndDate = ""
    var treatmentSupporterResult = ""

    var foodMonth = ""
    var foodDate = ""
    var foodHeight = ""
    var foodSignature = ""
    var foodItemImage = ""
    var foodIdentityImage = ""
}