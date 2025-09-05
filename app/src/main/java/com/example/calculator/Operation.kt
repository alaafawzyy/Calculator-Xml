package com.example.calculator

enum class Operation {
    ADD,
    SUB,
    MULTy,
    DIV,
    REMINDER,
}

fun Operation.toStringChar(): String {
    return when (this) {
        Operation.ADD -> "+"
        Operation.SUB -> "-"
        Operation.MULTy -> "x"
        Operation.DIV -> "/"
        Operation.REMINDER -> "%"
    }
}