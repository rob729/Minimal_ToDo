package com.example.robin.roomwordsample.adapter

interface ListActionPerformer<Action> {
    fun performAction(action: Action)
}