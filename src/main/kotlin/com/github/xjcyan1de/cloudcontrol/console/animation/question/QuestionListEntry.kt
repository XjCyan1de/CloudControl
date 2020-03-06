package com.github.xjcyan1de.cloudcontrol.console.animation.question

data class QuestionListEntry<T>(
    val key: String,
    val question: String,
    val answerType: QuestionAnswerType<T>
)