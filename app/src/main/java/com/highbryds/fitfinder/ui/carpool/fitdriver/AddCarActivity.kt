package com.highbryds.fitfinder.ui.carpool.fitdriver

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.Observer
import com.highbryds.fitfinder.R
import com.highbryds.fitfinder.commonHelper.toast
import com.highbryds.fitfinder.model.carpool.CarData
import com.highbryds.fitfinder.model.carpool.CarDetails
import com.highbryds.fitfinder.model.carpool.CarMakeModel
import com.highbryds.fitfinder.ui.BaseActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_add_car.*
import petrov.kristiyan.colorpicker.ColorPicker
import petrov.kristiyan.colorpicker.ColorPicker.OnFastChooseColorListener
import java.util.regex.Matcher
import java.util.regex.Pattern
import javax.inject.Inject

@AndroidEntryPoint
class AddCarActivity : BaseActivity(),View.OnClickListener {

    @Inject
    lateinit var carViewModel: AddCarViewModel
    val arrCarMake= mutableListOf<CarData>()
    val arrCarModels= mutableListOf<CarDetails>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_car)

        var adapterCarMake: ArrayAdapter<CarData> =
            ArrayAdapter<CarData>(this, android.R.layout.simple_list_item_1, arrCarMake)
        var adapterCarModel: ArrayAdapter<CarDetails> =
            ArrayAdapter<CarDetails>(this, android.R.layout.simple_list_item_1, arrCarModels)
        autoCompleteMake.setAdapter(adapterCarMake)
        autoCompleteModel.setAdapter(adapterCarModel)

        autoCompleteMake.onItemClickListener=object : AdapterView.OnItemClickListener {
            override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

                val carData:CarData= p0?.getItemAtPosition(p2) as CarData

                autoCompleteModel.setText("")
                adapterCarModel.clear()
              //  arrCarModels.addAll(carData?.carData?.car_details)
adapterCarModel.addAll(carData?.carData?.car_details)
              
            }

        }
        autoCompleteModel.onItemClickListener=object : AdapterView.OnItemClickListener {
            override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

            }

        }
        containerColorPicker.setOnClickListener(this)
        btnSubmit.setOnClickListener(this)


        carViewModel.getCarMakeModels()

        carViewModel.arrCarMakeModel.observe(this, Observer {

            arrCarMake.addAll(it)
            adapterCarMake.notifyDataSetChanged()

        })

        }


    override fun onClick(p0: View?) {

        when(p0?.id)
        {
            R.id.containerColorPicker->{
                val colorPicker = ColorPicker(this@AddCarActivity)

                colorPicker.setOnFastChooseColorListener(object : OnFastChooseColorListener {
                    override fun setOnFastChooseColorListener(position: Int, color: Int) {
                        // put code
                        txtPickColor.setBackgroundColor(color)
                    }

                    override fun onCancel() {
                        // put code
                    }
                })
                    .setRoundColorButton(true)
                    .setColumns(5)
                    .show()
            }
            R.id.btnSubmit->{

               if (checkValidPlate())
               {
                   toast(applicationContext,"Valid Reg No")
               }
                else
               {
                   toast(applicationContext,"not Valid reg no")
               }
            }
        }
    }

    fun checkValidPlate():Boolean
    {
        var text:String=""
        //validation setting

        //validation setting
        val REGEX =
            "^[a-zA-z]{1,4}\\s*[-]*[0-9]{0,2}\\s*[-]*[0-9]{3,4}$" //regular expression

        val number: Pattern //a pattern of compiled regex

        val matcher: Matcher //helps in matching the regex

        text = edtRegNo.text.toString()

        //fixing

        //fixing
      //  var m: Matcher = Pattern.compile("[-][0-9]{2}[-]|[-]|[\n]").matcher(text)
      //  text = m.replaceAll(" ")
      //  m =
       //     Pattern.compile("ICT|Islamabad|Sindh|Punjab|PK|Govt of Sindh|Ict-Islamabad|ICT-Islamabad")
        //        .matcher(text)
       // text = m.replaceAll("")

        //final touch

        //final touch
        //text = Pattern.compile("\\s[0-9]{2}\\s").matcher(text).replaceAll(" ")
       // text = text.replace("( +)", " ").trim()

        //number detection

        //number detection
        number = Pattern.compile(REGEX)
        matcher= number.matcher(text)

        return matcher.matches()
    }
}