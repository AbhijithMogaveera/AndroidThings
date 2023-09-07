package com.example.notifications.data.users

data class User(
    val userName: String
)

data class Message(
    val message: String,
    val user: User
)

data class Group(
    val groupName: String,
    val message: List<Message>,
    val id: Int
)

val userFoo = User("UserFoo")
val userBar = User("UserBar")
val AndroidGroup = Group(
    groupName = "Android",
    message = listOf(
        Message(
            message = "Hello Bar, welcome to android group",
            user = userFoo
        ),
        Message(
            message = "Hi foo!, thanks for having me",
            user = userBar
        )
    ),
    id = 10
)
val IosGroup = Group(
    groupName = "IOS",
    message = listOf(
        Message(
            message = "Hello foo, welcome to ios group",
            user = userBar
        ),
        Message(
            message = "Hi bar!, thanks for having me",
            user = userFoo
        )
    ),
    id = 11
)