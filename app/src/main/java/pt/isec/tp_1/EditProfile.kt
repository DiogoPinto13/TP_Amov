package pt.isec.tp_1

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.snackbar.Snackbar
import pt.isec.tp_1.databinding.ActivityEditProfileBinding

class EditProfile : AppCompatActivity(){
    companion object {
        private const val TITLE_KEY = "title"
        private const val IMAGE_KEY = "imagefile"

        private const val TAG = "EditProfileActivity"
        private const val ACTIVITY_REQUEST_CODE_GALLERY = 10
        private const val PERMISSIONS_REQUEST_CODE = 1

        private const val GALLERY = 1

        fun getGalleryIntent(context : Context) : Intent {
            return Intent(context,EditProfile::class.java)
        }

        private const val MODE_KEY = "mode"


        fun getIntent(context: Context, title: String, imagePath: String?): Intent {
            val intent = Intent(context, EditProfile::class.java)
            intent.putExtra(TITLE_KEY, title)
            intent.putExtra(IMAGE_KEY, imagePath)
            return intent
        }

    }

    private lateinit var binding : ActivityEditProfileBinding
    private var mode = GALLERY
    private var imagePath : String? = null
    private var permissionsGranted = false
        set(value) {
            field = value
            binding.btnImage.isEnabled = value
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mode = intent.getIntExtra(Companion.MODE_KEY, GALLERY)
        when (mode) {
            GALLERY ->
                binding.btnImage.apply {
                    text = getString(R.string.btnChooseImage)
                    setOnClickListener { chooseImage() }
                }
            else -> finish()
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        verifyPermissions()
        updatePreview()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_create,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.mnCreate) {
            if (binding.edTitle.text.trim().isEmpty()) {
                Snackbar.make(binding.edTitle,
                    R.string.msg_empty_title,
                    Snackbar.LENGTH_LONG).show()
                binding.edTitle.requestFocus()
                return true;
            }
            if (imagePath == null) {
                Snackbar.make(binding.edTitle,
                    R.string.msg_no_image,
                    Snackbar.LENGTH_LONG).show()
                return true;
            }
            val intent = Intent(
                EditProfile.getIntent(
                    this,
                    binding.edTitle.text.trim().toString(),
                    imagePath
                )
            )
            startActivity(intent)
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }


    var startActivityForContentResult = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        Log.i(TAG, "startActivityForContentResult: ")
        /*uri?.apply {
                val cursor = contentResolver.query(this,
                    arrayOf(MediaStore.Images.ImageColumns.DATA), null, null, null)
                if (cursor !=null && cursor.moveToFirst())
                    imagePath = cursor.getString(0)
                updatePreview()
        }*/
        imagePath = uri?.let { createFileFromUri(this, it) }
        updatePreview()
    }

    fun chooseImage() {
        Log.i(TAG, "chooseImage: ")
        startActivityForContentResult.launch("image/*")

    }

    fun updatePreview() {
        if (imagePath != null)
            setPic(binding.frPreview, imagePath!!)
        else
            binding.frPreview.background = ResourcesCompat.getDrawable(resources,
                android.R.drawable.ic_menu_report_image,
                null)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            permissionsGranted = grantResults.all { it == PackageManager.PERMISSION_GRANTED }
            return
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        permissionsGranted = isGranted
    }


    fun verifyPermissions() {
        Log.i(TAG, "verifyPermissions: ")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionsGranted = ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.READ_MEDIA_IMAGES
            ) == PackageManager.PERMISSION_GRANTED

            if (!permissionsGranted)
                requestPermissionLauncher.launch(android.Manifest.permission.READ_MEDIA_IMAGES)
            return
        }
        // GALLERY, versões < API33
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED /*||
            ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED */
        ) {
            permissionsGranted = false
            requestPermissionsLauncher.launch(
                arrayOf(
                    //android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                )
            )
        } else
            permissionsGranted = true
    }

    val requestPermissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()) { grantResults ->
        permissionsGranted = grantResults.values.any { it }
    }
}