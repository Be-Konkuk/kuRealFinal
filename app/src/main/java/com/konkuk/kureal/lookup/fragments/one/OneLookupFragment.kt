package com.konkuk.kureal.lookup.fragments.one

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.konkuk.kureal.databinding.FragmentLookupOneBinding
import com.konkuk.kureal.posting.fragments.one.OnePostViewModel


class OneLookupFragment : Fragment() {
    private val handler: Handler = Handler(Looper.getMainLooper())
    private var _binding: FragmentLookupOneBinding? = null
    private val binding get() = _binding ?: error("View를 참조하기 위해 binding이 초기화되지 않았습니다.")
    private val viewModel: OneLookupViewModel by viewModels() //위임초기화
    private lateinit var mContext: Context

    private var pk = -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLookupOneBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        getSafeArgs()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        observe()
    }

    private fun init(){
        //back버튼 클릭시
        binding.ivBack.setOnClickListener {
            activity?.finish()
        }
    }

    private fun observe(){
        viewModel.articleData.observe(viewLifecycleOwner){
            binding.tvNickname.text = it.nickname
            binding.tvArticle.text = it.article
            //.com다음/부터 JPEG전/까지 잘라서 -> /기준으로 버킷/폴더
            //버킷은 https://다음에,
//            val bucket = "kureal"
//            val folder = "photo"
//            val fileName = ""
//            var photoURL = "https://$bucket.s3.ap-northeast-2.amazonaws.com/$folder/$filename"
            Glide.with(requireContext())
                .load(it.photo)
                //.load("https://kureal.s3.ap-northeast-2.amazonaws.com/photo/JPEG_20210923_142444_5376845874471524384.jpg?response-content-disposition=inline&X-Amz-Security-Token=IQoJb3JpZ2luX2VjELH%2F%2F%2F%2F%2F%2F%2F%2F%2F%2FwEaDmFwLW5vcnRoZWFzdC0yIkcwRQIhAIhfRqQiXPa%2B2Zo4m%2FJDV7oQCzYFrCKeiCo7o%2B3xPv6qAiBybSMehzWUOgPLWc%2Fp9yGUw%2B1VBrjIK9Bd77YQCfov%2BSr2Agg7EAEaDDI1NDgwMDQ2NTk5MCIMK3WNlZ%2BNyyvXfXWnKtMC0tEq1QWHxVSSFOqGMJ3X5cG1jd3pXFvgJK1EKntO44W4g1yKNHgQqEYLmVrd3G02aWH3xwmGj4NMVYu%2BHboA17D7zo9wl61UOW4KbY0Zfc68ER2EoERvzed9sPtbSSJoQ%2FA%2FFFcjShDjFQ6Zag%2B7x4sa4XjGVOBl1LC3exCX51sXtx3oc%2FJAFLbN7UmO7ca5eAv054DMALvUmyrg%2Fn24AY63T4zjyhFxjeugloJdNQkQKVA07wBVLrC%2Bgb0ETjyw5Ap8hMqvHhbF%2FbfM8omUvih7ZBM8nOYMdvYx5P8GGnLXuquVKFr%2Fa5j4RjZEkxwsjrnn3TN%2FcZWvv3YdUUbU1i4ClQQERXq%2FsrFayfPH60kQ%2BhvGtteys0gbJI344AikWdtg6VIWW6ZhTQyWqJErEpkhOlbUSx%2FtUDELZYheq7pCDj4SbuqHdkAjqQh2HHKVUyB%2FMO6PnosGOrMCelvbWmtbo0%2Bp1lF%2BFNSOh1qelaIhRCt30f4Xylru33rsLOpum5WGF%2BtadPdHOzDDL3luYSKu%2FHVG0g75Oh8vY8Bmq6Cw%2F%2FnE3WvVnpFmB6ORyiR4fwnPMVOxBIlXULuANcXY9s881rDddby52y6ePrD%2Ftcu1YPAEvmoR2ib8vSBkd7BOmKgXSCZJCZrcltEFd3Z0sssneHTk40%2FWjQR39vC%2FqQvgwDXXC0LHJEhLjHCb%2FsIPOm%2BNto6WMMMVedzkffVL%2FIfCdfab4xbqsniyw4N57cVGzWiI2%2FFyS7mtyw1Thqpl5opOQI6w9qVj5CqyX%2BW7dh88AngZz275jPv1T%2BAdq0KtAJUC%2FCznZo9xw9bAqWU9gr80vpEjpOhv5ZtdK6J8fki5NzqQN%2F7RaChUTh8p8Q%3D%3D&X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20211014T020051Z&X-Amz-SignedHeaders=host&X-Amz-Expires=300&X-Amz-Credential=ASIATWU2KSBDGAPWUXKH%2F20211014%2Fap-northeast-2%2Fs3%2Faws4_request&X-Amz-Signature=19d1f3c9952b6bd696092703696798b4106baa02b4c60f235fae890c7de1d392")
                .into(binding.ivPhoto)
        }
    }

    //lookupActivity -> lookupFragment
    fun getSafeArgs(){
        arguments?.let{
            pk = it.getInt("pk")
            viewModel.geArticle(pk) //pk값 전달받기
        }
    }
}