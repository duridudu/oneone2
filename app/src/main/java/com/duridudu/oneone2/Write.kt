package com.duridudu.oneone2

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.duridudu.oneone2.adapter.DiaryAdapter
import com.duridudu.oneone2.databinding.FragmentWriteBinding
import com.duridudu.oneone2.model.Diary
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Write.newInstance] factory method to
 * create an instance of this fragment.
 */
class Write : Fragment() {
    // TODO: Rename and change types of parameters
    lateinit var binding: FragmentWriteBinding
    lateinit var  diaryAdapter: DiaryAdapter
    private lateinit var database: FirebaseDatabase
    private lateinit var diaryRef: DatabaseReference
    private lateinit var userId: String // 사용자의 고유 식별자

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentWriteBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userId = "BKNnNTkD5kgW1VILsAtiib5Tpks2"

        // Firebase Database 인스턴스 초기화
        database = FirebaseDatabase.getInstance("https://oneone2-4660f-default-rtdb.asia-southeast1.firebasedatabase.app")
        diaryRef = database.getReference("users/$userId/diaries")
        Log.d("WRITE++", "after firebase초기화")

        // 예시로 사용할 다이어리 데이터
        val diary = Diary(
            contentId = 4,
            title = "Fourth Diary",
            content = "Content of the fourth diary",
            timestamp = "2024-06-28",
            photoUrl = "https://www.google.com/url?sa=i&url=http%3A%2F%2Fs.blip.kr%2Fc%2F8287ef00&psig=AOvVaw1fTSz_x3W-KyNi7o5BeTXI&ust=1718527711483000&source=images&cd=vfe&opi=89978449&ved=0CBEQjRxqFwoTCLia79-c3YYDFQAAAAAdAAAAABAE"
        )

        // 새로운 다이어리 추가
        val newDiaryRef = diaryRef.push()
        newDiaryRef.setValue(diary)
    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Write.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Write().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}