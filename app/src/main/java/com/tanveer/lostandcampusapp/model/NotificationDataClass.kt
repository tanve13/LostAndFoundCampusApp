package com.tanveer.lostandcampusapp.model

data class NotificationDataClass( val id: String = "",
                                  val title: String = "",
                                  val message: String = "",
                                  val timestamp: Long = System.currentTimeMillis(),
                                  val type: String = "",
                                  val userId: String? = null,
                                  val isRead: Boolean = false)
