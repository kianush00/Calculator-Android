package com.example.calculator
import com.example.calculator.Constants.OPERATOR_DIV
import com.example.calculator.Constants.OPERATOR_MULTI
import com.example.calculator.Constants.OPERATOR_NULL
import com.example.calculator.Constants.OPERATOR_SUB
import com.example.calculator.Constants.OPERATOR_SUM
import com.example.calculator.Constants.POINT

class Operations {
    companion object {
        fun canReplaceOperator(charSequence: CharSequence): Boolean {
            if (charSequence.length < 2) return false

            val lastElement = charSequence[charSequence.length-1].toString()
            val penultimateElement = charSequence[charSequence.length-2].toString()

            return ((lastElement == OPERATOR_MULTI
                    || lastElement == OPERATOR_DIV
                    || lastElement == OPERATOR_SUM) &&
                    (penultimateElement == OPERATOR_MULTI
                    || penultimateElement == OPERATOR_DIV
                    || penultimateElement == OPERATOR_SUM
                    || penultimateElement == OPERATOR_SUB))
                    ||
                    ((lastElement == OPERATOR_SUB) &&
                    (penultimateElement == OPERATOR_SUB
                    || penultimateElement == OPERATOR_SUM))
        }

        fun tryResolve(operationRef: String, isFromResolve: Boolean, listener: OnResolveListener) {
            if (operationRef.isEmpty()) return

            var operation = operationRef
            val lastElementIsPoint = operationRef.contains(POINT)
                    && operationRef.lastIndexOf(POINT) == operationRef.length-1

            //delete last point if it's the last element
            if (lastElementIsPoint) {
                operation = operationRef.substring(0, operationRef.length-1)
            }

            val operator = getOperator(operation)

            //split values of operation
            val values = getValues(operator, operation)

            if (values.size > 1) {
                try {
                    val numberOne = values[0]!!.toDouble()
                    val numberTwo = values[1]!!.toDouble()

                    listener.onShowResult(getResult(numberOne, numberTwo, operator))
                } catch (e: java.lang.NumberFormatException) {
                    if (isFromResolve) listener.onShowMessage(R.string.message_num_incorrect)
                }
            } else {
                if (isFromResolve && operator != OPERATOR_NULL)
                    listener.onShowMessage(R.string.message_exp_incorrect)
            }
        }

        private fun getResult(firstNumber: Double, secondNumber: Double, operator: String) : Double {
            return when(operator) {
                OPERATOR_MULTI -> firstNumber * secondNumber
                OPERATOR_DIV -> firstNumber / secondNumber
                OPERATOR_SUM -> firstNumber + secondNumber
                else -> firstNumber - secondNumber  //OPERATOR_SUB
            }
        }

        fun getValues(operator: String, operation: String): Array<String?> {
            var values = arrayOfNulls<String>(0)

            if (operator != OPERATOR_NULL) {
                if (operator == OPERATOR_SUB) {
                    val indexLastSub = operation.lastIndexOf(OPERATOR_SUB)
                    val lastElementIsSub = indexLastSub >= operation.length-1

                    if (lastElementIsSub) {
                        values = arrayOfNulls(1)
                        values[0] = operation.substring(0, indexLastSub)
                    } else {
                        values = arrayOfNulls(2)
                        values[0] = operation.substring(0, indexLastSub)
                        values[1] = operation.substring(indexLastSub+1)
                    }
                } else {    //operator isn't substract
                    values = operation.split(operator).dropLastWhile { it == "" } .toTypedArray()
                }
            }

            return values
        }

        fun getOperator(operation: String): String {
            return if (operation.contains(OPERATOR_MULTI)) OPERATOR_MULTI
            else if (operation.contains(OPERATOR_DIV)) OPERATOR_DIV
            else if (operation.contains(OPERATOR_SUM)) OPERATOR_SUM
            else if (operation.lastIndexOf(OPERATOR_SUB) > 0) OPERATOR_SUB
            else OPERATOR_NULL
        }
    }
}