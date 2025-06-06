package com.nhm.distributor.screens.onboarding.register

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class RegisterAdapter (fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {

    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 -> Register1()
            1 -> Register2()
            2 -> Register3()
            else -> Register1()
        }
    }

    override fun getItemCount(): Int {
        return 3
    }
}