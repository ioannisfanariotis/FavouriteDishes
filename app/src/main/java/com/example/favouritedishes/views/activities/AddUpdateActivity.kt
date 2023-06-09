package com.example.favouritedishes.views.activities

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.provider.MediaStore
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.favouritedishes.R
import com.example.favouritedishes.App
import com.example.favouritedishes.databinding.ActivityAddUpdateBinding
import com.example.favouritedishes.databinding.DialogImageSelectionBinding
import com.example.favouritedishes.databinding.DialogListsBinding
import com.example.favouritedishes.models.models.Dish
import com.example.favouritedishes.utils.Constants
import com.example.favouritedishes.views.adapters.ListsAdapter
import com.example.favouritedishes.viewmodels.DishViewModel
import com.example.favouritedishes.viewmodels.DishViewModelFactory
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.UUID

class AddUpdateActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityAddUpdateBinding
    private var imagePath: String = ""
    private lateinit var dialog: Dialog
    private var details: Dish? = null
    private lateinit var listAdapter: ListsAdapter

    private val viewModel: DishViewModel by viewModels {
        DishViewModelFactory((application as App).repository)
    }

    companion object{
        private const val CAMERA = 1
        private const val GALLERY = 2
        private const val IMAGE_DIRECTORY = "DishImages"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddUpdateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (intent.hasExtra(Constants.EXTRA_DETAILS))
            details = intent.getParcelableExtra(Constants.EXTRA_DETAILS)

        setSupportActionBar(binding.toolbarAddDish)
        if (details != null && details!!.id != 0){
            supportActionBar?.let {
                it.title = resources.getString(R.string.edit_dish)
            }
        }else{
            supportActionBar?.let {
                it.title = resources.getString(R.string.add_dish)
            }
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbarAddDish.setNavigationOnClickListener {
            onBackPressed()
        }

        details?.let {
            if (it.id != 0){
                imagePath = it.image
                Glide.with(this@AddUpdateActivity)
                    .load(imagePath)
                    .centerCrop()
                    .into(binding.ivDish)
                binding.etTitle.setText(it.title)
                binding.etType.setText(it.type)
                binding.etCategory.setText(it.category)
                binding.etIngredients.setText(it.ingredients)
                binding.etCookingTime.setText(it.cookingTime)
                binding.etDirection.setText(it.direction)
                binding.btnAdd.text = resources.getString(R.string.update_dish)
            }
        }

        binding.ivAddDish.setOnClickListener(this)
        binding.etType.setOnClickListener(this)
        binding.etCategory.setOnClickListener(this)
        binding.etCookingTime.setOnClickListener(this)
        binding.btnAdd.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.iv_add_dish ->{
                showSelectionDialog()
                return
            }
            R.id.et_type ->{
                listDialog(resources.getString(R.string.select_dish_type), Constants.dishTypes(), Constants.TYPE)
                return
            }
            R.id.et_category ->{
                listDialog(resources.getString(R.string.select_dish_category), Constants.dishCategories(), Constants.CATEGORY)
                return
            }
            R.id.et_cooking_time ->{
                listDialog(resources.getString(R.string.select_cooking_time), Constants.dishCookingTime(), Constants.TIME)
                return
            }
            R.id.btn_add -> {
                val title = binding.etTitle.text.toString().trim{it <= ' '}
                val type = binding.etType.text.toString().trim{it <= ' '}
                val category = binding.etCategory.text.toString().trim{it <= ' '}
                val ingredients = binding.etIngredients.text.toString().trim{it <= ' '}
                val time = binding.etCookingTime.text.toString().trim{it <= ' '}
                val direction = binding.etDirection.text.toString().trim{it <= ' '}

                when{
                    TextUtils.isEmpty(imagePath) -> {
                        Toast.makeText(this@AddUpdateActivity, "Insert an Image", Toast.LENGTH_SHORT).show()
                    }
                    TextUtils.isEmpty(title) -> {
                        Toast.makeText(this@AddUpdateActivity, "Insert a Title", Toast.LENGTH_SHORT).show()
                    }
                    TextUtils.isEmpty(type) -> {
                        Toast.makeText(this@AddUpdateActivity, "Insert a Type", Toast.LENGTH_SHORT).show()
                    }
                    TextUtils.isEmpty(category) -> {
                        Toast.makeText(this@AddUpdateActivity, "Insert a Category", Toast.LENGTH_SHORT).show()
                    }
                    TextUtils.isEmpty(ingredients) -> {
                        Toast.makeText(this@AddUpdateActivity, "Insert Ingredients", Toast.LENGTH_SHORT).show()
                    }
                    TextUtils.isEmpty(time) -> {
                        Toast.makeText(this@AddUpdateActivity, "Insert Cooking Time", Toast.LENGTH_SHORT).show()
                    }
                    TextUtils.isEmpty(direction) -> {
                        Toast.makeText(this@AddUpdateActivity, "Insert Direction", Toast.LENGTH_SHORT).show()
                    }
                    else ->{
                        var dishId = 0
                        var imageSource = Constants.SOURCE_LOCAL
                        var favDish = false

                        details?.let {
                            if (it.id != 0){
                                dishId = it.id
                                imageSource = it.imageSource
                                favDish = it.fav
                            }
                        }

                        val dishDetails = Dish(imagePath, imageSource, title, type, category, ingredients, time, direction, favDish, dishId)

                        if (dishId == 0){
                            viewModel.insertDishData(dishDetails)
                            Toast.makeText(this@AddUpdateActivity, "Inserted!", Toast.LENGTH_SHORT).show()
                        }else{
                            viewModel.updateDishDetails(dishDetails)
                            Toast.makeText(this@AddUpdateActivity, "Updated!", Toast.LENGTH_SHORT).show()
                        }
                        finish()
                    }
                }
            }
        }
    }

    private fun showSelectionDialog(){
        dialog = Dialog(this)
        val binding: DialogImageSelectionBinding = DialogImageSelectionBinding.inflate(layoutInflater)
        dialog.setContentView(binding.root)

        binding.tvCamera.setOnClickListener {
            Dexter.withContext(this)
                .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                                 Manifest.permission.CAMERA)
                .withListener(object: MultiplePermissionsListener{
                    override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                        report?.let {
                            if (report.areAllPermissionsGranted()) {
                                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                                startActivityForResult(intent, CAMERA)
                            }
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(permissions: MutableList<PermissionRequest>?, token: PermissionToken?) {
                        showPermissionDialog()
                    }
                }).onSameThread().check()
            dialog.dismiss() }

        binding.tvGallery.setOnClickListener {
            Dexter.withContext(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(object: PermissionListener{
                    override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                        startActivityForResult(intent, GALLERY)
                    }

                    override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                        Toast.makeText(this@AddUpdateActivity, "You have denied gallery permission!", Toast.LENGTH_SHORT).show()
                    }

                    override fun onPermissionRationaleShouldBeShown(permission: PermissionRequest?, token: PermissionToken?) {
                        showPermissionDialog()
                    }
                } )
            dialog.dismiss() }
        dialog.show()
    }

    private fun showPermissionDialog(){
        AlertDialog.Builder(this@AddUpdateActivity).setMessage("No permission granted")
            .setPositiveButton("Go to Settings"){
                    _, _ ->
                        try {
                            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                            val uri = Uri.fromParts("package", packageName, null)
                            intent.data = uri
                            startActivity(intent)
                        }catch (e: ActivityNotFoundException){
                            e.printStackTrace()
                        }
            }
            .setNegativeButton("Cancel"){
                    dialog, _ -> dialog.dismiss()
            }.show()
    }

    fun selectedListItem(item: String, selection: String){
        when(selection){
            Constants.TYPE -> {
                dialog.dismiss()
                binding.etType.setText(item)
            }
            Constants.CATEGORY -> {
                dialog.dismiss()
                binding.etCategory.setText(item)
            }
            else -> {
                dialog.dismiss()
                binding.etCookingTime.setText(item)
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK){
            if (requestCode == CAMERA){
                data?.extras?.let {
                    val thumbnail: Bitmap = data.extras!!.get("data") as Bitmap
                    Glide.with(this)
                        .load(thumbnail)
                        .centerCrop()
                        .into(binding.ivDish)
                    imagePath = saveImageToPhone(thumbnail)
                    Log.i("imagePath", imagePath)
                    binding.ivAddDish.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_edit))
                }
            }
            if (requestCode == GALLERY){
                data?.let {
                    val selectedPhoto = data.data
                    Glide.with(this)
                        .load(selectedPhoto)
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .listener(object : RequestListener<Drawable>{
                            override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                                Log.e("TAG", "Error loading image", e)
                                return false
                            }

                            override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                                resource?.let {
                                    val bitmap: Bitmap = resource.toBitmap()
                                    imagePath = saveImageToPhone(bitmap)
                                    Log.i("ImagePath", imagePath)
                                }
                                return false
                            }
                        })
                        .into(binding.ivDish)
                    binding.ivAddDish.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_edit))
                }
            }
        }else if (resultCode == Activity.RESULT_CANCELED)
            Log.e("cancelled", "cancelled")
    }

    private fun saveImageToPhone(bitmap: Bitmap): String{
        val wrapper = ContextWrapper(applicationContext)
        var file = wrapper.getDir(IMAGE_DIRECTORY, Context.MODE_PRIVATE)
        file = File(file, "${UUID.randomUUID()}.jpg")
        try {
            val stream: OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            stream.flush()
            stream.close()
        }catch (e: IOException){
            e.printStackTrace()
        }
        return file.absolutePath
    }

    private fun listDialog(title: String, itemList: List<String>, selection: String){
        dialog = Dialog(this)
        val binding = DialogListsBinding.inflate(layoutInflater)
        dialog.setContentView(binding.root)
        binding.tvTitle.text = title
        binding.rvList.layoutManager = LinearLayoutManager(this)

        listAdapter = ListsAdapter(this, null, itemList, selection)
        binding.rvList.adapter = listAdapter
        dialog.show()
    }
}