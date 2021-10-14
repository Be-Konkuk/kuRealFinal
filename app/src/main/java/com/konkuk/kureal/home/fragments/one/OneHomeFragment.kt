package com.konkuk.kureal.home.fragments.one

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.konkuk.kureal.databinding.FragmentHomeOneBinding
import com.konkuk.kureal.lookup.LookupActivity
import com.konkuk.kureal.posting.PostActivity


class OneHomeFragment : Fragment() {
    private val handler: Handler = Handler(Looper.getMainLooper())
    private var _binding: FragmentHomeOneBinding? = null
    private val binding get() = _binding ?: error("View를 참조하기 위해 binding이 초기화되지 않았습니다.")


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeOneBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btn_clicked() //화면 전환 확인용
    }

    fun btn_clicked(){
        binding.btnLookup.setOnClickListener { //글 조회 버튼 클릭
            val intent = Intent(context,LookupActivity::class.java)
            intent.putExtra("pk",6) //TODO : pk값 변경하기
            startActivity(intent)
        }
        binding.btnPost.setOnClickListener { //글 작성 버튼 클릭
            val intent = Intent(context, PostActivity::class.java)
            startActivity(intent)
        }
    }
}