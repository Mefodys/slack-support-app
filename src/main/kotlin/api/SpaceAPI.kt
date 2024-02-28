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
    private fun createSession() = SpaceClient(
        ktorClient = ktorClientForSpace(),
        serverUrl = "https://jetbrains.team",
        token = token)



    private var client: SpaceClient = createSession()

    private fun recreateSession() {
        client = createSession()
    }

    // (Email -> 4 Projects max)
    suspend fun getEmailToFirst4TeamNames(users: List<User>): MutableMap<String, MutableList<String>> {

        val listOfTeamID = mutableListOf<String>()
        val mapForEmailAndTeam = mutableMapOf<String, MutableList<String>>()

        println("starting to get each user from Space...")

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

                print("${user.name}, ")

            } catch (e: Exception) {
                println("Failed on  name: ${user.name} email: ${user.email} ")
                println(e.message + "\n")
            }
        }
        println("\n\nGot ${users.count()} users")

        recreateSession()

        println("starting to get teams of each user...")
        val teamIdToName = listOfTeamID.map { teamId ->
            val teamName = client.teamDirectory.teams.getTeam(
                id = TeamIdentifier.Id(teamId)
            ) {
                name()
            }
            print("${teamName?.name}, ")

            teamId to teamName?.name.toString()
        }.toMap()

        println()

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




