package com.rsschool.quiz

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.core.view.forEachIndexed
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.rsschool.quiz.databinding.FragmentQuizBinding

class QuizFragment : Fragment() {
    private var _binding: FragmentQuizBinding? = null
    private val binding get() = _binding!!

    private var selectedAns = -1
    private var currentQuestion = 0
    private var keysArray :IntArray = intArrayOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQuizBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fragmentArgs : QuizFragmentArgs by navArgs()
        keysArray = fragmentArgs.ansver ?: intArrayOf()
        currentQuestion = fragmentArgs.currentQuestion
        val dataXMLParser = (activity as MainActivity).dataXMLParser

        selectedAns = checkRadioButtonAnswerFromKeyArray(keysArray, currentQuestion)

        keysArray = getBackKeyArray(keysArray)
        setBackKeyArray(keysArray)

        readQuestionFromXML(dataXMLParser)

        if (currentQuestion != 0) binding.previousButton.isEnabled = true

        binding.toolbar.title = resources.getString( R.string.toolbar_title_next ) + " ${currentQuestion + 1}"

        //listeners
        binding.radioGroup.setOnCheckedChangeListener { group, _ ->
            group.forEachIndexed { index, viewRB ->
                if ((viewRB as RadioButton).isChecked) {
                    binding.nextButton.isEnabled = true
                    selectedAns = index
                }
            }
            keysArray = setAnswerToKeyArray(keysArray, selectedAns, currentQuestion)
            setBackKeyArray(keysArray)
        }

        binding.nextButton.setOnClickListener {
            if (binding.nextButton.text != resources.getString(R.string.button_text_submit)) {
                val actionForward =
                    QuizFragmentDirections.actionQuizFragmentSelf(keysArray, currentQuestion + 1)
                findNavController().navigate(actionForward)
            } else {
                val actionSubmit =
                    QuizFragmentDirections.actionQuizFragmentToResultFragment(keysArray)
                findNavController().navigate(actionSubmit)
            }
        }

        binding.previousButton.setOnClickListener {
            if (findNavController().previousBackStackEntry != null)
                findNavController().popBackStack()
        }

        binding.toolbar.setNavigationOnClickListener {
            if (findNavController().previousBackStackEntry != null)
                findNavController().popBackStack()
        }
    }

    private fun setAnswerToKeyArray(intArray: IntArray, answer: Int, currentQ: Int): IntArray {
        var newArray = intArray
        if (newArray.lastIndex < currentQ)
            newArray += intArrayOf(answer)
        else
            newArray[currentQ] = answer
        return newArray
    }

    private fun setBackKeyArray(intArray: IntArray) {
        if (intArray.isNotEmpty()) {
            findNavController().previousBackStackEntry?.savedStateHandle?.set("answersBack", intArray)
        }
    }

    private fun getAnswerFromKeyArray(intArray: IntArray, currentQ: Int): Int {
        val newArray: IntArray = intArray
        if (newArray.lastIndex < currentQ) return -1
        return newArray[currentQ]
    }

    private fun getBackKeyArray(intArray: IntArray) :IntArray {
        val keysArrayBack =
            findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<IntArray>("answersBack")?.value
        return keysArrayBack ?: intArray
    }

    private fun checkRadioButtonAnswerFromKeyArray(intArray: IntArray, currentQ: Int) : Int {
        val wasSelecting = getAnswerFromKeyArray(intArray, currentQ)
        if (wasSelecting >= 0) {
            val rb: RadioButton = binding.radioGroup[wasSelecting] as RadioButton
            rb.isChecked = true
            binding.nextButton.isEnabled = true
        }
        return wasSelecting
    }

    private fun readQuestionFromXML(dataXMLParser: DataQuizXMLParser) {
        if (currentQuestion >= dataXMLParser.lastQuestionNumber()){
            binding.nextButton.text = resources.getString(R.string.button_text_submit)
        }
        binding.question.text = dataXMLParser.getQuestion(currentQuestion)
        dataXMLParser.getAnswerList(currentQuestion).forEachIndexed { index, str ->
            (binding.radioGroup[index] as RadioButton).text = str
        }
    }
}