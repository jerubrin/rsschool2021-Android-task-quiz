package com.rsschool.quiz


import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.appcompat.view.ContextThemeWrapper
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
    private var themesArray = intArrayOf(
        R.style.Theme_Quiz_First,
        R.style.Theme_Quiz_Second,
        R.style.Theme_Quiz_Third,
        R.style.Theme_Quiz_Fourth,
        R.style.Theme_Quiz_Fives,
        R.style.Theme_Quiz_Sixth,
        R.style.Theme_Quiz_Seventh,
        R.style.Theme_Quiz_Eighth,
        R.style.Theme_Quiz_Ninth,
        R.style.Theme_Quiz_Tenth,
        R.style.Theme_Quiz_Eleventh,
        R.style.Theme_Quiz_Twelves,
        R.style.Theme_Quiz_Thirteenth,
        R.style.Theme_Quiz_Fourteenth,
        R.style.Theme_Quiz_Fifteenth
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val fragmentArgs : QuizFragmentArgs by navArgs()
        keysArray = fragmentArgs.answer ?: intArrayOf()
        currentQuestion = fragmentArgs.currentQuestion

        val contextThemeWrapper = ContextThemeWrapper(
            activity,
            themesArray[currentQuestion % (themesArray.lastIndex + 1)]
        )
        val localInflater = inflater.cloneInContext(contextThemeWrapper)
        val typedValue = TypedValue()
        contextThemeWrapper.theme?.resolveAttribute(android.R.attr.statusBarColor, typedValue, true)
        activity?.window?.statusBarColor = typedValue.data

        _binding = FragmentQuizBinding.inflate(localInflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dataXMLParser = (activity as MainActivity).dataXMLParser

        if (currentQuestion != 0) binding.previousButton.isEnabled = true
        else binding.toolbar.navigationIcon = null



        readQuestionFromXML(dataXMLParser)

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
                val correctKeys = dataXMLParser.getKeys()
                val actionSubmit =
                    QuizFragmentDirections.actionQuizFragmentToResultFragment(keysArray, correctKeys)
                while (findNavController().previousBackStackEntry != null) {
                    findNavController().popBackStack()
                }
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

    override fun onResume() {
        super.onResume()
        keysArray = getBackKeyArray(keysArray)
        selectedAns = checkRadioButtonAnswerFromKeyArray(keysArray, currentQuestion)
        setBackKeyArray(keysArray)
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
        if (findNavController().previousBackStackEntry != null) {
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
        } else {
            binding.radioGroup.clearCheck()
            binding.nextButton.isEnabled = false
        }
        return wasSelecting
    }

    private fun readQuestionFromXML(dataXMLParser: DataQuizXMLParser) {
        if (currentQuestion >= dataXMLParser.lastQuestionNumber()){
            binding.nextButton.text = resources.getString(R.string.button_text_submit)
        }
        binding.question.text = dataXMLParser.getQuestion(currentQuestion)
        dataXMLParser.getAnswerList(currentQuestion).forEachIndexed { index, str ->
            (binding.radioGroup[index] as RadioButton).apply {
                text = str
                visibility = View.VISIBLE
            }
        }
    }
}