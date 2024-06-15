package com.duridudu.oneone2

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.duridudu.oneone2.adapter.DiaryAdapter
import com.duridudu.oneone2.databinding.FragmentWriteBinding
import com.duridudu.oneone2.model.Diary
import com.duridudu.oneone2.viewmodel.DiaryViewModel
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import io.github.muddz.styleabletoast.StyleableToast
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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

    private var title: String = "" // 입력된 제목
    private var content: String = "" // 입력된 내용
    private var photoUrl: String = "" // 입력된 내용
    private var diaryId : String? = null // 파이어베이스 다이어리객체 고유 키

    // 작성된 글일 경우 불러오기 위한 뷰모델
    private lateinit var viewModel: DiaryViewModel
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
        viewModel = ViewModelProvider(requireActivity()).get(DiaryViewModel::class.java)

        // ViewModel에서 선택된 Diary 가져오기
        var selectedDiary = viewModel.getSelectedDiary()

        // 수정으로 넘어온 경우!! 하 수정버튼을 만드는게 낫겠음.. 쩝
        if (selectedDiary != null) {

            // 일기 영역 보이게
            binding.btnWrite.visibility = View.INVISIBLE
            binding.writeContent.visibility = View.VISIBLE

            // 선택된 Diary 데이터를 UI에 설정하는 코드
            binding.title.setText(selectedDiary.title)
            binding.writeContent.setText(selectedDiary.content)

            // 업데이트를 위해 고유키 설정
            diaryId = selectedDiary.diaryId.toString()

            Log.d("WRITE++", "UPDATE : $diaryId")

            // 수정 버전으로 파이어베이스 생성
            initFirebase("update", diaryId)
        }
        else{
            Log.d("WRITE++", "CREATE")
            // 신규 추가 버전으로 파이어베이스 생성
            initFirebase("create", null)
        }
        // 사진 등록
        binding.btnPhoto.setOnClickListener {
            binding.btnPhoto.visibility = View.INVISIBLE
            // 사진 업로드 기능

        }
        // 일기 등록
        binding.btnWrite.setOnClickListener {
            binding.btnWrite.visibility = View.INVISIBLE
            binding.writeContent.visibility = View.VISIBLE
        }

        // 최종 등록 버튼
        binding.btnRegister.setOnClickListener {
            if (binding.title.text == null){
                StyleableToast.makeText(requireContext(), "제목을 입력해주세요!", R.style.myToast).show()
            }
            else{
                StyleableToast.makeText(requireContext(), "저장되었습니다.",R.style.myToast).show()
            }

            Log.d("WRITE++", "Register : $diaryId")
            // 예시로 사용할 다이어리 데이터


            if (selectedDiary != null) {
                val diary = Diary(
                    diaryId = diaryId,
                    title =  binding.title.text.toString(),
                    content = content,
                    timestamp = getCurrentTimestamp(),
                    photoUrl = photoUrl
                )
                // 해당 경로의 데이터를 업데이트
                diaryRef.setValue(diary)
                    .addOnSuccessListener {
                    // 성공적으로 업데이트된 경우 처리할 로직
                    Log.d("Firebase", "Diary updated successfully")
                }
                    .addOnFailureListener { e ->
                        // 업데이트 실패 시 처리할 로직
                        Log.w("Firebase", "Error updating diary", e)
                    }
            }
            else{
                val diary = Diary(
                    title =  binding.title.text.toString(),
                    content = content,
                    timestamp = getCurrentTimestamp(),
                    photoUrl = photoUrl
                )
                // 새로운 다이어리 추가
                val newDiaryRef = diaryRef.push()
                newDiaryRef.setValue(diary)
                val diaryId = newDiaryRef.key // 생성된 고유 키 가져오기

                // Diary 객체에 고유 키 설정
                newDiaryRef.child("diaryId").setValue(diaryId)
            }

        }
    }

    private fun initFirebase(s: String, key:String?) {
        var key = key
        userId = "BKNnNTkD5kgW1VILsAtiib5Tpks2"
        // Firebase Database 인스턴스 초기화
        database = FirebaseDatabase.getInstance("https://oneone2-4660f-default-rtdb.asia-southeast1.firebasedatabase.app")
        try {
            if (s == "create"){
                diaryRef = database.getReference("users/$userId/diaries")
                Log.d("WRITE++", "IN NEW initFirebase")

            }
            else{
                diaryRef = database.getReference("users/$userId/diaries/$key")
                Log.d("WRITE++", "IN UPDATE initFirebase")
            }

        } catch (e: Exception) {
            Log.e("WRITE++", "Firebase initialization failed", e)
        }
    }
    private fun getCurrentTimestamp(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        return dateFormat.format(Date())
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