package com.example.calculator

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.calculator.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private val viewModel : CalculatorViewModel by  viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setOnClickListeners()
        observeStates()

    }

    private fun observeStates() {
        lifecycleScope.launch {
            viewModel.state.collect { state ->
                binding.lastCalcText.text = state.lastLine
                binding.resultText.text = state.currentLine
            }
        }
    }

    private fun setOnClickListeners() {
        val allButtons = listOf(
            binding.btnClear,
            binding.btnBackspace,
            binding.btnModulo,
            binding.btnDivide,
            binding.btnMultiply,
            binding.btnPlus,
            binding.btnMinus,
            binding.btnEquals,
            binding.btnPosNeg,
            binding.btnDot,
            binding.btnZero,
            binding.btnOne,
            binding.btnTwo,
            binding.btnThree,
            binding.btnFour,
            binding.btnFive,
            binding.btnSix,
            binding.btnSeven,
            binding.btnEight,
            binding.btnNine
        )

        allButtons.forEach { button ->
            button.setOnClickListener {buttonClicked ->
                when (val tag = buttonClicked.tag.toString()) {
                    in "0".."9" -> viewModel.onNumberClick(tag)
                    "DOT" -> viewModel.onFloatPointClick()
                    "CLEAR" -> viewModel.onClearAllClick()
                    "BACKSPACE" -> viewModel.onBackspaceClick()
                    "EQUAL" -> viewModel.onEqualClick()
                    "NEGATE" -> viewModel.onNegationClick()
                    else -> {
                        val operation = Operation.valueOf(tag)
                        viewModel.onOperationClick(operation)
                    }
                }
            }
        }
    }
}