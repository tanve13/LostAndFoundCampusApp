package com.tanveer.lostandcampusapp.model

data class AdminNotificationDataClass(val id: String,
                                      val type: String,        // "NEW_POST" or "CLAIMED"
                                      val title: String,
                                      val subtitle: String,
                                      val timestamp: Long,
                                      var read: Boolean = false,
                                      val refId: String = "",
                                      val message: String = "",
                                      val time: String = "")
