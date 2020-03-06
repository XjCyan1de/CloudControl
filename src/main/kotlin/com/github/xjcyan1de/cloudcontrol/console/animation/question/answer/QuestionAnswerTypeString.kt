package com.github.xjcyan1de.cloudcontrol.console.animation.question.answer

import com.github.xjcyan1de.cloudcontrol.console.animation.question.QuestionAnswerType

open class QuestionAnswerTypeString : QuestionAnswerType<String> {
    override fun isValidInput(input: String): Boolean = true

    override fun parse(input: String): String = input

    override val possibleAnswers: List<String> = emptyList()
}