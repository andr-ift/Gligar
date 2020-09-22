package com.opensooq.supernova.gligarexample

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.opensooq.supernova.gligar.GligarPicker
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

//    val PICKER_REQUEST_CODE = 30

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        GligarPicker().limit(4).withActivity(this).showForResult(startForResult)
    }

    private val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_CANCELED) {
            Log.d("JS_d", "fuc")
        } else {
            val imagesList = result.data?.extras?.getStringArray(GligarPicker.IMAGES_RESULT)
            if (!imagesList.isNullOrEmpty()) {
                imagesCount.text = "Number of selected Images: ${imagesList.size}"
            }
        }
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (resultCode != Activity.RESULT_OK) {
//            return
//        }
//
//        when (requestCode) {
//            PICKER_REQUEST_CODE -> {
//                val imagesList = data?.extras?.getStringArray(GligarPicker.IMAGES_RESULT)
//                if (!imagesList.isNullOrEmpty()) {
//                    imagesCount.text = "Number of selected Images: ${imagesList.size}"
//                }
//            }
//        }
//    }
}
