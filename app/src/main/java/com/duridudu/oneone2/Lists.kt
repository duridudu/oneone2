package com.duridudu.oneone2

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.duridudu.oneone2.adapter.DiaryAdapter
import com.duridudu.oneone2.databinding.FragmentListBinding
import com.duridudu.oneone2.databinding.ItemDiariesBinding
import com.duridudu.oneone2.model.Diary
import com.duridudu.oneone2.viewmodel.DiaryViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Lists.newInstance] factory method to
 * create an instance of this fragment.
 */
class Lists: Fragment() {
    // TODO: Rename and change types of parameters
    lateinit var binding:FragmentListBinding
    lateinit var listBinding: ItemDiariesBinding
    lateinit var  diaryAdapter:DiaryAdapter
    private lateinit var database: FirebaseDatabase
    private lateinit var diaryRef: DatabaseReference
    private lateinit var userId: String // 사용자의 고유 식별자
    private var diariesList = mutableListOf<Diary>()
    // 프래그먼트 이동을 위한 뷰모델
    private lateinit var viewModel: DiaryViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding= FragmentListBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[DiaryViewModel::class.java]
        Log.d("LIST++", "onViewCreated")

        // 클릭 이벤트 처리법 같이 줌
        diaryAdapter = DiaryAdapter { diary ->
            viewModel.setSelectedDiary(diary)
            navigateToWriteFragment()


        }

        binding.listRecyclerview.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = diaryAdapter
        }


//        binding.listRecyclerview.layoutManager = LinearLayoutManager(requireContext())
//        diaryAdapter = DiaryAdapter(requireContext(), )
//        binding.listRecyclerview.adapter = diaryAdapter


        Log.d("LIST++", "after adapter")
        // Firebase 초기화 및 데이터 요청은 onViewCreated 내에서 처리
        initFirebase()
        Log.d("LIST++", "after initFirebase")


    }


    private fun initFirebase() {
        try {
            userId = "BKNnNTkD5kgW1VILsAtiib5Tpks2"
            // Firebase Database 인스턴스 초기화
            database = FirebaseDatabase.getInstance("https://oneone2-4660f-default-rtdb.asia-southeast1.firebasedatabase.app")
            diaryRef = database.getReference("users/$userId/diaries")
            Log.d("LIST++", "IN initFirebase")

            // 데이터 요청
            getFBContentData()
            Log.d("LIST++", "after getFBContentData")
        } catch (e: Exception) {
            Log.e("LIST++", "Firebase initialization failed", e)
        }
    }

    private fun getFBContentData() {
        Log.d("LIST++", "IN getFBContentData")
        val postListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d("LIST++", "onData")
                diariesList.clear()
                Log.d("LIST++", snapshot.childrenCount.toString())
                for (diarySnapshot in snapshot.children) {

                  val diary = diarySnapshot.getValue(Diary::class.java)
                   if (diary != null) {
                       //diary.title?.let { Log.d("LIST++", it) }
                        diary?.let {
                            // diary.timestamp에서 월 정보 가져오기
                            val dateString = it.timestamp
                            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                            val date = dateFormat.parse(dateString)

                            val calendar = Calendar.getInstance()
                            calendar.time = date

                            val month = calendar.get(Calendar.MONTH) + 1 // Calendar.MONTH는 0부터 시작하므로 +1 해줘야 함

                            // 여기서 원하는 월(예: 6월)에 해당하는 diary만 diariesList에 추가할 수 있음
                            if (month == 6) { // 6월에 해당하는 데이터만 추가 예시
                                diariesList.add(it)
                            }
                        }
                   }
                    }
                     //notifyDataSetChanged()를 호출하여 adapter에게 값이 변경 되었음을 알려준다.
                  diaryAdapter.submitList(diariesList)
                }
                override fun onCancelled(error: DatabaseError) {
                }
            }
      diaryRef.addValueEventListener(postListener)

    }

//    override fun onDeleteClick(diary: Diary) {
//        // 삭제 버튼이 클릭된 경우 처리할 로직 구현
//        viewModel.deleteDiary(diary) // 뷰모델에 삭제 명령 전달
//    }
    private fun navigateToWriteFragment() {
        val writeFragment = Write()
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, writeFragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    companion object {
        private const val TAG = "ListsFragment"
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment List.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Lists().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}