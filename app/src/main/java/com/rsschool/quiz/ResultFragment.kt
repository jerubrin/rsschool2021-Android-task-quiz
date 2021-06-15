package com.rsschool.quiz

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.rsschool.quiz.databinding.FragmentResultBinding

class ResultFragment : Fragment() {
    private var _binding: FragmentResultBinding? = null
    private val binding get() = _binding !!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val fragmentArgs: ResultFragmentArgs by navArgs()
        findNavController().previousBackStackEntry?.savedStateHandle?.set("answersBack", intArrayOf())

        val answers = fragmentArgs.answer
        val correctKeys = fragmentArgs.correctKeys

        var points = 0
        answers.forEachIndexed { i, it -> points += if(it == correctKeys[i]) 1 else 0 }
        val outOfText = resources.getText(R.string.out_of)
        val allQuestions = answers.lastIndex+1

        binding.results.text = "$points$outOfText$allQuestions"
        binding.present.text = "(${(points*100).div(allQuestions)}%)"

        binding.buttonBack.setOnClickListener { findNavController().popBackStack() }
        binding.buttonExit.setOnClickListener { activity?.finish() }
        binding.buttonShare.setOnClickListener {
            val sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TITLE, getEmailTitle(points, allQuestions))
                putExtra(Intent.EXTRA_TEXT, getEmailText(points, allQuestions, answers))
                type = "text/html"
            }
            val intentTitleText = resources.getText(R.string.choose_client)
            startActivity(Intent.createChooser(sendIntent, intentTitleText))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getEmailTitle(points: Int, allQuestions: Int): String {
        val outOfText = resources.getText(R.string.out_of)
        val answered = resources.getText(R.string.i_result_answered)

        return  "$answered $points$outOfText$allQuestions (${(points*100).div(allQuestions)}%)"
    }

    private fun getEmailText(points: Int, allQuestions: Int, answers: IntArray) :String{
        val outOfText = resources.getText(R.string.out_of)
        val answered = resources.getText(R.string.i_result_answered)
        val listAnswersTitle = resources.getText(R.string.list_of_question_title)

        return  "$answered $points$outOfText$allQuestions\n" +
                "(${(points*100).div(allQuestions)}%)\n" +
                "\n$listAnswersTitle\n\n" +
                getAnswers(answers)
    }

    private fun getAnswers(answers: IntArray): String{
        var rString = ""
        val myAnswer = resources.getText(R.string.my_answer)
        (activity as MainActivity).dataXMLParser.apply {
            for(i in 0..lastQuestionNumber()){
                rString += ("${i+1}) ${getQuestion(i)}\n" +
                            "$myAnswer ${getAnswer(i, answers[i])}\n\n")
            }
        }
        return rString
    }
}