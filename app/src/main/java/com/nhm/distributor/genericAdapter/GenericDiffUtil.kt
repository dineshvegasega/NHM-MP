package com.nhm.distributor.genericAdapter

import android.annotation.SuppressLint
import androidx.recyclerview.widget.DiffUtil


/** You pass <M> data class type
 * and its received in <T> data
 * class type.
 * */

class GenericDiffUtil<T> : DiffUtil.ItemCallback<T>() {
    /** Check old item and new item are same
     *  If they are both same */
//    override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
//        return oldItem?.equals(newItem) ?: false
//    }
//
//    @SuppressLint("DiffUtilEquals")
//    override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
//        return oldItem?.equals(newItem) ?: false
//    }

    override fun areItemsTheSame(oldItem: T & Any, newItem: T & Any): Boolean {
        return oldItem?.equals(newItem) ?: false
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: T & Any, newItem: T & Any): Boolean {
        return oldItem?.equals(newItem) ?: false
    }
}