package com.example.crudtestff

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.example.crudtestff.databinding.ActivityMainBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.io.ByteArrayOutputStream
import java.lang.Exception

class MainActivity : AppCompatActivity() {
    var sImage:String? =""
    var nodeId = ""
    private lateinit var db: DatabaseReference
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    fun insertdata(view: View) {
        val itemName = binding.etName.text.toString()
        val itemRate = binding.etRt.text.toString()
        val itemUnit = binding.etUn.text.toString()
        val itemDel = binding.etDel.text.toString()
        db = FirebaseDatabase.getInstance().getReference("items")
        val item = itemDs(itemName,itemRate,itemUnit,sImage,itemDel)
        val databaseReference = FirebaseDatabase.getInstance().reference
        val id = databaseReference.push().key
        db.child(id.toString()).setValue(item).addOnSuccessListener {
            binding.etName.text.clear()
            binding.etRt.text.clear()
            sImage = ""
            Toast.makeText(this,"data inserted",Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(this,"data not inserted",Toast.LENGTH_SHORT).show()
        }


    }

    fun insertImg(view: View) {
        val myfileintent = Intent(Intent.ACTION_GET_CONTENT)
        myfileintent.setType("image/*")
        ActivityResultLauncher.launch(myfileintent)


    }
    private val ActivityResultLauncher = registerForActivityResult<Intent,ActivityResult>(
        ActivityResultContracts.StartActivityForResult()
    ){result:ActivityResult ->
            if(result.resultCode== RESULT_OK){
                val uri = result.data!!.data
                try {
                    val inputStream = contentResolver.openInputStream(uri!!)
                    val myBitmap = BitmapFactory.decodeStream(inputStream)
                    val stream =ByteArrayOutputStream()
                    myBitmap.compress(Bitmap.CompressFormat.PNG,100,stream)
                    val bytes = stream.toByteArray()
                    sImage = Base64.encodeToString(bytes, Base64.DEFAULT)
                    binding.imageView.setImageBitmap(myBitmap)
                    inputStream!!.close()
                    Toast.makeText(this,"Image Selected",Toast.LENGTH_SHORT).show()
                }catch (ex: Exception){
                    Toast.makeText(this,ex.message.toString(), Toast.LENGTH_LONG).show()
                }
            }
    }

    private val itemResultLauncher = registerForActivityResult<Intent,ActivityResult>(
        ActivityResultContracts.StartActivityForResult()
    ){result:ActivityResult ->
        if(result.resultCode== 2){
            val intent = result.data
            if(intent != null) {
                nodeId = intent.getStringExtra("itm_id").toString()
            }
            db =FirebaseDatabase.getInstance().getReference("items")
            db.child(nodeId).get().addOnSuccessListener {
                if(it.exists()){
                    binding.etName.setText(it.child("itemName").value.toString())
                    binding.etName.setText(it.child("itemRate").value.toString())
                    binding.etName.setText(it.child("itemUnit").value.toString())

                    sImage = it.child("itemImg").value.toString()
                    binding.etName.setText(it.child("itemDel").value.toString())
                    val bytes = Base64.decode(sImage,Base64.DEFAULT)
                    val bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.size)
                    binding.imageView.setImageBitmap(bitmap)
                    binding.bynUpdate.visibility = View.VISIBLE
                    binding.btnDel.visibility = View.VISIBLE




                }else{
                    Toast.makeText(this,"item not found",Toast.LENGTH_LONG).show()
                }
            }.addOnFailureListener{
                Toast.makeText(this,it.toString(),Toast.LENGTH_LONG).show()
            }
        }
    }

    fun showList(view: View) {
        var i:Intent
        i = Intent(this,ItemList::class.java)
        itemResultLauncher.launch(i)

    }

    fun update_data(view: View) {
        val itemName = binding.etName.text.toString()
        val itemRate = binding.etRt.text.toString()
        val itemUnit = binding.etUn.text.toString()
        val itemDel = binding.etDel.text.toString()
        db = FirebaseDatabase.getInstance().getReference("items")
        val item = itemDs(itemName,itemRate,itemUnit,itemDel,sImage)



        db.child(nodeId).setValue(item).addOnSuccessListener {
            binding.etName.text.clear()
            binding.etRt.text.clear()
            sImage = ""
            binding.bynUpdate.visibility = View.INVISIBLE
            binding.btnDel.visibility = View.INVISIBLE
            Toast.makeText(this,"data inserted",Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(this,"data not inserted",Toast.LENGTH_SHORT).show()
        }


    }
    fun delete_data(view: View) {
        db = FirebaseDatabase.getInstance().getReference("items")
        db.child(nodeId).removeValue()


    }
}