package com.alxnophis.jetpack.api.jsonplaceholder.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserApiModel(
    @SerialName("id") val id: Int,
    @SerialName("name") val name: String,
    @SerialName("username") val username: String,
    @SerialName("email") val email: String,
    @SerialName("address") val address: AddressApiModel,
    @SerialName("phone") val phone: String,
    @SerialName("website") val website: String,
    @SerialName("company") val company: CompanyApiModel,
)

@Serializable
data class AddressApiModel(
    @SerialName("street") val street: String,
    @SerialName("suite") val suite: String,
    @SerialName("city") val city: String,
    @SerialName("zipcode") val zipcode: String,
    @SerialName("geo") val geo: GeoApiModel,
)

@Serializable
data class GeoApiModel(
    @SerialName("lat") val lat: String,
    @SerialName("lng") val lng: String,
)

@Serializable
data class CompanyApiModel(
    @SerialName("name") val name: String,
    @SerialName("catchPhrase") val catchPhrase: String,
    @SerialName("bs") val bs: String,
)
