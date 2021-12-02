package com.example.khawlah

import android.content.DialogInterface
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.khawlah.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import java.lang.Exception
import kotlin.random.Random

enum class OperatorType {
    ADD, SUB, MULTIPLY, DIVISION
}

class MainActivity : AppCompatActivity() {

    private var messages = arrayListOf<String>()
    private var operator = OperatorType.ADD
    private lateinit var equation: String
    private var answer = 0.0
    private var score = 0
    private var highScore = 0
    private lateinit var binding: ActivityMainBinding
    lateinit var sf: SharedPreferences
    lateinit var editor: SharedPreferences.Editor
    lateinit var myRv: RecyclerView
    lateinit var myLayout: ConstraintLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setup()

        // Show an alert dialog when the app starts to welcome the user and explain the app
        showAlert("Welcome to Math Study App!\nHow many equations can you solve?")
    }

    private fun setup() {
        setupRecyclerView()
        setupSubmitButton()
        setupSharedPreferences()

        myLayout = binding.clMain
        // fetch high Score from Shared Preferences
        highScore = sf.getInt("highScore", 0)
        binding.tvHighScore.text = "High Score: $highScore"
    }

    private fun setupRecyclerView() {
        myRv = binding.rvMain
        myRv.adapter = equationAdapter(messages)
        myRv.layoutManager = LinearLayoutManager(this)
    }

    private fun setupSharedPreferences() {
        sf = getSharedPreferences("details", MODE_PRIVATE)
        editor = sf.edit()
    }

    private fun newGame() {
        enableButton()
        score = 0
        binding.tvScore.text = "Score: $score"
        messages.clear()
        myRv.adapter!!.notifyDataSetChanged()
        newEquation()
    }

    // The app should generate random equations and ask the user to enter the solution
    private fun newEquation() {
        /*
        The problems should get progressively more difficult with each equation (use larger numbers)
        for example:
        in the equation1: random number from 0 until 10
        in the equation2: random number from 0 until 11
        in the equation3: random number from 0 until 12
        .
        .
        .
        in the equation10: random number from 0 until 19
        */
        val num1 = Random.nextInt(0, score + 10)
        var num2 = Random.nextInt(0, score + 10)

        // if the operator is DIVISION, the num2 must be non zero
        if (operator == OperatorType.DIVISION) {
            num2 = Random.nextInt(1, score + 10)
        }

        // make operator as string
        val op = when (operator) {
            OperatorType.ADD -> "+"
            OperatorType.SUB -> "-"
            OperatorType.MULTIPLY -> "*"
            OperatorType.DIVISION -> "/"
        }
        equation = "$num1 $op $num2 ="
        binding.tvProblem.text = equation

        answer = when (operator) {
            OperatorType.ADD -> (num1 + num2).toDouble()
            OperatorType.SUB -> (num1 - num2).toDouble()
            OperatorType.MULTIPLY -> (num1 * num2).toDouble()
            OperatorType.DIVISION -> num1 / num2.toDouble()
        }
    }

    private fun setupSubmitButton() {
        binding.btnSubmit.setOnClickListener {
            var solution = binding.etSolution.text.toString()
            binding.etSolution.text.clear()
                if (convertToDouble(solution)) {
                    if (solution.toDouble() == answer) {
                        if (operator == OperatorType.DIVISION) {
                            messages.add("$equation $answer")
                        } else {
                            messages.add("$equation ${answer.toInt()}")
                        }

                        newEquation() // generates another problem
                        score++ // adds one point to their score.
                        binding.tvScore.text = "Score: $score"
                    } else {
                        if (operator == OperatorType.DIVISION) {
                            messages.add("The correct answer was $answer")
                        } else {
                            messages.add("The correct answer was ${answer.toInt()}")
                        }

                        if (isHighScore()) {
                            messages.add("New High Score!")
                            binding.tvHighScore.text = "High Score: $highScore"
                        }
                        showAlert("play again?") // Allow users to start a new round with an alert window
                    }

                    myRv.adapter!!.notifyDataSetChanged()
                    myRv.scrollToPosition(messages.size - 1)
                }

        }

    }

    private fun convertToDouble(input: String): Boolean {
        return try {
            input.toDouble()
            true
        } catch (e: Exception) {
            Snackbar.make(myLayout, "Please enter number", Snackbar.LENGTH_LONG).show()
            false
        }
    }

    private fun isHighScore(): Boolean {
        // The high score should be preserved using shared preferences
        if (highScore < score) {
            editor.putInt("highScore", score)
            editor.commit()
            highScore = score
            return true
        }
        return false
    }

    private fun showAlert(title: String) {
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setMessage(title)
        if (title.contains("Welcome")) {
            dialogBuilder.setPositiveButton("Start", DialogInterface.OnClickListener { dialog, id ->
                newGame()
            })
        } else {
            dialogBuilder.setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, id ->
                newGame()
            })
            dialogBuilder.setNegativeButton("No", DialogInterface.OnClickListener { dialog, id ->
                disableButton()
                dialog.cancel()
            })
        }

        val alert = dialogBuilder.create()
        alert.setTitle("Math Study App")
        alert.show()
    }

    private fun enableButton() {
        binding.btnSubmit.isEnabled = true
        binding.btnSubmit.isClickable = true
    }

    private fun disableButton() {
        binding.btnSubmit.isEnabled = false
        binding.btnSubmit.isClickable = false
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.add -> {
                operator = OperatorType.ADD
                newGame()
                return true
            }
            R.id.sub -> {
                operator = OperatorType.SUB
                newGame()
                return true
            }
            R.id.multiply -> {
                operator = OperatorType.MULTIPLY
                newGame()
                return true
            }
            R.id.div -> {
                operator = OperatorType.DIVISION
                newGame()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}