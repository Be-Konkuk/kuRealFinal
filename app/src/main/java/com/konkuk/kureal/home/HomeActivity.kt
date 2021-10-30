package com.konkuk.kureal.home

import android.Manifest
import android.annotation.TargetApi
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.RectF
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Surface
import android.view.SurfaceView
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.amazonaws.mobileconnectors.s3.transferutility.*
import com.konkuk.kureal.databinding.ActivityHomeBinding
import com.konkuk.kureal.lookup.LookupActivity
import com.konkuk.kureal.posting.PostActivity
import org.opencv.android.*
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2
import org.opencv.core.Mat
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

class HomeActivity : AppCompatActivity() , CvCameraViewListener2{
    private var _binding: ActivityHomeBinding? = null
    private val binding get() = _binding ?: error("View를 참조하기 위해 binding이 초기화되지 않았습니다.")
    private val viewModel: HomeViewModel by viewModels()

    private var matInput: Mat? = null
    private var mOpenCvCameraView: CameraBridgeViewBase? = null

    companion object {
        private const val TAG = "opencv"

        const val CAMERA_PERMISSION_REQUEST_CODE = 200
    }

    private val mLoaderCallback: BaseLoaderCallback = object : BaseLoaderCallback(this) {
        override fun onManagerConnected(status: Int) {
            when (status) {
                SUCCESS -> {
                    mOpenCvCameraView!!.enableView()
                }
                else -> {
                    super.onManagerConnected(status)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        window.setFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
        )

        mOpenCvCameraView = binding.jcvCamera
        mOpenCvCameraView!!.visibility = SurfaceView.VISIBLE
        mOpenCvCameraView!!.setCvCameraViewListener(this)
        mOpenCvCameraView!!.setCameraIndex(0) // front-camera(1),  back-camera(0)

        btnClicked()
        //divideVideo()
    }

    protected fun btnClicked(){
        binding.btnLookup.setOnClickListener { //글 조회 버튼 클릭
            val intent = Intent(this, LookupActivity::class.java)
            intent.putExtra("pk",8) //TODO : pk값 변경하기
            startActivity(intent)
        }
        binding.btnPost.setOnClickListener { //글 작성 버튼 클릭
            val intent = Intent(this, PostActivity::class.java)
            startActivity(intent)
        }
    }

    protected fun divideVideo(){
//        //버튼 클릭으로
//        val btn = findViewById<View>(R.id.btn_camera) as Button
//        btn.setOnClickListener {
//            Log.d("BUTTON", "CLICKED!!!! S3 GO!!!")
//
//            //mat to bitmap
//            val bitmap = Bitmap.createBitmap(
//                matInput.cols(),
//                matInput.rows(),
//                Bitmap.Config.ARGB_8888
//            )
//            Utils.matToBitmap(matInput, bitmap)
//            //bitmap to file
//            saveBitmapToJpeg(bitmap, "tmpBitmap")
//            //s3 file send
//            val sendFile = File("$cacheDir/tmpBitmap.jpg")
//            viewModel.uploadWithTransferUtilty("5test" + System.currentTimeMillis(), sendFile)
//        }

        //n초마다 S3로 사진 전송
        val timer = Timer()
        val timerTask: TimerTask = object : TimerTask() {
            override fun run() { // 코드 작성
                if (matInput!=null) {
                    //mat to bitmap
                    val bitmap = matInput?.cols()?.let {
                        Bitmap.createBitmap(
                            it,
                            matInput?.rows()!!,
                            Bitmap.Config.ARGB_8888
                        )
                    }
                    Utils.matToBitmap(matInput, bitmap)
                    //bitmap to file
                    if (bitmap != null) {
                        saveBitmapToJpeg(bitmap, "tmpBitmap")
                    }
                    //s3 file send
                    val sendFile = File("$cacheDir/tmpBitmap.jpg")
                    viewModel.uploadWithTransferUtilty("5test" + System.currentTimeMillis(), sendFile)
                }
            }
        }
        timer.schedule(timerTask, 0, 1000)
    }

    private fun saveBitmapToJpeg(bitmap: Bitmap, name: String): File? {

        //내부저장소 캐시 경로를 받아옵니다.
        val storage = cacheDir

        //저장할 파일 이름
        val fileName = "$name.jpg"

        //storage 에 파일 인스턴스를 생성합니다.
        val tempFile = File(storage, fileName)
        try {

            // 자동으로 빈 파일을 생성합니다.
            tempFile.createNewFile()

            // 파일을 쓸 수 있는 스트림을 준비합니다.
            val out = FileOutputStream(tempFile)

            // compress 함수를 사용해 스트림에 비트맵을 저장합니다.
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)

            // 스트림 사용후 닫아줍니다.
            out.close()

//            File sendFile = new File(getCacheDir()+"/tmpBitmap.jpg");
//            uploadWithTransferUtilty("3test"+System.currentTimeMillis(), sendFile);
        } catch (e: FileNotFoundException) {
            Log.e("MyTag", "FileNotFoundException : " + e.message)
        } catch (e: IOException) {
            Log.e("MyTag", "IOException : " + e.message)
        }
        return tempFile
    }

    public override fun onPause() {
        super.onPause()
        if (mOpenCvCameraView != null) mOpenCvCameraView!!.disableView()
    }

    public override fun onResume() {
        super.onResume()
        if (!OpenCVLoader.initDebug()) {
            Log.d("HOMEACTIVITY", "onResume :: Internal OpenCV library not found.")
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_2_0, this, mLoaderCallback)
        } else {
            Log.d("HOMEACTIVITY", "onResum :: OpenCV library found inside package. Using it!")
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS)
        }
    }

    public override fun onDestroy() {
        super.onDestroy()
        if (mOpenCvCameraView != null) mOpenCvCameraView!!.disableView()
    }

    override fun onCameraViewStarted(width: Int, height: Int) {}
    override fun onCameraViewStopped() {}
    override fun onCameraFrame(inputFrame: CvCameraViewFrame): Mat? {
        matInput = inputFrame.rgba()
        //val rotateMat = matInput?.t()
        return matInput as Mat?
    }

    protected val cameraViewList: List<CameraBridgeViewBase>
        protected get() = listOf(mOpenCvCameraView) as List<CameraBridgeViewBase>

    protected fun onCameraPermissionGranted() {
        val cameraViews = cameraViewList ?: return
        for (cameraBridgeViewBase in cameraViews) {
            cameraBridgeViewBase?.setCameraPermissionGranted()
        }
    }

    override fun onStart() {
        super.onStart()
        var havePermission = true
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                    arrayOf(Manifest.permission.CAMERA),
                    HomeActivity.CAMERA_PERMISSION_REQUEST_CODE
                )
                havePermission = false
            }
        }
        if (havePermission) {
            onCameraPermissionGranted()
        }
    }

    //사용 권한 확인
    @TargetApi(Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == HomeActivity.CAMERA_PERMISSION_REQUEST_CODE && grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            onCameraPermissionGranted()
        } else {
            showDialogForPermission("앱을 실행하려면 퍼미션을 허가하셔야합니다.")
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    @TargetApi(Build.VERSION_CODES.M)
    private fun showDialogForPermission(msg: String) {
        val builder = AlertDialog.Builder(this@HomeActivity)
        builder.setTitle("알림")
        builder.setMessage(msg)
        builder.setCancelable(false)
        builder.setPositiveButton(
            "예"
        ) { dialog, id ->
            requestPermissions(
                arrayOf(Manifest.permission.CAMERA),
                HomeActivity.CAMERA_PERMISSION_REQUEST_CODE
            )
        }

        @TargetApi(Build.VERSION_CODES.M)
        fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<String>,
            grantResults: IntArray
        ) {
            if (requestCode == HomeActivity.CAMERA_PERMISSION_REQUEST_CODE && grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                onCameraPermissionGranted()
            } else {
                showDialogForPermission("앱을 실행하려면 퍼미션을 허가하셔야합니다.")
            }
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }

        @TargetApi(Build.VERSION_CODES.M)
        fun showDialogForPermission(msg: String) {
            val builder = AlertDialog.Builder(this@HomeActivity)
            builder.setTitle("알림")
            builder.setMessage(msg)
            builder.setCancelable(false)
            builder.setPositiveButton(
                "예"
            ) { dialog, id ->
                requestPermissions(
                    arrayOf(Manifest.permission.CAMERA),
                    HomeActivity.CAMERA_PERMISSION_REQUEST_CODE
                )
            }
            builder.setNegativeButton(
                "아니오"
            ) { arg0, arg1 -> finish() }
            builder.create().show()
        }
        builder.setNegativeButton(
            "아니오"
        ) { arg0, arg1 -> finish() }
        builder.create().show()
    }
}