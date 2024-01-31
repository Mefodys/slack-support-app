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


// (Email -> 4 Projects max)
suspend fun makeMapForEmailAndTeam(users: List<User>): MutableMap<String, MutableList<String>> {

    val listOfTeamID = mutableListOf<String>()
    val mapOfTeamIDAndTeamName = mutableMapOf<String, String>()
    val mapForEmailandTeam = mutableMapOf<String, MutableList<String>>()

    val spaceToken = System.getenv("SPACE_TOKEN")
    val spaceHttpClient = ktorClientForSpace()

    val spaceClient = SpaceClient(
        ktorClient = spaceHttpClient,
        serverUrl = "https://jetbrains.team",
        token = spaceToken
    )

    //Mef comment: Create a list of teamIDs (using user email)
    for (user in users) {

        try {
            val infoFromSpace = spaceClient.teamDirectory.profiles.getProfileByEmail(
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
            mapForEmailandTeam[user.email] = tempListOfTeamIDs


        } catch (e: Exception) { println("Failed on $user ") }
    }


    //Create a map of TeamIDs+TeamName
    for (teamID in listOfTeamID) {
        val teamName = spaceClient.teamDirectory.teams.getTeam(
            id = TeamIdentifier.Id(teamID)
        ) {
            name()
        }
        mapOfTeamIDAndTeamName[teamID] = teamName?.name.toString()
    }


    val newMapForEmailandTeam = mutableMapOf<String, MutableList<String>>()
    var tempTrueNames = mutableListOf<String>()
    for (i in mapForEmailandTeam) {
        var counter = i.value.size
        if (i.value.size > 1) {

            if (counter <= 0) {
                break
            } else {
                for (j in i.value) {
                    for (k in mapOfTeamIDAndTeamName) {
                        if (j == k.key) {
                            tempTrueNames.add(k.value)
                        }

                    }
                    counter -= 1
                }
            }
        } else {
            for (j in i.value) {
                for (k in mapOfTeamIDAndTeamName) {
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

        newMapForEmailandTeam[i.key] = tempTrueNames
        tempTrueNames = mutableListOf()


    }

    return newMapForEmailandTeam
}
