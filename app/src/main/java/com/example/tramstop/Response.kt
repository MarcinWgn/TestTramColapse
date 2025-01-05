package com.example.tramstop

import com.google.gson.annotations.SerializedName

data class Response(

	@field:SerializedName("actual")
	val actual: List<ActualItem?>? = null,

	@field:SerializedName("routes")
	val routes: List<RoutesItem?>? = null,

	@field:SerializedName("directions")
	val directions: List<Any?>? = null,

	@field:SerializedName("old")
	val old: List<OldItem?>? = null,

	@field:SerializedName("stopShortName")
	val stopShortName: String? = null,

	@field:SerializedName("firstPassageTime")
	val firstPassageTime: Long? = null,

	@field:SerializedName("lastPassageTime")
	val lastPassageTime: Long? = null,

	@field:SerializedName("generalAlerts")
	val generalAlerts: List<Any?>? = null,

	@field:SerializedName("stopName")
	val stopName: String? = null
)

data class RoutesItem(

	@field:SerializedName("alerts")
	val alerts: List<Any?>? = null,

	@field:SerializedName("directions")
	val directions: List<String?>? = null,

	@field:SerializedName("authority")
	val authority: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("shortName")
	val shortName: String? = null,

	@field:SerializedName("routeType")
	val routeType: String? = null
)

data class OldItem(

	@field:SerializedName("routeId")
	val routeId: String? = null,

	@field:SerializedName("mixedTime")
	val mixedTime: String? = null,

	@field:SerializedName("plannedTime")
	val plannedTime: String? = null,

	@field:SerializedName("passageid")
	val passageid: String? = null,

	@field:SerializedName("tripId")
	val tripId: String? = null,

	@field:SerializedName("actualRelativeTime")
	val actualRelativeTime: Int? = null,

	@field:SerializedName("vehicleId")
	val vehicleId: String? = null,

	@field:SerializedName("patternText")
	val patternText: String? = null,

	@field:SerializedName("direction")
	val direction: String? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class ActualItem(

	@field:SerializedName("actualTime")
	val actualTime: String? = null,

	@field:SerializedName("routeId")
	val routeId: String? = null,

	@field:SerializedName("mixedTime")
	val mixedTime: String? = null,

	@field:SerializedName("plannedTime")
	val plannedTime: String? = null,

	@field:SerializedName("passageid")
	val passageid: String? = null,

	@field:SerializedName("tripId")
	val tripId: String? = null,

	@field:SerializedName("actualRelativeTime")
	val actualRelativeTime: Int? = null,

	@field:SerializedName("vehicleId")
	val vehicleId: String? = null,

	@field:SerializedName("patternText")
	val patternText: String? = null,

	@field:SerializedName("direction")
	val direction: String? = null,

	@field:SerializedName("status")
	val status: String? = null
)
