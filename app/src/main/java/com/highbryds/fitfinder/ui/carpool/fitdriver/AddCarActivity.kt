package com.highbryds.fitfinder.ui.carpool.fitdriver

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CompoundButton
import androidx.lifecycle.Observer
import com.google.gson.Gson
import com.highbryds.fitfinder.R
import com.highbryds.fitfinder.commonHelper.Constants
import com.highbryds.fitfinder.commonHelper.KotlinHelper
import com.highbryds.fitfinder.commonHelper.PrefsHelper
import com.highbryds.fitfinder.commonHelper.toast
import com.highbryds.fitfinder.model.carpool.CarData
import com.highbryds.fitfinder.model.carpool.CarDetails
import com.highbryds.fitfinder.model.carpool.FD_CarPool
import com.highbryds.fitfinder.ui.BaseActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_add_car.*
import kotlinx.android.synthetic.main.toolbar.*
import java.util.regex.Matcher
import java.util.regex.Pattern
import javax.inject.Inject


@AndroidEntryPoint
class AddCarActivity : BaseActivity(), View.OnClickListener, KotlinHelper.Companion.GetData {

    @Inject
    lateinit var carViewModel: AddCarViewModel
    val arrCarMake = mutableListOf<CarData>()
    val arrCarModels = mutableListOf<CarDetails>()
    lateinit var fdCarpool: FD_CarPool;
    var AC: String = "AC";
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_car)
        setSupportActionBar(toolbar)
        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true);
        getSupportActionBar()?.setDisplayShowHomeEnabled(true);
        supportActionBar?.title = "Add Car Details"
        toolbar.setNavigationOnClickListener { finish() }
        var adapterCarMake: ArrayAdapter<CarData> =
            ArrayAdapter<CarData>(this, android.R.layout.simple_list_item_1, arrCarMake)
        var adapterCarModel: ArrayAdapter<CarDetails> =
            ArrayAdapter<CarDetails>(this, android.R.layout.simple_list_item_1, arrCarModels)
        autoCompleteMake.setAdapter(adapterCarMake)
        autoCompleteModel.setAdapter(adapterCarModel)

        autoCompleteMake.onItemClickListener = object : AdapterView.OnItemClickListener {
            override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

                val carData: CarData = p0?.getItemAtPosition(p2) as CarData

                autoCompleteModel.setText("")
                adapterCarModel.clear()
                //  arrCarModels.addAll(carData?.carData?.car_details)
                adapterCarModel.addAll(carData?.carData?.car_details)

            }

        }
        autoCompleteModel.onItemClickListener = object : AdapterView.OnItemClickListener {
            override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

            }

        }

        switchAC.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener {
            override fun onCheckedChanged(p0: CompoundButton?, p1: Boolean) {

                if (p1) {
                    AC = "YES"
                } else {

                    AC = "NO"
                }
            }

        })
        //  containerColorPicker.setOnClickListener(this)
        btnSubmit.setOnClickListener(this)
        edtColor.setOnClickListener(this)


        carViewModel.getCarMakeModels()

        carViewModel.arrCarMakeModel.observe(this, Observer {

            arrCarMake.addAll(it)
            adapterCarMake.notifyDataSetChanged()

        })


        bindData()
    }


    override fun onClick(p0: View?) {

        when (p0?.id) {
            R.id.edtColor -> {

                KotlinHelper.getCarColors(this, this)


                /*     val col = resources.getStringArray(R.array.colors)

                     var categoryList : ArrayList<String>?=null
                     categoryList = col.toCollection(ArrayList())



     //                val colors: ArrayList<String> =
     //                    resources.getStringArray(R.array.colors) as ArrayList<String>

                     val colorPicker = ColorPicker(this@AddCarActivity)

                     colorPicker.setOnFastChooseColorListener(object : OnFastChooseColorListener {
                         override fun setOnFastChooseColorListener(position: Int, color: Int) {
                             // put code
                             txtPickColor.setBackgroundColor(color)
                             pickedColor = position.toString()
                         }

                         override fun onCancel() {
                             // put code
                         }
                     })
                         //.setColors(categoryList)
                         .setRoundColorButton(true)
                         .setColumns(5)
                         .show()*/
            }
            R.id.btnSubmit -> {

                if (autoCompleteMake.text.trim().isEmpty()) {
                    toast(applicationContext, "Please enter make")
                    return
                }
                if (autoCompleteModel.text.trim().isEmpty()) {
                    toast(applicationContext, "Please enter model")
                    return
                }
                if (edtRegNo.text.trim().isEmpty()) {
                    toast(applicationContext, "Please enter registration number")
                    return
                }
                if (!checkValidPlate()) {
                    toast(applicationContext, "Please enter valid registration number")
                    return
                }

                if (edtColor.text.isEmpty()) {
                    toast(applicationContext, "Please pick color of car")

                    return
                }


                if (edtCostPerSeat.text.trim().isEmpty()) {
                    toast(applicationContext, "Cost per seat is required")

                    return
                }


                fdCarpool = FD_CarPool(
                    autoCompleteMake.text.toString(),
                    autoCompleteModel.text.toString(),
                    "FitDrive",
                    KotlinHelper.getUsersData().cellNumber,
                    edtColor.text.toString(),
                    AC,
                    edtRegNo.text.toString(),
                    "Car",
                    number_picker.value,
                    1,
                    50,
                    Integer.parseInt(edtCostPerSeat.text.toString())
                )


                PrefsHelper.putString(Constants.Pref_CarData, Gson().toJson(fdCarpool))
                finish()


            }
        }
    }

    fun bindData() {
        val json = PrefsHelper.getString(Constants.Pref_CarData)
        val model = Gson().fromJson<FD_CarPool>(json, FD_CarPool::class.java)
        model?.let {

            with(model)
            {
                autoCompleteMake.setText(carmake)
                autoCompleteModel.setText(carmodel)
                edtRegNo.setText(regno)
                edtColor.setText(color)
                number_picker.value = totalseats
                edtCostPerSeat.setText("$preferredcost_max")

                switchAC.isChecked = if (aC.equals("YES")) true else false
            }
        }
    }

    fun checkValidPlate(): Boolean {
        var text: String = ""
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
        matcher = number.matcher(text)

        return matcher.matches()
    }

    override fun getColor(color: String) {
        edtColor.setText(color)
    }
}