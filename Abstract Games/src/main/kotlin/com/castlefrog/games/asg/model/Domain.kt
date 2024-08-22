package com.castlefrog.games.asg.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Domain(val type: DomainType, val params: Map<String, String>) : Parcelable
