package kr.ac.kumoh.s20181180.catdoctor

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Matrix
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import kotlinx.android.synthetic.main.activity_photo_diagnose.*
import org.tensorflow.lite.Interpreter
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.text.SimpleDateFormat
import java.util.*
import java.io.IOException as IOException1


class PhotoDiagnoseActivity : AppCompatActivity() {
    private val FROM_ALBUM = 1
    private val REQUEST_IMAGE_CAPTURE = 2
    lateinit var currentPhotoPath : String
    private var symptom_id = ArrayList<Int>()
    var train_disease_id = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_diagnose)

        settingPermission() // 권한체크 시작

        btn_gallery.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(intent, FROM_ALBUM)
        }

        btn_picture.setOnClickListener {
            startCapture()
        }

        btn_result.setOnClickListener {
            if (train_disease_id == -1)
                Toast.makeText(getApplication(), "먼저 이미지를 가져와주세요.", Toast.LENGTH_LONG).show()
            else {
                val intent = Intent(this@PhotoDiagnoseActivity, DiseaseDetailActivity::class.java)
                intent.putExtra(DiagnoseViewModel.DISEASE_ID, train_disease_id.toString())
                intent.putExtra(DiagnoseViewModel.SYMPTOM_ID, symptom_id)
                startActivity(intent)
            }
        }
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // 카메라로 사진 찍기
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            val file = File(currentPhotoPath)
            val bitmap = MediaStore.Images.Media
                        .getBitmap(contentResolver, Uri.fromFile(file))

            // 이미지 회전되는 현상 해결
            val matrix = Matrix().apply { postRotate(90f) }
            val bitmapSource = bitmap
            val rotatedBitmap = Bitmap.createBitmap(bitmapSource, 0, 0, bitmapSource.width, bitmapSource.height, matrix, true)
            img_picture.setImageBitmap(rotatedBitmap)

            val batchNum = 0
            val input = Array(1) { Array(64) { Array(64) { FloatArray(3) } } }
            val output = Array(1) { FloatArray(6) }

            for (x in 0..63) {
                for (y in 0..63) {
                    val pixel = rotatedBitmap.getPixel(x, y)
                    input[batchNum][x][y][0] = Color.red(pixel) / 1.0f
                    input[batchNum][x][y][1] = Color.green(pixel) / 1.0f
                    input[batchNum][x][y][2] = Color.blue(pixel) / 1.0f
                }
            }

            val lite = getTfliteInterpreter("converted_model5.tflite")
            lite!!.run(input, output)

            var max = 0.0f
            var max_index = -1
            for (i in 0..5)
            {
                Log.i("train", output[0][i].toString())
                if (max < output[0][i]){
                    max = output[0][i]
                    max_index = i
                }
            }
            if (max_index == 0) {   // 링웜
                tv_output.text = "Ringworm와\n"+ output[0][0] * 100 +"%\n일치합니다."
                train_disease_id = 964
            } else if (max_index == 1) {    // 턱드름
                tv_output.text = "Feline Acne와\n"+ output[0][1] * 100 +"%\n일치합니다."
                train_disease_id = 965
            } else if (max_index == 2) {    // 외이도염
                tv_output.text = "External Otitis와\n"+ output[0][2] * 100 +"%\n일치합니다."
                train_disease_id = 966
            } else if (max_index == 3) {    // 치주염
                tv_output.text = "Periodontal Disease와\n"+ output[0][3] * 100 +"%\n일치합니다."
                train_disease_id = 967
            } else if (max_index == 4) {    // 구내염
                tv_output.text = "Feline Stomatitis와\n"+ output[0][4] * 100 +"%\n일치합니다."
                train_disease_id = 834
            } else if (max_index == 5) { // 포도막염
                tv_output.text = "Uveitis와\n"+ output[0][5] * 100 +"%\n일치합니다."
                train_disease_id = 923
            }else {
                tv_output.text = "예측 결과가 없습니다."
            }

        }

        // 앨범에서 사진 가져오기
        if (requestCode == FROM_ALBUM && resultCode == Activity.RESULT_OK) {
            val batchNum = 0
            val buf: InputStream? = contentResolver.openInputStream(data!!.data!!)
            var bitmap = BitmapFactory.decodeStream(buf)
            buf!!.close()

            // 이미지 회전되는 현상 해결
//            val matrix = Matrix().apply { postRotate(90f) }
//            val bitmapSource = bitmap
//            val rotatedBitmap = Bitmap.createBitmap(bitmapSource, 0, 0, bitmapSource.width, bitmapSource.height, matrix, true)
            img_picture.setImageBitmap(bitmap)
            bitmap = Bitmap.createScaledBitmap(bitmap, 64, 64, true)

            val input = Array(1) { Array(64) { Array(64) { FloatArray(3) } } }
            val output = Array(1) { FloatArray(6) }

            for (x in 0..63) {
                for (y in 0..63) {
                    val pixel = bitmap.getPixel(x, y)
                    input[batchNum][x][y][0] = Color.red(pixel) / 1.0f
                    input[batchNum][x][y][1] = Color.green(pixel) / 1.0f
                    input[batchNum][x][y][2] = Color.blue(pixel) / 1.0f
                }
            }


            val lite = getTfliteInterpreter("converted_model5.tflite")
            lite!!.run(input, output)

            var max = 0.0f
            var max_index = -1
            for (i in 0..5)
            {
                Log.i("train", output[0][i].toString())
                if (max < output[0][i]){
                    max = output[0][i]
                    max_index = i
                }
            }

            if (max_index == 0) {   // 링웜
                tv_output.text = "Ringworm와\n"+ output[0][0] * 100 +"%\n일치합니다."
                train_disease_id = 964
            } else if (max_index == 1) {    // 턱드름
                tv_output.text = "Feline Acne와\n"+ output[0][1] * 100 +"%\n일치합니다."
                train_disease_id = 965
            } else if (max_index == 2) {    // 외이도염
                tv_output.text = "External Otitis와\n"+ output[0][2] * 100 +"%\n일치합니다."
                train_disease_id = 966
            } else if (max_index == 3) {    // 치주염
                tv_output.text = "Periodontal Disease와\n"+ output[0][3] * 100 +"%\n일치합니다."
                train_disease_id = 967
            } else if (max_index == 4) {    // 구내염
                tv_output.text = "Feline Stomatitis와\n"+ output[0][4] * 100 +"%\n일치합니다."
                train_disease_id = 834
            } else if (max_index == 5) { // 포도막염
                tv_output.text = "Uveitis와\n"+ output[0][5] * 100 +"%\n일치합니다."
                train_disease_id = 923
            }else {
                tv_output.text = "예측 결과가 없습니다."
            }
        }
    }



    private fun getTfliteInterpreter(modelPath: String): Interpreter? {
        try {
            return Interpreter(loadModelFile(this@PhotoDiagnoseActivity, modelPath)!!)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }


    @Throws(IOException1::class)
    fun loadModelFile(activity: Activity, modelPath: String?): MappedByteBuffer? {
        val fileDescriptor = activity.assets.openFd(modelPath!!)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    @Throws(IOException1::class)
    private fun createImageFile() : File{
        val timeStamp : String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir : File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
                "JPEG_${timeStamp}_",
                ".jpg",
                storageDir
        ).apply{
            currentPhotoPath = absolutePath
        }
    }

    fun settingPermission(){
        var permis = object  : PermissionListener {
            //            어떠한 형식을 상속받는 익명 클래스의 객체를 생성하기 위해 다음과 같이 작성
            override fun onPermissionGranted() {
                Toast.makeText(this@PhotoDiagnoseActivity, "권한 허가", Toast.LENGTH_SHORT)
                        .show()
            }

            override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                Toast.makeText(this@PhotoDiagnoseActivity, "권한 거부", Toast.LENGTH_SHORT)
                        .show()
                //ActivityCompat.finishAffinity(this@PhotoDiagnoseActivity) // 권한 거부시 앱 종료
            }
        }

        TedPermission.with(this)
                .setPermissionListener(permis)
                .setRationaleMessage("카메라 사진 권한 필요")
                .setDeniedMessage("카메라 권한 요청 거부")
                .setPermissions(
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                        android.Manifest.permission.CAMERA)
                .check()
    }

    fun startCapture(){
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                val photoFile: File? = try{
                    createImageFile()
                }catch(ex: IOException1){
                    null
                }
                photoFile?.also{
                    val photoURI : Uri = FileProvider.getUriForFile(
                            this,
                            "org.techtown.capturepicture.fileprovider",
                            it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                }
            }
        }
    }
}
