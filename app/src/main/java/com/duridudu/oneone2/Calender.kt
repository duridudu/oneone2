package com.duridudu.oneone2

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.view.children
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.duridudu.oneone2.adapter.DiaryAdapter
import com.duridudu.oneone2.databinding.ActivityMainBinding
import com.duridudu.oneone2.databinding.FragmentCalenderBinding
import com.duridudu.oneone2.model.Diary
import com.duridudu.oneone2.viewmodel.DiaryViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener
import com.prolificinteractive.materialcalendarview.format.ArrayWeekDayFormatter
import java.text.SimpleDateFormat

import java.time.DayOfWeek
import java.time.LocalDate

import java.time.YearMonth
import java.util.Calendar
import java.util.Locale

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Calender.newInstance] factory method to
 * create an instance of this fragment.
 */
@Suppress("UNREACHABLE_CODE")
class Calender : Fragment() {

    lateinit var binding:FragmentCalenderBinding
    lateinit var  diaryAdapter: DiaryAdapter
    private lateinit var database: FirebaseDatabase
    private lateinit var diaryRef: DatabaseReference
    private lateinit var userId: String // 사용자의 고유 식별자
    private var diariesList = mutableListOf<Diary>()
    // 프래그먼트 이동을 위한 뷰모델
    private lateinit var viewModel: DiaryViewModel

    // 선택된 날짜 저장
    private var selectedDate: String? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        //binding.calendarView.setWeekDayFormatter(ArrayWeekDayFormatter(resources.getTextArray(R.array.custom_weekdays)))
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentCalenderBinding.inflate(inflater)

        binding.calendarView.setOnDateChangedListener(object:OnDateSelectedListener{
            override fun onDateSelected(
                widget: MaterialCalendarView,
                date: CalendarDay,
                selected: Boolean
            ) {
                if (date.month < 10){
                    selectedDate = "${date.year}-0${date.month}-${date.day}"
                }else{
                    selectedDate = "${date.year}-${date.month}-${date.day}"
                }

                fetchDiariesForSelectedDate(selectedDate!!)
            }
        })

        return binding.root
    }
    private fun fetchDiariesForSelectedDate(date: String) {
        diaryRef.orderByChild("timestamp").startAt("$date 00:00").endAt("$date 23:59")
            .addListenerForSingleValueEvent(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {
                    Log.d("CALENDER++", date)
                    val diaries = mutableListOf<Diary>()
                    for (data in snapshot.children) {
                        val diary = data.getValue(Diary::class.java)
                        diary?.let { diaries.add(it) }
                    }
                    diaryAdapter.submitList(diaries)
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                }
            })
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

        binding.calendarRecyclerview.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = diaryAdapter
        }
        initFirebase()


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
    private fun navigateToWriteFragment() {
        val writeFragment = Write()
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, writeFragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }


}