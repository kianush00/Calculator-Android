package com.example.calculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.core.widget.addTextChangedListener
import com.example.calculator.Constants.OPERATOR_SUB
import com.example.calculator.Constants.POINT
import com.example.calculator.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvOperation.run {
            addTextChangedListener { charSequence ->
                if (Operations.canReplaceOperator(charSequence.toString())) {
                    val length = text.length
                    val newOperation ="${text.substring(0, length - 2)}${text.substring(length - 1)}"
                    text = newOperation
                }
            }
        }

    }

    fun onClickButton(view: View) {
        val valueStr = (view as Button).text.toString()
        val operation = binding.tvOperation.text.toString()

        when(view.id) {
            R.id.btnDelete -> {
                with (binding.tvOperation) {
                    if (text.isNotEmpty()) text = operation.substring(0, text.length-1)
                }
            }

            R.id.btnClear -> {
                with(binding) {
                    tvOperation.text = ""
                    tvResult.text = ""
                }
            }

            R.id.btnResolve -> checkOrResolve(operation, true)

            R.id.btnMulti,
            R.id.btnDiv,
            R.id.btnSum,
            R.id.btnSub -> {
                checkOrResolve(operation, false)
                addOperator(valueStr, operation)
            }

            R.id.btnPoint -> addPoint(valueStr, operation)

            else -> appendToOperation(valueStr)
        }
    }

    private fun addPoint(pointStr: String, operation: String) {
        if (!operation.contains(POINT)) {
            appendToOperation(pointStr)
        } else {
            val operator = Operations.getOperator(operation)

            val values = Operations.getValues(operator, operation)

            if (values.isNotEmpty()) {
                val firstNumber = values[0]!!
                if (values.size > 1) {
                    val secondNumber = values[1]!!
                    if (firstNumber.contains(POINT) && !secondNumber.contains(POINT)) {
                        appendToOperation(pointStr)
                    }
                } else {
                    if (firstNumber.contains(POINT)) {
                        appendToOperation(pointStr)
                    }
                }
            }
        }
    }

    private fun addOperator(operator: String, operation: String) {
        val lastElement =
            if (operation.isEmpty()) ""
            else operation.substring(operation.length-1)

        if ((lastElement != POINT) &&
            (operator == OPERATOR_SUB || operation.replace("-","").isNotEmpty()))
            appendToOperation(operator)
    }

    private fun checkOrResolve(operation: String, isFromResolve: Boolean) {
        Operations.tryResolve(operation, isFromResolve, object: OnResolveListener{
            override fun onShowResult(result: Double) {
                with(binding) {
                    //solve the operation
                    tvResult.text = String.format(Locale.ROOT, "%.4f", result)

                    //copy result to operation
                    if (tvResult.text.isNotEmpty() && !isFromResolve) {
                        tvOperation.text = tvResult.text
                    }
                }
            }

            override fun onShowMessage(errorRes: Int) {
                showMessageExpIncorrect(errorRes)
            }
        })
    }

    private fun showMessageExpIncorrect(errorRes: Int) {
        Snackbar.make(binding.root, errorRes, Snackbar.LENGTH_SHORT)
            .setAnchorView(binding.llTop).show()
    }

    private fun appendToOperation(value: String) {
        binding.tvOperation.append(value)
    }
}