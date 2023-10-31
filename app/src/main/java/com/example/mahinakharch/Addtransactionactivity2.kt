package com.example.mahinakharch

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import androidx.core.widget.addTextChangedListener
import androidx.room.Room
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class Addtransactionactivity2 : AppCompatActivity() {
    @SuppressLint("WrongViewCast", "MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_addtransactionactivity2)
        val labelLayout=findViewById<TextInputLayout>(R.id.labelLayout)

        val labelInput=findViewById<TextInputEditText>(R.id.labelInput)
        labelInput.addTextChangedListener {
            if(it!!.count() > 0)
                labelLayout.error = null
        }

        val amountInput=findViewById<TextInputEditText >(R.id.amountInput)
        val amountLayout=findViewById<TextInputLayout>(R.id.amountLayout)
        amountInput.addTextChangedListener {

            if(it!!.count() > 0)
                amountLayout.error = null
        }

        val addTransactionBtn=findViewById<Button>(R.id.addTransactionBtn)
        val descriptionInput=findViewById<TextInputEditText>(R.id.descriptionInput)
        addTransactionBtn.setOnClickListener {
            val label = labelInput.text.toString()

            val description = descriptionInput.text.toString()
            val amount = amountInput.text.toString().toDoubleOrNull()

            if(label.isEmpty())
                labelLayout.error = "Please enter a valid label"

            else if(amount == null)
                amountLayout.error = "Please enter a valid amount"
            else {
                val transaction  =Transaction(0, label, amount, description)
                insert(transaction)
            }

        }

        val closeBtn=findViewById<ImageButton>(R.id.closeBtn)
        closeBtn.setOnClickListener {
            finish()
        }
    }


    private fun insert(transaction: Transaction){
        val db = Room.databaseBuilder(this,
            AppDatabase::class.java,
            "transactions").build()

        GlobalScope.launch {
            db.transactionDao().insertAll(transaction)
            finish()
        }
    }
}