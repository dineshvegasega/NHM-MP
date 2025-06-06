package com.nhm.distributor.screens.onboarding.quickRegistration

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class QuickRegistrationAdapter (fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {

    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 -> QuickRegistration1()
            1 -> QuickRegistration2()
            else -> QuickRegistration1()
        }
    }

    override fun getItemCount(): Int {
        return 2
    }
}