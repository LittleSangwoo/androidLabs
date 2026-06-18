package com.example.nearestnumber

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.util.TreeMap
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private lateinit var etTargetNumber: EditText
    private lateinit var btnFindNearest: Button
    private lateinit var tvResult: TextView
    private lateinit var tvError: TextView

    // TreeMap для хранения пар <Число, Его первый индекс в массиве>
    private val numbersTree = TreeMap<Int, Int>()

    // Выносим константы, избавляясь от "магических чисел"
    companion object {
        private const val ARRAY_SIZE = 1000
        private const val MIN_VALUE = 1
        private const val MAX_VALUE = 10000
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        etTargetNumber = findViewById(R.id.etTargetNumber)
        btnFindNearest = findViewById(R.id.btnFindNearest)
        tvResult = findViewById(R.id.tvResult)
        tvError = findViewById(R.id.tvError)

        generateNumbers()

        btnFindNearest.setOnClickListener {
            findNearestNumber()
        }
    }

    private fun generateNumbers() {
        for (index in 0 until ARRAY_SIZE) {
            val number = Random.nextInt(MIN_VALUE, MAX_VALUE + 1)
// Если число генерируется впервые, сохраняем его и индекс.
// Если дубликат — игнорируем, чтобы сохранить индекс первого появления.
            if (!numbersTree.containsKey(number)) {
                numbersTree[number] = index
            }
        }
    }

    private fun findNearestNumber() {
        tvError.visibility = View.GONE
        tvResult.visibility = View.GONE

        val inputStr = etTargetNumber.text.toString().trim()
        val target = inputStr.toIntOrNull()

        if (target == null) {
            showError("Пожалуйста, введите корректное целое число.")
            return
        }

        val nearest = searchClosest(target)

        if (nearest != null) {
            tvResult.text = "Ближайшее число в массиве: $nearest"
            tvResult.visibility = View.VISIBLE
        } else {
            showError("Массив пуст или данные не сгенерированы.")
        }
    }

    private fun searchClosest(target: Int): Int? {
// Ищем ближайшее меньшее (или равное) и ближайшее большее (или равное)
        val floorEntry = numbersTree.floorEntry(target)
        val ceilingEntry = numbersTree.ceilingEntry(target)

        return when {
            floorEntry == null && ceilingEntry == null -> null // Дерево пустое
            floorEntry == null -> ceilingEntry?.key // Есть только числа больше target
            ceilingEntry == null -> floorEntry.key // Есть только числа меньше target
            else -> {
                val distToFloor = target - floorEntry.key
                val distToCeiling = ceilingEntry.key - target

                when {
                    distToFloor < distToCeiling -> floorEntry.key
                    distToCeiling < distToFloor -> ceilingEntry.key
                    else -> {
// Расстояния равны. Сравниваем оригинальные индексы из массива.
// Возвращаем то число, чей индекс меньше (встретилось раньше).
                        if (floorEntry.value < ceilingEntry.value) {
                            floorEntry.key
                        } else {
                            ceilingEntry.key
                        }
                    }
                }
            }
        }
    }

    private fun showError(message: String) {
        tvError.text = message
        tvError.visibility = View.VISIBLE
    }
}