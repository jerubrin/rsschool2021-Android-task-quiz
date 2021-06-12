package com.rsschool.quiz

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.core.view.forEach
import androidx.core.view.forEachIndexed
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.rsschool.quiz.databinding.FragmentQuizBinding

class QuizFragment : Fragment() {
    private var _binding: FragmentQuizBinding? = null
    private val binding get() = _binding!!

    var keysArray :IntArray? = null
    private var selectedAns = -1

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
        keysArray = fragmentArgs.keys
        if (keysArray != null) binding.previousButton.isEnabled = true

        //--------------------------
        binding.question.text = "["
        keysArray?.forEach {
            binding.question.text = "${binding.question.text} $it"
        }
        binding.question.text = "${binding.question.text}]"
        //--------------------------

        binding.radioGroup.setOnCheckedChangeListener { group, _ ->
            group.forEachIndexed { index, viewRB ->
                if ((viewRB as RadioButton).isChecked) {
                    binding.nextButton.isEnabled = true
                    selectedAns = index
                }
            }
        }

        binding.nextButton.setOnClickListener {
            keysArray = if (keysArray == null) intArrayOf(selectedAns) else keysArray?.plus(intArrayOf(selectedAns))
            val actionForward = QuizFragmentDirections.actionQuizFragmentSelf(keysArray)
            findNavController().navigate(actionForward)
        }

        binding.previousButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}