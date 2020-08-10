package com.example.robin.roomwordsample.adapter

import com.example.robin.roomwordsample.data.Task

sealed class Action
class ToggleTaskStatus(val id: Int, val mark: Boolean): Action()
class RemoveTask(val task: Task): Action()
class RestoreTask(val task: Task): Action()
class UpdateTask(val id: Int, val word: String, val description: String): Action()
class OpenTaskDetailsBottomSheet(val task: Task): Action()