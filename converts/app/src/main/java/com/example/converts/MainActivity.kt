package com.example.converts

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var etAmount: EditText
    private lateinit var spinnerSource: Spinner
    private lateinit var spinnerTarget: Spinner
    private lateinit var etUsdRate: EditText
    private lateinit var etEurRate: EditText
    private lateinit var btnConvert: Button
    private lateinit var tvError: TextView
    private lateinit var tvResult: TextView

    // Массив доступных валют
    private val currencies = arrayOf("RUB", "USD", "EUR")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

// Инициализация UI
        etAmount = findViewById(R.id.etAmount)
        spinnerSource = findViewById(R.id.spinnerSource)
        spinnerTarget = findViewById(R.id.spinnerTarget)
        etUsdRate = findViewById(R.id.etUsdRate)
        etEurRate = findViewById(R.id.etEurRate)
        btnConvert = findViewById(R.id.btnConvert)
        tvError = findViewById(R.id.tvError)
        tvResult = findViewById(R.id.tvResult)

// Настройка выпадающих списков (Spinner)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, currencies)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerSource.adapter = adapter
        spinnerTarget.adapter = adapter

// Установим разные значения по умолчанию для наглядности (Из USD в RUB)
        spinnerSource.setSelection(1) // USD
        spinnerTarget.setSelection(0) // RUB

// Подписка на кнопку
        btnConvert.setOnClickListener { convertCurrency() }
    }

    private fun convertCurrency() {
        tvError.visibility = View.GONE
        tvResult.visibility = View.GONE

// Валидация суммы
        val amountStr = etAmount.text.toString().trim()
        val amount = amountStr.toDoubleOrNull()

        if (amount == null || amount < 0) {
            showError("Введите корректную положительную сумму.")
            return
        }

// Валидация курсов
        val usdRateStr = etUsdRate.text.toString().trim()
        val eurRateStr = etEurRate.text.toString().trim()

        val usdRate = usdRateStr.toDoubleOrNull()
        val eurRate = eurRateStr.toDoubleOrNull()

        if (usdRate == null || eurRate == null || usdRate <= 0 || eurRate <= 0) {
            showError("Курсы валют должны быть положительными числами.")
            return
        }

        val sourceCurrency = spinnerSource.selectedItem.toString()
        val targetCurrency = spinnerTarget.selectedItem.toString()

// Если валюты совпадают, возвращаем исходную сумму
        if (sourceCurrency == targetCurrency) {
            displayResult(amount, targetCurrency)
            return
        }

// 1. Конвертация в базовую валюту (Рубли)
        val amountInRubles = when (sourceCurrency) {
            "USD" -> amount * usdRate
            "EUR" -> amount * eurRate
            "RUB" -> amount
            else -> amount
        }

// 2. Конвертация из Рублей в целевую валюту
        val finalAmount = when (targetCurrency) {
            "USD" -> amountInRubles / usdRate
            "EUR" -> amountInRubles / eurRate
            "RUB" -> amountInRubles
            else -> amountInRubles
        }

        displayResult(finalAmount, targetCurrency)
    }

    private fun displayResult(result: Double, currency: String) {
// Вывод результата с точностью до 2 знаков
        tvResult.text = String.format(Locale.getDefault(), "Результат: %.2f %s", result, currency)
        tvResult.visibility = View.VISIBLE
    }

    private fun showError(message: String) {
        tvError.text = message
        tvError.visibility = View.VISIBLE
    }
}