package com.weather.info.binding

import androidx.databinding.DataBindingComponent

class BindingComponent : DataBindingComponent {
    override fun getBindingAdapters(): BindingAdapters {
        return BindingAdapters()
    }

    /*override fun getSpinnerBindings(): SpinnerBindings {
        return SpinnerBindings()
    }*/
}