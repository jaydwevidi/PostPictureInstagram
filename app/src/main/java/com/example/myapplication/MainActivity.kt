package com.example.myapplication

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aghajari.zoomhelper.ZoomHelper
import com.bumptech.glide.Glide
import com.example.uploadfeed.retrofit.BuilderInstance
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.single.PermissionListener
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    private lateinit var path : File
    val imageList = mutableListOf<ImageFileObject>()
    private lateinit var selectedPicture : String

    val TAG = "Jay MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.hide()

        path = File(System.getenv("EXTERNAL_STORAGE")!!)
        askPermissionForStorage()
        postPictureButton()

        ZoomHelper.addZoomableView(findViewById<ImageView>(R.id.selectedImage))
        zoomFunctionality()

        findViewById<TextView>(R.id.galleryTV).setOnClickListener {

            val mFile = imageList[0].file

            val filePart = MultipartBody.Part.createFormData(
                "file",
                mFile.name,
                RequestBody.create(MediaType.parse("image/*"), mFile)
            )

            val description : RequestBody = RequestBody.create(
                MediaType.parse("text/plain"),
                "This is a sample description by Jay Dwivedi pakka"
            )

            val id : RequestBody = RequestBody.create(
                MediaType.parse("text/plain"),
                    "211"
                )


            val hashtag : RequestBody = RequestBody.create(
                MediaType.parse("text/plain"),
                "[\"#frds\",\"#care\"]"
            )



            lifecycleScope.launchWhenCreated {
                val response = try {
                    BuilderInstance.builderAPI.addToFeed(
                        id ,
                        description ,
                        filePart,
                        hashtag
                    )
                }

                catch (e: Exception) {
                    Log.d(TAG, "onCreate: login Exception $e  , ${e.message}")
                    return@launchWhenCreated
                }

                if(response.isSuccessful){
                    val body = response.body()
                    Log.d(TAG, "onCreate: response successful ${response.body()}")
                }
                else{
                    Log.d(TAG, "onCreate: response unsuccessful")
                    Toast.makeText(this@MainActivity, "some Error Occurred", Toast.LENGTH_SHORT)
                        .show()

                }
            }
        }
    }

    private fun zoomFunctionality(){
        ZoomHelper.addZoomableView(findViewById<ImageView>(R.id.selectedImage))
    }
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        return ZoomHelper.getInstance().dispatchTouchEvent(ev!!,this) || super.dispatchTouchEvent(ev)
    }

    fun setupRV(){
        val rv = findViewById<RecyclerView>(R.id.gridImagesRV)
        val list = getList()
        val mAdapter = GridRVAdapter(imageList , applicationContext)


        mAdapter.msetupOnClickListner(object : GridRVAdapter.MyOnItemClickListner{
            override fun onItemClick(position: Int) {


                Glide.with(applicationContext)
                    .load(imageList[position].file)
                    .fitCenter()
                    .into(findViewById<ImageView>(R.id.selectedImage))

                selectedPicture = imageList[position].path
            }

        })


        rv.apply {
            adapter = mAdapter
            layoutManager = GridLayoutManager(context , 3)

        }
        try {
            Glide.with(applicationContext)
                .load(imageList[0].file)
                .centerCrop()
                .into(findViewById<ImageView>(R.id.selectedImage))

            selectedPicture = imageList[0].path

        }catch (e : Exception){
            Toast.makeText(this, "no Images found", Toast.LENGTH_SHORT).show()
        }


    }

    fun postPictureButton(){
        findViewById<ImageView>(R.id.postPictureButton).setOnClickListener {
            val intent = Intent(this , PostPictureActivity::class.java)
            intent.putExtra("1" , selectedPicture )
            startActivity(intent)
        }
    }


    private fun askPermissionForStorage() {

        val permissionListner = object : PermissionListener {

            override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                //Toast.makeText(this@MainActivity, "Permission Given", Toast.LENGTH_SHORT).show()
                displayFiles(path)
                Log.d("videoList", "$imageList ")
                setupRV()
            }

            override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                Toast.makeText(this@MainActivity, "permission dedo please", Toast.LENGTH_SHORT).show()
            }

            override fun onPermissionRationaleShouldBeShown(
                p0: com.karumi.dexter.listener.PermissionRequest?,
                p1: PermissionToken?
            ) {
                p1?.continuePermissionRequest()
            }
        }

        Dexter.withContext(this)
            .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
            .withListener(permissionListner)
            .check()
    }


    private fun displayFiles(file : File){

        val allFiles  = file.listFiles() ?: return
        for(i in allFiles){
            if(i.isDirectory || !i.isHidden){
                //Log.d("directory found", "= ${i.name}")
                displayFiles(i)

                if(i.name.endsWith(".jpg") || i.name.endsWith(".jpeg") || i.name.endsWith(".png")) {

                    val videoObject = ImageFileObject(i.name , i.path.toString() ,  i )
                    imageList.add(videoObject)
                    Log.d("myPath", "=  ${i.path} ")
                    //Log.d("videofound = ", " ${i.name}")
                }
            }

        }
    }






    private fun getList() : MutableList<String>{
        val list = mutableListOf<String>()
        list.add("https://static.wikia.nocookie.net/disney/images/1/13/Gal_Gadot.jpg/revision/latest?cb=20180811005357")
        list.add("https://m.media-amazon.com/images/M/MV5BZWVhYzE0NzgtM2U1Yi00OWM1LWJlZTUtZmNkNWZhM2VkMDczXkEyXkFqcGdeQW1yb3NzZXI@._V1_.jpg")
        list.add("https://i.guim.co.uk/img/media/1af905f750e1dc85eb490a3ec20bf76fb3ac51f7/0_486_2518_1509/master/2518.jpg?width=465&quality=45&auto=format&fit=max&dpr=2&s=1257fe1010592f3e91cf17a80471eefa")
        list.add("https://observer.com/wp-content/uploads/sites/2/2021/05/1_ynJEWSa6ivgFpF0EUP1L_A.jpeg?quality=80&strip")
        list.add("https://static.wikia.nocookie.net/disney/images/1/13/Gal_Gadot.jpg/revision/latest?cb=20180811005357")
        list.add("https://m.media-amazon.com/images/M/MV5BZWVhYzE0NzgtM2U1Yi00OWM1LWJlZTUtZmNkNWZhM2VkMDczXkEyXkFqcGdeQW1yb3NzZXI@._V1_.jpg")
        list.add("https://i.guim.co.uk/img/media/1af905f750e1dc85eb490a3ec20bf76fb3ac51f7/0_486_2518_1509/master/2518.jpg?width=465&quality=45&auto=format&fit=max&dpr=2&s=1257fe1010592f3e91cf17a80471eefa")
        list.add("https://observer.com/wp-content/uploads/sites/2/2021/05/1_ynJEWSa6ivgFpF0EUP1L_A.jpeg?quality=80&strip")
        list.add("https://static.wikia.nocookie.net/disney/images/1/13/Gal_Gadot.jpg/revision/latest?cb=20180811005357")
        list.add("https://m.media-amazon.com/images/M/MV5BZWVhYzE0NzgtM2U1Yi00OWM1LWJlZTUtZmNkNWZhM2VkMDczXkEyXkFqcGdeQW1yb3NzZXI@._V1_.jpg")
        list.add("https://i.guim.co.uk/img/media/1af905f750e1dc85eb490a3ec20bf76fb3ac51f7/0_486_2518_1509/master/2518.jpg?width=465&quality=45&auto=format&fit=max&dpr=2&s=1257fe1010592f3e91cf17a80471eefa")
        list.add("https://observer.com/wp-content/uploads/sites/2/2021/05/1_ynJEWSa6ivgFpF0EUP1L_A.jpeg?quality=80&strip")
        list.add("https://static.wikia.nocookie.net/disney/images/1/13/Gal_Gadot.jpg/revision/latest?cb=20180811005357")
        list.add("https://m.media-amazon.com/images/M/MV5BZWVhYzE0NzgtM2U1Yi00OWM1LWJlZTUtZmNkNWZhM2VkMDczXkEyXkFqcGdeQW1yb3NzZXI@._V1_.jpg")
        list.add("https://i.guim.co.uk/img/media/1af905f750e1dc85eb490a3ec20bf76fb3ac51f7/0_486_2518_1509/master/2518.jpg?width=465&quality=45&auto=format&fit=max&dpr=2&s=1257fe1010592f3e91cf17a80471eefa")
        list.add("https://observer.com/wp-content/uploads/sites/2/2021/05/1_ynJEWSa6ivgFpF0EUP1L_A.jpeg?quality=80&strip")
        list.add("https://static.wikia.nocookie.net/disney/images/1/13/Gal_Gadot.jpg/revision/latest?cb=20180811005357")
        list.add("https://m.media-amazon.com/images/M/MV5BZWVhYzE0NzgtM2U1Yi00OWM1LWJlZTUtZmNkNWZhM2VkMDczXkEyXkFqcGdeQW1yb3NzZXI@._V1_.jpg")
        list.add("https://i.guim.co.uk/img/media/1af905f750e1dc85eb490a3ec20bf76fb3ac51f7/0_486_2518_1509/master/2518.jpg?width=465&quality=45&auto=format&fit=max&dpr=2&s=1257fe1010592f3e91cf17a80471eefa")
        list.add("https://observer.com/wp-content/uploads/sites/2/2021/05/1_ynJEWSa6ivgFpF0EUP1L_A.jpeg?quality=80&strip")
        list.add("https://static.wikia.nocookie.net/disney/images/1/13/Gal_Gadot.jpg/revision/latest?cb=20180811005357")
        list.add("https://m.media-amazon.com/images/M/MV5BZWVhYzE0NzgtM2U1Yi00OWM1LWJlZTUtZmNkNWZhM2VkMDczXkEyXkFqcGdeQW1yb3NzZXI@._V1_.jpg")
        list.add("https://i.guim.co.uk/img/media/1af905f750e1dc85eb490a3ec20bf76fb3ac51f7/0_486_2518_1509/master/2518.jpg?width=465&quality=45&auto=format&fit=max&dpr=2&s=1257fe1010592f3e91cf17a80471eefa")
        list.add("https://observer.com/wp-content/uploads/sites/2/2021/05/1_ynJEWSa6ivgFpF0EUP1L_A.jpeg?quality=80&strip")
        list.add("https://static.wikia.nocookie.net/disney/images/1/13/Gal_Gadot.jpg/revision/latest?cb=20180811005357")
        list.add("https://m.media-amazon.com/images/M/MV5BZWVhYzE0NzgtM2U1Yi00OWM1LWJlZTUtZmNkNWZhM2VkMDczXkEyXkFqcGdeQW1yb3NzZXI@._V1_.jpg")
        list.add("https://i.guim.co.uk/img/media/1af905f750e1dc85eb490a3ec20bf76fb3ac51f7/0_486_2518_1509/master/2518.jpg?width=465&quality=45&auto=format&fit=max&dpr=2&s=1257fe1010592f3e91cf17a80471eefa")
        list.add("https://observer.com/wp-content/uploads/sites/2/2021/05/1_ynJEWSa6ivgFpF0EUP1L_A.jpeg?quality=80&strip")
        list.add("https://static.wikia.nocookie.net/disney/images/1/13/Gal_Gadot.jpg/revision/latest?cb=20180811005357")
        list.add("https://m.media-amazon.com/images/M/MV5BZWVhYzE0NzgtM2U1Yi00OWM1LWJlZTUtZmNkNWZhM2VkMDczXkEyXkFqcGdeQW1yb3NzZXI@._V1_.jpg")
        list.add("https://i.guim.co.uk/img/media/1af905f750e1dc85eb490a3ec20bf76fb3ac51f7/0_486_2518_1509/master/2518.jpg?width=465&quality=45&auto=format&fit=max&dpr=2&s=1257fe1010592f3e91cf17a80471eefa")
        list.add("https://observer.com/wp-content/uploads/sites/2/2021/05/1_ynJEWSa6ivgFpF0EUP1L_A.jpeg?quality=80&strip")
        list.add("https://static.wikia.nocookie.net/disney/images/1/13/Gal_Gadot.jpg/revision/latest?cb=20180811005357")
        list.add("https://m.media-amazon.com/images/M/MV5BZWVhYzE0NzgtM2U1Yi00OWM1LWJlZTUtZmNkNWZhM2VkMDczXkEyXkFqcGdeQW1yb3NzZXI@._V1_.jpg")
        list.add("https://i.guim.co.uk/img/media/1af905f750e1dc85eb490a3ec20bf76fb3ac51f7/0_486_2518_1509/master/2518.jpg?width=465&quality=45&auto=format&fit=max&dpr=2&s=1257fe1010592f3e91cf17a80471eefa")
        list.add("https://observer.com/wp-content/uploads/sites/2/2021/05/1_ynJEWSa6ivgFpF0EUP1L_A.jpeg?quality=80&strip")
        list.add("https://static.wikia.nocookie.net/disney/images/1/13/Gal_Gadot.jpg/revision/latest?cb=20180811005357")
        list.add("https://m.media-amazon.com/images/M/MV5BZWVhYzE0NzgtM2U1Yi00OWM1LWJlZTUtZmNkNWZhM2VkMDczXkEyXkFqcGdeQW1yb3NzZXI@._V1_.jpg")
        list.add("https://i.guim.co.uk/img/media/1af905f750e1dc85eb490a3ec20bf76fb3ac51f7/0_486_2518_1509/master/2518.jpg?width=465&quality=45&auto=format&fit=max&dpr=2&s=1257fe1010592f3e91cf17a80471eefa")
        list.add("https://observer.com/wp-content/uploads/sites/2/2021/05/1_ynJEWSa6ivgFpF0EUP1L_A.jpeg?quality=80&strip")
        list.add("https://static.wikia.nocookie.net/disney/images/1/13/Gal_Gadot.jpg/revision/latest?cb=20180811005357")
        list.add("https://m.media-amazon.com/images/M/MV5BZWVhYzE0NzgtM2U1Yi00OWM1LWJlZTUtZmNkNWZhM2VkMDczXkEyXkFqcGdeQW1yb3NzZXI@._V1_.jpg")
        list.add("https://i.guim.co.uk/img/media/1af905f750e1dc85eb490a3ec20bf76fb3ac51f7/0_486_2518_1509/master/2518.jpg?width=465&quality=45&auto=format&fit=max&dpr=2&s=1257fe1010592f3e91cf17a80471eefa")
        list.add("https://observer.com/wp-content/uploads/sites/2/2021/05/1_ynJEWSa6ivgFpF0EUP1L_A.jpeg?quality=80&strip")

        return list
    }

}