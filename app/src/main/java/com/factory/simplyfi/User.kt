package com.factory.simplyfi

class User(val uid : String, val username : String, val photoUrl : String, val email : String, val accountType : String, val xp : Int, val skillsRead : Int, val teamID : String){
    constructor() : this("","", "", "","", 0, 0, "")
}