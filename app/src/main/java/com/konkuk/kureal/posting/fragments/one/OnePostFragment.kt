package com.konkuk.kureal.posting.fragments.one

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.provider.MediaStore.Images.Media.getBitmap
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.konkuk.kureal.R
import com.konkuk.kureal.databinding.FragmentPostOneBinding
import com.konkuk.kureal.posting.fragments.Article
import com.konkuk.kureal.posting.fragments.one.api.PostingData
import com.konkuk.kureal.util.LocationHelper
import com.konkuk.kureal.util.picture.GalleryHelper
import com.konkuk.kureal.util.picture.PermissionHelper
import com.konkuk.kureal.util.picture.PictureHelper
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class OnePostFragment : Fragment() {
    private var _binding: FragmentPostOneBinding? = null
    private val binding get() = _binding ?: error("View를 참조하기 위해 binding이 초기화되지 않았습니다.")
    private val viewModel: OnePostViewModel by viewModels() //위임초기화
    private lateinit var mContext: Context

    //권한 요청
    private lateinit var permissionHelper : PermissionHelper
    private lateinit var galleryHelper: GalleryHelper
    private lateinit var pictureHelper: PictureHelper

    private val handler: Handler = Handler(Looper.getMainLooper()) //tmp

    private lateinit var article: PostingData
    private var latitude:Double = 0.0
    private var longitude:Double = 0.0
    private var photoURL:String = ""

    private var gpsReady = false
    private var articleReady = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPostOneBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        mContext = requireContext()

        permissionHelper = PermissionHelper(mContext) //권한 요청
        galleryHelper = GalleryHelper(mContext) //사진 촬영 및 저장
        pictureHelper = PictureHelper(mContext) //사진 합치기 및 절대경로 변환

        goCheckPermissions()
        getLocation() //위도,경도 값 설정

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        openCamera()
        clickPost() //공유 버튼 클릭
    }



    //공유 버튼 클릭
    private fun clickPost(){
        binding.tvPost.setOnClickListener {
            //사용자가 article, nickname 작성 -> not null 테스트 필요 (null이면 공유 버튼 눌렀을 때 다 작성해달라는 토스트메시지 생성)
            //사용자가 tag 작성 -> null 테스트 불필요
            //공유 누르는 순간 date, gps값 받아오고 -> 서버로 데이터 보내기

            if (binding.etArticle.text.isEmpty() || binding.etNickname.text.isEmpty()) {
                Toast.makeText(getContext(),"글 내용 혹은 닉네임은 필수 입력 조건입니다.",Toast.LENGTH_LONG).show()
            }
            else{

                article = PostingData(getDate(),binding.etNickname.text.toString(),binding.etArticle.text.toString(),
                    photoURL,binding.etTag.text.toString(),latitude,longitude)
                articleReady = true

                sendToServer()
            }
        }
    }

    private fun sendToServer(){
        if (gpsReady && articleReady){ //gps, article다 준비됐을 때 서버에 전송
            gpsReady = false
            articleReady = false

            article = PostingData(article.date,article.nickname,article.article,
                article.photo,article.tag,article.latitude,article.longitude)

            Log.d("***POST_DATA",article.date+","+article.nickname+","+article.article+","+article.photo+","+article.tag+","+article.latitude+","+article.longitude)
            //Toast.makeText(getContext(),article.date+","+article.nickname+","+article.article+","+article.photo+","+article.tag+","+article.latitude+","+article.longitude,Toast.LENGTH_LONG).show()

            //TODO : 서버 연결
            viewModel.posting(article)
        }
    }

    private fun getDate() :String {
        val date = Date(System.currentTimeMillis())
        val dateFormat = SimpleDateFormat("yyyy.MM.dd", Locale("ko","KR"))

        return dateFormat.format(date)
    }

    //TODO : 왜... 늦게..동작해서 날..빡치게 하는거ㅑㅇ.....
    private fun getLocation(){
        LocationHelper().startListeningUserLocation(requireContext() , object : LocationHelper.MyLocationListener {
            override fun onLocationChanged(location: Location) {
                // Here you got user location :)
                Log.d("Location","" + location.latitude + "," + location.longitude)

                latitude = location.latitude
                longitude = location.longitude

                gpsReady = true
                sendToServer()
            }
        })
    }

    /**
     * 카메라 activity */
    private fun openCamera(){
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(mContext.packageManager)?.also {
                // 사진 파일을 만듭니다.
                val photoFile = galleryHelper.photoFile

                // photoUri를 보내는 코드
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        mContext,
                        "com.konkuk.kureal.fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    cameraActivityLauncher.launch(takePictureIntent)
                }
            }
        }
    }

    private val cameraActivityLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){
            result: ActivityResult ->
        // TODO : 다른 FRAGMENT를 갔다가 들어오면 launch가 제대로 동작하지 않음
        galleryHelper.galleryAddPic()

        // imageview에 사진 띄우기
        var file = File(galleryHelper.currentPhotoPath); //TODO : 이거를 서버에 보내야함
        var bitmap = MediaStore.Images.Media
            .getBitmap(mContext.contentResolver, Uri.fromFile(file));
        if (bitmap != null) {
            binding.ivPhoto.setImageBitmap(bitmap); //화면 썸네일에 그림 넣기
        }

        //s3로 전송, article.photo에 주소 넣기
        photoURL = viewModel.uploadWithTransferUtilty(file.getName(),file)
    }

    /**
     * Permission 체크 */
    fun goCheckPermissions(){
        if(!permissionHelper.checkPermissions()){ //권한 요청 거부
            permissionHelper.requestPermission()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(!permissionHelper.permissionResult(requestCode,permissions ,grantResults)){
            permissionHelper.requestPermission()
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        gpsReady = false
        articleReady = false
    }
    

}