package com.davis.kevin.dijkelapp.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import com.davis.kevin.dijkelapp.DOM.Dijkel
import com.davis.kevin.dijkelapp.DOM.Schacht
import com.davis.kevin.dijkelapp.R


class DijkelLijstAdapter( private val context: Context,
private val dataSource: MutableList<Schacht>, private val dijkelLijst: MutableList<Dijkel>
) : BaseAdapter() {

    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val rowView = inflater.inflate(R.layout.customdijkellijst, parent, false)

        val Schachttextview = rowView.findViewById(R.id.txtSchacht) as TextView
        val Dijkeltextview = rowView.findViewById(R.id.txtDijkels) as TextView
        Schachttextview.text = dataSource[position].voornaam + " " + dataSource[position].achternaam


        var aantalDijkels : Int = 0
        for(item in dijkelLijst){
            if(dataSource[position].id == item.schachtid){
                if(!item.done){
                    aantalDijkels++
                }
            }
        }


        Dijkeltextview.text = aantalDijkels.toString()

        return rowView
    }

    override fun getItem(position: Int): Any {
        return dataSource[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return dataSource.size
    }
}