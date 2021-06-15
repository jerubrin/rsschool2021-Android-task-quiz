package com.rsschool.quiz

import android.content.res.XmlResourceParser


class DataQuizXMLParser(xmlResourceParser: XmlResourceParser) {
    private val questionList = mutableListOf<Question>()
    private data class Question (var question: String, val answers: MutableList<String>)
    private var keysArray = intArrayOf()

    init {
        while (xmlResourceParser.eventType != XmlResourceParser.END_DOCUMENT){
            if (xmlResourceParser.eventType == XmlResourceParser.START_TAG) {
                when (xmlResourceParser.name) {
                    "question" -> questionList.add(Question("", mutableListOf()))
                    "title" -> questionList[questionList.lastIndex].question = xmlResourceParser.nextText()
                    "answer" -> questionList[questionList.lastIndex].answers.add(xmlResourceParser.nextText())
                    "key" -> keysArray += intArrayOf(xmlResourceParser.nextText().toInt() - 1)
                }
            }
            xmlResourceParser.next()
        }
    }

    fun lastQuestionNumber() : Int {
        return questionList.lastIndex
    }

    fun getQuestion(questionNumber: Int) : String {
        return questionList[questionNumber].question
    }

    fun getAnswerList(questionNumber: Int) : MutableList<String> {
        return questionList[questionNumber].answers
    }

    fun getAnswer(questionNumber: Int, index: Int): String{
        return questionList[questionNumber].answers[index]
    }

    fun getKeys() : IntArray {
        return keysArray
    }
}