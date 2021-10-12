package com.konkuk.kureal.posting.fragments.one

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.provider.MediaStore.Images.Media.getBitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.konkuk.kureal.R
import com.konkuk.kureal.databinding.FragmentPostOneBinding
import com.konkuk.kureal.util.picture.GalleryHelper
import com.konkuk.kureal.util.picture.PermissionHelper
import com.konkuk.kureal.util.picture.PictureHelper
import java.io.File


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

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        openCamera()
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
            binding.ivPhoto.setImageBitmap(bitmap);
        }
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

}