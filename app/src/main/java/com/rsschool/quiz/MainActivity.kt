package com.rsschool.quiz

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.rsschool.quiz.DataQuizXMLParser as DataXMLParser

class MainActivity : AppCompatActivity() {
    internal lateinit var dataXMLParser :DataXMLParser
    fun get(): DataXMLParser {return dataXMLParser}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataXMLParser = DataXMLParser( resources.getXml(R.xml.quizdata) )
        setContentView(R.layout.activity_main)
    }
}