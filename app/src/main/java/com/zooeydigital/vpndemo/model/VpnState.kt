package com.zooeydigital.vpndemo.model

/**
 * Created by Clayton Hatathlie on 6/30/25
 **/
enum class VpnState {
    RUNNING,
    NOT_RUNNING,
}

val VpnState.label: String
    get() = when (this) {
        VpnState.RUNNING -> "Running"
        VpnState.NOT_RUNNING -> "Not Running"
    }