package com.github.xjcyan1de.cloudcontrol.console.animation.question

import com.github.xjcyan1de.cyanlibz.localization.textOf

interface QuestionAnswerType<T> {
    fun isValidInput(input: String): Boolean
    fun parse(input: String): T

    val possibleAnswers: List<String>
    val completableAnswers: List<String>
        get() = emptyList()

    val recommendation: String?
        get() = null

    val possibleAnswersAsString: String
        get() = possibleAnswers.joinToString()

    fun getInvalidInputMessage(input: String?): String? {
        val possibleAnswers = possibleAnswers
        return textOf("ca.question.list.question_list", "values" to { possibleAnswersAsString }).get()
    }
}