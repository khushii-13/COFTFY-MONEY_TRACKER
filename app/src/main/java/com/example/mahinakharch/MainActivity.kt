package com.example.mahinakharch

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button

import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.mahinakharch.databinding.ActivityMainBinding
//import kotlinx.android.synthetic.main.content_main.*

import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var deletedTransaction: Transaction
    private lateinit var transactions:List<Transaction>
    private lateinit var oldTransactions : List<Transaction>
    private lateinit var transactionAdapter: TransactionAdapter
    private lateinit var linearlayoutManager: LinearLayoutManager
    private lateinit var db:AppDatabase

    @SuppressLint("MissingInflatedId", "SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        transactions= arrayListOf()
        transactionAdapter= TransactionAdapter(transactions)
        linearlayoutManager= LinearLayoutManager(this)
        db= Room.databaseBuilder(this,AppDatabase::class .java,"transactions").build()
        val rm=findViewById<RecyclerView>(R.id.recylerview)
        rm.apply {
            adapter=transactionAdapter
            layoutManager=linearlayoutManager
        }
        // swipe to remove
        val itemTouchHelper = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                deleteTransaction(transactions[viewHolder.adapterPosition])
            }

        }
        val recyclerview=findViewById<RecyclerView>(R.id.recylerview)
        val swipeHelper = ItemTouchHelper(itemTouchHelper)
        swipeHelper.attachToRecyclerView(recyclerview)
        val btn:FloatingActionButton=findViewById(R.id.addbtn)

        btn.setOnClickListener {
            //  Toast.makeText(applicationContext, "This a toast message", Toast.LENGTH_LONG).show()
            val i = Intent(this, Addtransactionactivity2::class.java)
            startActivity(i)
        }



    }
    private fun fetchAll(){
        GlobalScope.launch {
            transactions = db.transactionDao().getAll()

            runOnUiThread {
                updateDashboard()
                transactionAdapter.setData(transactions)
            }
        }
    }
    private fun updateDashboard(){
        val totalAmount = transactions.map { it.amount }.sum()
        val budgetAmount = transactions.filter { it.amount>0 }.map{it.amount}.sum()
        val expenseAmount = totalAmount - budgetAmount

       findViewById<TextView>( R.id.balance).text = "₹ %.2f".format(totalAmount)
        findViewById<TextView>( R.id.budget).text  = "₹ %.2f".format(budgetAmount)
        findViewById<TextView>( R.id.expense).text = "₹ %.2f".format(expenseAmount)
    }
    private fun undoDelete(){
        GlobalScope.launch {
            db.transactionDao().insertAll(deletedTransaction)

            transactions = oldTransactions

            runOnUiThread {
                transactionAdapter.setData(transactions)
                updateDashboard()
            }
        }
    }
    private fun showSnackbar(){
        val view = findViewById<View>(R.id.coordinator)
        val snackbar = Snackbar.make(view, "Transaction deleted!",Snackbar.LENGTH_LONG)
        snackbar.setAction("Undo"){
            undoDelete()
        }
            .setActionTextColor(ContextCompat.getColor(this, R.color.red))
            .setTextColor(ContextCompat.getColor(this, R.color.white))
            .show()
    }
    private fun deleteTransaction(transaction: Transaction){
        deletedTransaction = transaction
        oldTransactions = transactions

        GlobalScope.launch {
            db.transactionDao().delete(transaction)

            transactions = transactions.filter { it.id != transaction.id }
            runOnUiThread {
                updateDashboard()
                transactionAdapter.setData(transactions)
                showSnackbar()
            }
        }
    }
    override fun onResume() {
        super.onResume()
        fetchAll()
    }

}