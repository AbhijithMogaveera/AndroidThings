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
    val id: Int
) {
    var messages: List<Message> = emptyList(); private set
    @Synchronized
    fun addMessage(message: Message) {
        messages = messages + message
    }
}

val userFoo = User("UserFoo")
val userBar = User("UserBar")
val AndroidGroup = Group(
    groupName = "Android",
    id = 10
).apply {
    listOf(
        Message(
            message = "Hello Bar, welcome to android group",
            user = userFoo
        ),
        Message(
            message = "Hi foo!, thanks for having me",
            user = userBar
        )
    ).forEach(this::addMessage)
}
val IosGroup = Group(
    groupName = "IOS",
    id = 11
).apply {
    listOf(
        Message(
            message = "Hello foo, welcome to ios group",
            user = userBar
        ),
        Message(
            message = "Hi bar!, thanks for having me",
            user = userFoo
        )
    ).forEach(this::addMessage)
}

val allGroups = listOf(AndroidGroup, IosGroup)