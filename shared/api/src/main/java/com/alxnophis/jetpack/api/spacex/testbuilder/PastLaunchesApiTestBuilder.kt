package com.alxnophis.jetpack.api.spacex.testbuilder

import com.alxnophis.jetpack.spacex.LaunchesQuery
import com.alxnophis.jetpack.spacex.test.LaunchesQuery_TestBuilder.Data
import com.apollographql.apollo3.annotations.ApolloExperimental
import org.jetbrains.annotations.TestOnly

@TestOnly
@ApolloExperimental
object PastLaunchesApiTestBuilder {
    val launches = LaunchesQuery.Data() {
        launches = listOf(
            launch {
                id = "id"
                mission_name = "mission_name"
                details = "details"
                launch_date_utc = null
                rocket = rocket {
                    rocket_name = "rocket_name"
                }
                launch_site = launch_site {
                    site_name = "launch_site_name"
                    site_name_long = "launch_site_name_long"
                }
                links = links {
                    mission_patch_small = "mission_patch_small"
                }
            }
        )
    }
    val nullableLaunches = LaunchesQuery.Data() {
        launches = null
    }
}
