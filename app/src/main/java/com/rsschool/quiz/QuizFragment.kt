package com.rsschool.quiz

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.core.os.bundleOf
import androidx.core.view.forEach
import androidx.core.view.forEachIndexed
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.rsschool.quiz.databinding.FragmentQuizBinding
import kotlinx.coroutines.handleCoroutineException

class QuizFragment : Fragment() {
    private var _binding: FragmentQuizBinding? = null
    private val binding get() = _binding!!

    private var selectedAns = -1
    private var currentQuestion = 0
    private var keysArray :IntArray? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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
        keysArray = fragmentArgs.ansver
        currentQuestion = fragmentArgs.currentQuestion

        if (currentQuestion != 0) binding.previousButton.isEnabled = true

        val wasSelecting = getSelectedFromKeysArray(keysArray, currentQuestion)
        if (wasSelecting >= 0) {
            var rb: RadioButton = binding.radioGroup[wasSelecting] as RadioButton
            rb.setChecked(true)
            binding.nextButton.isEnabled = true
            selectedAns = wasSelecting
        }

        binding.radioGroup.setOnCheckedChangeListener { group, _ ->
            group.forEachIndexed { index, viewRB ->
                if ((viewRB as RadioButton).isChecked) {
                    binding.nextButton.isEnabled = true
                    selectedAns = index
                }
            }
            keysArray = setKeyToKeyArray(keysArray, selectedAns, currentQuestion)
        }

        binding.nextButton.setOnClickListener {
            val actionForward = QuizFragmentDirections.actionQuizFragmentSelf(keysArray, currentQuestion + 1)
            findNavController().navigate(actionForward)
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

        var keysArrayBack =
            findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<IntArray>("answersBack")?.value
        if (keysArrayBack != null) {
            keysArray = keysArrayBack
        }
        //--------------------------
        binding.question.text = "["
        keysArray?.forEach {
            binding.question.text = "${binding.question.text} $it"
        }
        binding.question.text = "${binding.question.text}] currentQ=$currentQuestion"
        //--------------------------
        keysArray = setKeyToKeyArray(keysArray, selectedAns, currentQuestion)
    }

    private fun setKeyToKeyArray(intArray: IntArray?, answer: Int, currentQ: Int): IntArray? {
        var newArray = intArray
        if (newArray == null) {
            newArray = intArrayOf(answer)
        } else {
            if (newArray?.lastIndex!! < currentQ) {
                newArray = newArray?.plus(intArrayOf(answer))
            } else {
                newArray!![currentQ] = answer
            }
        }
        if (newArray != null) {
            findNavController().previousBackStackEntry?.savedStateHandle?.set("answersBack", newArray)
        }
        return newArray
    }

    private fun getSelectedFromKeysArray(intArray: IntArray?, currentQ: Int): Int {
        var newArray: IntArray? = intArray ?: return -1
        if (newArray?.lastIndex!! < currentQ) return -1
        return newArray[currentQ]
    }
}