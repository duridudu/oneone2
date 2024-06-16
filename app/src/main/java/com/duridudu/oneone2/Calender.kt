package com.duridudu.oneone2

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
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
import com.prolificinteractive.materialcalendarview.spans.DotSpan
import java.text.SimpleDateFormat

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
        val today = CalendarDay.today()

        // 오늘 날짜를 자동으로 선택
        binding.calendarView.setDateSelected(today, true)
        // 다른 날짜 클릭 시 일정 불러오기
        binding.calendarView.setOnDateChangedListener(object:OnDateSelectedListener{
            override fun onDateSelected(
                widget: MaterialCalendarView,
                date: CalendarDay,
                selected: Boolean
            ) {
                Log.d("CALENDER++", "setOnDateChanged${date.month}")
                if (date.month < 10){
                    selectedDate = "${date.year}-0${date.month}-${date.day}"
                }else{
                    selectedDate = "${date.year}-${date.month}-${date.day}"
                }
                Log.d("CALENDER++", "setOnDateChanged2$selectedDate")
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
                    Log.d("CALENDER++","fetchDiaries ${snapshot.children.count()}개")
                    for (data in snapshot.children) {
                        val diary = data.getValue(Diary::class.java)
                        if (diary != null) {
                            Log.d("CALENDER++","IN ${diary.title}")
                        }
                        diary?.let { diaries.add(it) }
                    }
                    Log.d("CALENDER++","BEFORE adopter")
                    diaryAdapter.submitList(diaries)
                    Log.d("CALENDER++","AFTER adopter")
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                }
            })
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[DiaryViewModel::class.java]
        Log.d("CALENDER++", "onViewCreated")

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
        //initCalenderView()

    }

    private fun initCalenderView() {
        TODO("Not yet implemented")
    }


    private fun initFirebase() {
        try {
            userId = "BKNnNTkD5kgW1VILsAtiib5Tpks2"
            // Firebase Database 인스턴스 초기화
            database = FirebaseDatabase.getInstance("https://oneone2-4660f-default-rtdb.asia-southeast1.firebasedatabase.app")
            diaryRef = database.getReference("users/$userId/diaries")
            Log.d("CALENDER++", "IN initFirebase")

            // 데이터 요청
            getFBContentData()
            Log.d("CALENDER++", "after getFBContentData")
        } catch (e: Exception) {
            Log.e("CALENDER++", "Firebase initialization failed", e)
        }
    }

    private fun getFBContentData() {
        Log.d("CALENDER++", "IN getFBContentData")
        val postListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d("CALENDER++", "onData")
                val datesWithEntries = mutableListOf<CalendarDay>()
                diariesList.clear()
                Log.d("CALENDER++", snapshot.childrenCount.toString())
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

                            val day = CalendarDay.from(2024, date.month+1, date.date)
                            Log.d("CALENDER++3", day.toString())
                            // 날짜 리스트에 추가
                            datesWithEntries.add(day)
                            Log.d("CALENDER++", "AFTER datesWithEntries")
                           // diariesList.add(it)
                            Log.d("CALENDER++", "AFTER diariesList")
//                            // 여기서 원하는 월(예: 6월)에 해당하는 diary만 diariesList에 추가할 수 있음
                            if (month == 6) { // 6월에 해당하는 데이터만 추가 예시
                                diariesList.add(it)
                            }
                        }
                        Log.d("CALENDER++", "BEFORE dot")
//                        // 머티리얼 캘린더뷰에 Decorator 추가

                    }
                }
                binding.calendarView.addDecorators(
                    DotDecorator(requireContext(), R.color.main, datesWithEntries)
                )
                //notifyDataSetChanged()를 호출하여 adapter에게 값이 변경 되었음을 알려준다.
                diaryAdapter.submitList(diariesList)
                Log.d("CALENDER++", "AFTER adopter")
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

    // Custom decorator 구현 - 일정 있는 날짜
    class DotDecorator(
        private val context: Context,
        private val colorResId: Int,
        private val dates: List<CalendarDay>
    ) :
        com.prolificinteractive.materialcalendarview.DayViewDecorator {

        private val color: Int = ContextCompat.getColor(context, colorResId)
        override fun shouldDecorate(day: CalendarDay): Boolean {
            return dates.contains(day)
        }

        override fun decorate(view: com.prolificinteractive.materialcalendarview.DayViewFacade) {
            view.addSpan(DotSpan(5f, color) )// 점 추가
        }
    }


    // Custom decorator 구현 - 오늘 날짜
    class TodayDecorator(private val context: Context, private val colorResId: Int, private val dates: List<CalendarDay>) :
        com.prolificinteractive.materialcalendarview.DayViewDecorator {

        private val color: Int = ContextCompat.getColor(context, colorResId)
        override fun shouldDecorate(day: CalendarDay): Boolean {
            return dates.contains(day)
        }

        override fun decorate(view: com.prolificinteractive.materialcalendarview.DayViewFacade) {
            view.addSpan(DotSpan(5f, color) )// 점 추가
        }
    }


}