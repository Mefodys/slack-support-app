package api

import space.jetbrains.api.runtime.SpaceClient
import space.jetbrains.api.runtime.ktorClientForSpace
import space.jetbrains.api.runtime.resources.teamDirectory
import space.jetbrains.api.runtime.types.TeamIdentifier
import types.User
import kotlin.collections.set
import kotlin.collections.take
import kotlin.collections.toMutableList
import kotlin.toString

class SpaceAPI(
    private val token: String,
) {
    private val client: SpaceClient = SpaceClient(
        ktorClient = ktorClientForSpace(),
        serverUrl = "https://jetbrains.team",
        token = token
    )

    // (Email -> 4 Projects max)
    suspend fun getEmailToFirst4TeamNames(users: List<User>): MutableMap<String, MutableList<String>> {

        val listOfTeamID = mutableListOf<String>()
        val mapForEmailAndTeam = mutableMapOf<String, MutableList<String>>()

        //Mef comment: Create a list of teamIDs (using user email)
        for (user in users) {
            try {
                val infoFromSpace = client.teamDirectory.profiles.getProfileByEmail(
                    email = user.email
                ) {
                    //username()
                    memberships()
                }

                val memberships = infoFromSpace.memberships
                val tempListOfTeamIDs = mutableListOf<String>()
                for (i in memberships) {
                    listOfTeamID.add(i.team.id)
                    tempListOfTeamIDs.add(i.team.id)
                }
                mapForEmailAndTeam[user.email] = tempListOfTeamIDs


            } catch (_: Exception) {
                println("Failed on $user ")
            }
        }

        val teamIdToName = listOfTeamID.map { teamId ->
            val teamName = client.teamDirectory.teams.getTeam(
                id = TeamIdentifier.Id(teamId)
            ) {
                name()
            }
            teamId to teamName?.name.toString()
        }.toMap()


        val newMapForEmailAndTeam = mutableMapOf<String, MutableList<String>>()
        var tempTrueNames = mutableListOf<String>()

        for (i in mapForEmailAndTeam) {
            var counter = i.value.size
            if (i.value.size > 1) {

                if (counter <= 0) {
                    break
                } else {
                    for (j in i.value) {
                        for (k in teamIdToName) {
                            if (j == k.key) {
                                tempTrueNames.add(k.value)
                            }
                        }
                        counter -= 1
                    }
                }
            } else {
                for (j in i.value) {
                    for (k in teamIdToName) {
                        if (j == k.key) {
                            tempTrueNames.add(k.value)
                        }
                    }
                }
            }

            if (tempTrueNames.size <= 4) {
                val listSize = tempTrueNames.size
                val countOfNull = 4 - listSize
                repeat(countOfNull) { tempTrueNames.add("null") }
            } else {
                tempTrueNames = tempTrueNames.take(4).toMutableList()
            }

            newMapForEmailAndTeam[i.key] = tempTrueNames
            tempTrueNames = mutableListOf()
        }

        return newMapForEmailAndTeam
    }
}




