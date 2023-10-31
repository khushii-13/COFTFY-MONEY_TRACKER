package com.example.mahinakharch

import android.annotation.SuppressLint
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.sax.RootElement
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.ImageButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.addTextChangedListener
import androidx.room.Room
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class DetailedActivity : AppCompatActivity() {
    private  lateinit var transaction:Transaction
    @SuppressLint("ServiceCast")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detailed)
         transaction = intent.getSerializableExtra("transaction") as Transaction


        val labelLayout=findViewById<TextInputLayout>(R.id.labelLayout)
         val UpdateBtn=findViewById<Button>(R.id.UpdateBtn)
        val labelInput=findViewById<TextInputEditText>(R.id.labelInput)
        val descriptionInput=findViewById<TextInputEditText>(R.id.descriptionInput)

        labelInput.setText(transaction.label)

        labelInput.addTextChangedListener {
            UpdateBtn.visibility = View.VISIBLE
            if(it!!.count() > 0)
                labelLayout.error = null
        }

        val amountInput=findViewById<TextInputEditText>(R.id.amountInput)
        val amountLayout=findViewById<TextInputLayout>(R.id.amountLayout)
        amountInput.setText(transaction.amount.toString())

        val rootView =findViewById<ConstraintLayout>(R.id.rootView)
        rootView.setOnClickListener {
            this.window.decorView.clearFocus()

            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(it.windowToken, 0)
        }


        amountInput.addTextChangedListener {
            UpdateBtn.visibility = View.VISIBLE
            if(it!!.count() > 0)
                amountLayout.error = null
        }
        descriptionInput.addTextChangedListener {
            UpdateBtn.visibility = View.VISIBLE
        }

        descriptionInput.setText(transaction.description)
        UpdateBtn.setOnClickListener {
            val label = labelInput.text.toString()

            val description = descriptionInput.text.toString()
            val amount = amountInput.text.toString().toDoubleOrNull()

            if(label.isEmpty())
                labelLayout.error = "Please enter a valid label"

            else if(amount == null)
                amountLayout.error = "Please enter a valid amount"
            else {
                val transaction  =Transaction(transaction.id, label, amount, description)
                update(transaction)
            }

        }

        val closeBtn=findViewById<ImageButton>(R.id.closeBtn)
        closeBtn.setOnClickListener {
            finish()
        }
    }
    private fun update(transaction: Transaction){
        val db = Room.databaseBuilder(this,
            AppDatabase::class.java,
            "transactions").build()

        GlobalScope.launch {
            db.transactionDao().update(transaction)
            finish()
        }
    }

}