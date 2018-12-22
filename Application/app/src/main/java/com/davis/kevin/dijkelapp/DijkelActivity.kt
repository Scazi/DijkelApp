package com.davis.kevin.dijkelapp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.davis.kevin.dijkelapp.Adapters.DijkelLijstAdapter
import com.davis.kevin.dijkelapp.Adapters.DijkeltjesAdapter
import com.davis.kevin.dijkelapp.DOM.Dijkel
import com.davis.kevin.dijkelapp.DOM.Schacht
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_dijkel.*
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.util.*

class DijkelActivity : AppCompatActivity() {

    lateinit var item: Schacht
    lateinit var database: FirebaseDatabase
    private lateinit var reference: DatabaseReference
    private lateinit var dijkelref: DatabaseReference
    private var schacht: Schacht? = null
    private var schachtenLijst: MutableList<Schacht> = mutableListOf()
    private var dijkel: Dijkel? = null
    private var tempDijkelLijst: MutableList<Dijkel> = mutableListOf()
    private var dijkelLijst: MutableList<Dijkel> = mutableListOf()
    private var id: String = ""
    lateinit var customView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dijkel)
        database = FirebaseDatabase.getInstance()
        reference = FirebaseDatabase.getInstance().getReference("schachten")
        dijkelref = FirebaseDatabase.getInstance().getReference("dijkels")

        id = intent.getStringExtra("id")
        fireBaseGet()
        val adapter = DijkeltjesAdapter(applicationContext, dijkelLijst)
        listDijkels.adapter = adapter
    }

    fun getClickedSchacht() {
        for (schacht in schachtenLijst) {
            if (schacht.id == id) {
                item = schacht
            }
        }

        txtNaam.text = item.voornaam + " " + item.achternaam
    }

    fun fireBaseGet() {
        val schachtListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                // Get Post object and use the values to update the UI
                if (dataSnapshot.exists()) {
                    schachtenLijst.clear()
                    for (h in dataSnapshot.children) {
                        schacht = h.getValue(Schacht::class.java)
                        schachtenLijst.add(schacht!!)
                    }
                    getClickedSchacht()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w("TAG", "loadPost:onCancelled", databaseError.toException())
                // ...
            }
        }
        reference.addValueEventListener(schachtListener)


        val dijkelListener = object : ValueEventListener {
            override fun onDataChange(dijkelSnapshot: DataSnapshot) {

                // Get Post object and use the values to update the UI
                if (dijkelSnapshot.exists()) {
                    dijkelLijst.clear()
                    tempDijkelLijst.clear()
                    for (h in dijkelSnapshot.children) {
                        dijkel = h.getValue(Dijkel::class.java)
                        tempDijkelLijst.add(dijkel!!)
                    }

                    for (item in tempDijkelLijst) {
                        if (item.schachtid == id) {
                            dijkelLijst.add(item)
                        }
                    }


                    val adapter = DijkeltjesAdapter(applicationContext, dijkelLijst)
                    listDijkels.adapter = adapter
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w("TAG", "loadPost:onCancelled", databaseError.toException())
                // ...
            }
        }
        dijkelref.addValueEventListener(dijkelListener)


    }

    fun addDijkel(view: View) {
        val dialog = MaterialDialog(this).show {
            customView(R.layout.customdijkeldialog)
        }

        customView = dialog.getCustomView() ?: return


        val aantalDijkels: EditText = customView.findViewById(R.id.txtAantalDijkels)
        val redenDijkel: EditText = customView.findViewById(R.id.txtReden)
        val buttonConfirm: Button = customView.findViewById(R.id.btnConfirm)
        val buttonCancel: Button = customView.findViewById(R.id.btnCancel)

        buttonConfirm.setOnClickListener {
            onClickConfirm(aantalDijkels, redenDijkel)
            dialog.dismiss()
        }
        buttonCancel.setOnClickListener {
            dialog.dismiss()
        }
    }

    fun onClickConfirm(aantalDijkels: EditText, redenDijkel: EditText) {
        var aantal: Int = aantalDijkels.text.toString().trim().toInt()
        var reden: String = redenDijkel.text.toString().trim()

        if (checkFields(aantal, reden)) {

            val myRef = database.getReference("dijkels")

            for(i in 1 .. aantal){
                val dijkelId = myRef.push().key.toString()
                val sdf = SimpleDateFormat("dd/M/yyyy")
                val currentDate = sdf.format(Date())
                val dijkel = Dijkel(dijkelId, item.id, reden, "TEMPORARY USER", false, currentDate)//TODO ACTIVE USER
                myRef.child(dijkelId).setValue(dijkel).addOnCompleteListener {}
            }

        } else {
            Toast.makeText(this, "Vul alle velden in...", Toast.LENGTH_SHORT).show()
        }
    }

    fun checkFields(aantal: Int, reden: String): Boolean {
        //return !(voornaam.isNullOrEmpty() || achternaam.isNullOrEmpty() || voornaam.isNullOrBlank() || achternaam.isNullOrBlank())
        return aantal > 0 && reden != ""
    }


}