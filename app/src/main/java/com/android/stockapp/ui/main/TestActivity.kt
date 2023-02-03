package com.android.stockapp.ui.main

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.icu.lang.UCharacter.GraphemeClusterBreak.V
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.recyclerview.widget.SnapHelper
import com.android.stockapp.R
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.android.synthetic.main.activity_test.*
import java.io.File
import java.lang.reflect.Method

class TestActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
        hideSystemSoftKeyboard(et1)

        rv.apply {
            adapter =object : Adapter<ViewHolder>() {
                override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
                    return object: ViewHolder(LayoutInflater.from(this@TestActivity).inflate(R.layout.simple_item,parent,false)){}
                }
                override fun onBindViewHolder(holder: ViewHolder, position: Int) {
                    holder.itemView.layoutParams.width = (rv.width-55*resources.displayMetrics.density).toInt()
                    holder.itemView.findViewById<ImageView>(R.id.iv).setImageResource(if(position%2==0) R.drawable.item1 else R.drawable.item2)
                }
                override fun getItemCount()=Int.MAX_VALUE
            }

            layoutManager = LinearLayoutManager(this@TestActivity,LinearLayoutManager.HORIZONTAL,false)
            var snapHelper: SnapHelper = MyPagerSnapHelper()
            snapHelper.attachToRecyclerView(this)

            layoutManager?.scrollToPosition(Int.MAX_VALUE/2)
        }
    }

    fun onViewClicked(v: View){
//        AppUtils.tackPick(this)
        for (i in 0..10){
            ivTest.postDelayed({
                ViewCompat.animate(ivTest).apply {
                    cancel()
                    alpha(1.0f).setDuration(500).start()
                }
            },200)
        }
        ViewCompat.animate(ivTest).alpha(1.0f).setDuration(500).start()

        val rxPermissions = RxPermissions(this)
        rxPermissions.request(Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .subscribe{
                if(it){
                    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    if (intent.resolveActivity(packageManager) != null) {
                        val contentValues = ContentValues()

                        // 拍完保存在Pictures，这里并不需要写文件权限，后续读取是否需要最好加上
                        contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, "xxx01.jpeg")
                        val file = File(Environment.getExternalStoragePublicDirectory(
                                Environment.DIRECTORY_DCIM),
                                System.currentTimeMillis()
                                    .toString() + ".jpg")
                        contentValues.put(MediaStore.Images.Media.DATA, file.absolutePath)

                        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                        var mPhotoUri = contentResolver.insert(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            contentValues
                        )
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, mPhotoUri)

                        startActivityForResult(intent, 222)
                    } else {
                        Log.e("0000", "no activity handle $intent")
                    }
                }
            }
    }

    fun hideSystemSoftKeyboard(editText: EditText?) {
        try {
            val cls = EditText::class.java
            val setShowSoftInputOnFocus: Method
            setShowSoftInputOnFocus =
                cls.getMethod("setShowSoftInputOnFocus", Boolean::class.javaPrimitiveType)
            setShowSoftInputOnFocus.isAccessible = true
            setShowSoftInputOnFocus.invoke(editText, false)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}