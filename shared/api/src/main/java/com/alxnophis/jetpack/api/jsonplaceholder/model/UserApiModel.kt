package com.alxnophis.jetpack.api.jsonplaceholder.model

data class UserApiModel(
    val id: Int,
    val name: String,
    val username: String,
    val email: String,
    val address: AddressApiModel,
    val phone: String,
    val website: String,
    val company: CompanyApiModel
)

data class AddressApiModel(
    val street: String,
    val suite: String,
    val city: String,
    val zipcode: String,
    val geo: GeoApiModel
)

data class GeoApiModel(
    val lat: String,
    val lng: String
)

data class CompanyApiModel(
    val name: String,
    val catchPhrase: String,
    val bs: String
)
