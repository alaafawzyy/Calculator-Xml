package com.example.calculator

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.Stack

class CalculatorViewModel : ViewModel() {

    private val _state = MutableStateFlow(CalculatorData())
    val state = _state.asStateFlow()
    private val stack = Stack<String>()
    private var hasFloatingPoint = false

     fun onClearAllClick() {
        stack.clear()
        hasFloatingPoint = false
        _state.update { it.copy(lastLine = "", currentLine = "") }
    }

     fun onBackspaceClick() {
        if (stack.isEmpty()) return
        val last = stack.pop()
        when {
            last.isNumber() -> {
                val newNumber = last.dropLast(1)
                if (newNumber.isNotEmpty() && newNumber != "-") stack.push(newNumber)
                hasFloatingPoint = newNumber.contains('.')
            }
            last == "." -> hasFloatingPoint = false
        }
        updateCurrentLine()
    }

     fun onNumberClick(digit: String) {
        if (stack.isEmpty()) {
            if (digit != "0") stack.push(digit)
        } else if (stack.peek().isNumber()) {
            if (stack.peek() != "0") stack.push(stack.pop() + digit)
            else stack.push(digit)
        } else {
            stack.push(digit)
        }
        updateCurrentLine()
    }

     fun onOperationClick(operation: Operation) {
        if (stack.isNotEmpty() && stack.peek().isNumber()) {
            stack.push(operation.toStringChar())
            hasFloatingPoint = false
        }
        updateCurrentLine()
    }

     fun onFloatPointClick() {
        if (!hasFloatingPoint && stack.isNotEmpty() && stack.peek().isNumber()) {
            stack.push(stack.pop() + ".")
            hasFloatingPoint = true
        } else if (!hasFloatingPoint) {
            stack.push(".")
            hasFloatingPoint = true
        }
        updateCurrentLine()
    }

     fun onNegationClick() {
        if (stack.size == 1 && stack.peek().isNumber()) {
            val number = stack.pop()
            stack.push(if (number.startsWith("-")) number.drop(1) else "-$number")
        }
        updateCurrentLine()
    }

     fun onEqualClick() {
        if (stack.isNotEmpty() && stack.peek().isNumber()) {
            _state.update { it.copy(lastLine = stack.joinToString("")) }
            val result = evaluate()
            stack.clear()
            hasFloatingPoint = result.contains(".")
            if (result != "Error" && result.isNotEmpty()) stack.push(result)
            _state.update { it.copy(currentLine = result) }
        }
    }

    private fun String.isNumber(): Boolean {
        return this.matches("-?\\d+(\\.\\d+)?".toRegex()) || this == "0" || this.matches("-?\\d+".toRegex())
    }

    private fun evaluate(): String {
        if (stack.isEmpty()) return "0"
        try {
            val expression = stack.joinToString(" ") { it }
            val parts = expression.split(" ").filter { it.isNotEmpty() }
            if (parts.size == 1) return parts[0]

            val numbers = mutableListOf<Float>()
            val operations = mutableListOf<String>()
            parts.forEachIndexed { i, part ->
                if (i % 2 == 0) numbers.add(part.toFloatOrNull() ?: return "Error")
                else operations.add(part)
            }

            var i = 0
            while (i < operations.size) {
                if (operations[i] in listOf("x", "/", "%")) {
                    val result = when (operations[i]) {
                        "x" -> numbers[i] * numbers[i + 1]
                        "/" -> if (numbers[i + 1] == 0f) return "Error" else numbers[i] / numbers[i + 1]
                        "%" -> if (numbers[i + 1] == 0f) return "Error" else numbers[i] % numbers[i + 1]
                        else -> return "Error"
                    }
                    numbers[i] = result
                    numbers.removeAt(i + 1)
                    operations.removeAt(i)
                } else {
                    i++
                }
            }

            var result = numbers[0]
            operations.forEachIndexed { i, op ->
                result = when (op) {
                    "+" -> result + numbers[i + 1]
                    "-" -> result - numbers[i + 1]
                    else -> return "Error"
                }
            }
            return if (result == result.toLong().toFloat()) result.toLong().toString() else result.toString()
        } catch (e: Exception) {
            return "Error"
        }
    }

    private fun updateCurrentLine() {
        _state.update { it.copy(currentLine = stack.joinToString("")) }
    }
}