package com.duridudu.oneone2

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.duridudu.oneone2.adapter.DiaryAdapter
import com.duridudu.oneone2.databinding.FragmentWriteBinding
import com.duridudu.oneone2.model.Diary
import com.duridudu.oneone2.viewmodel.DiaryViewModel
import com.duridudu.oneone2.viewmodel.UserViewModel
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import io.github.muddz.styleabletoast.StyleableToast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
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

    private val calendar = Calendar.getInstance()

    // 작성된 글일 경우 불러오기 위한 뷰모델
    private lateinit var viewModel: DiaryViewModel
    private lateinit var userViewModel:UserViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentWriteBinding.inflate(inflater)
        initTextView()
        // 날짜 선택
        binding.writeDate.setOnClickListener {
            showDatePicker()
        }

        // 시간 선택
        binding.writeTime.setOnClickListener {
            showTimePicker()
        }
        return binding.root
    }

    private fun showTimePicker() {

        // TimePickerDialog
        val timePickerDialog = TimePickerDialog(
            requireContext(),
            { _, hourOfDay, minute ->
                // 사용자가 시간을 선택하면 호출됨
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)

                // 선택된 날짜와 시간을 TextView에 설정
                updateTimeTextView()
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true // 24-hour format
        )
        timePickerDialog.show()
    }



    private fun showDatePicker() {
        // DatePickerDialog
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                // 사용자가 날짜를 선택하면 호출됨
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateDateTextView()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initTextView() {
        val currentDateTime = LocalDateTime.now()

        // 원하는 형식으로 포맷 지정
        // 원하는 형식으로 포맷 지정
        val dateFormat = DateTimeFormatter.ofPattern("yyyy.MM.dd (E)")
        val dateString = currentDateTime.format(dateFormat)


        val timeFormat = DateTimeFormatter.ofPattern("a HH시 mm분")
        val timeString = currentDateTime.format(timeFormat)
        binding.writeDate.text = dateString
        binding.writeTime.text = timeString
    }

    private fun updateDateTextView() {
        val dateFormat = SimpleDateFormat("yyyy.MM.dd (E)", Locale.getDefault())
        val dateString = dateFormat.format(calendar.time)


        binding.writeDate.text = dateString

    }

    private fun updateTimeTextView() {
        val timeFormat = SimpleDateFormat("a HH시 mm분", Locale.getDefault())
        val timeString = timeFormat.format(calendar.time)
        binding.writeTime.text = timeString
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(DiaryViewModel::class.java)
        userViewModel = ViewModelProvider(requireActivity()).get(UserViewModel::class.java)
        // ViewModel에서 선택된 Diary 가져오기
        var selectedDiary = viewModel.getSelectedDiary()

        // 수정으로 넘어온 경우!! 하 수정버튼을 만드는게 낫겠음.. 쩝
        if (selectedDiary != null) {
            Log.d("WRITE++", "수정!!!!!!!!")
            // 일기 영역 보이게
            binding.btnWrite.visibility = View.INVISIBLE
            binding.writeContent.visibility = View.VISIBLE


            var writeDate = changeTimestampToDate(selectedDiary.timestamp)
            var writeTime  = changeTimestampToTime(selectedDiary.timestamp)

            // 선택된 Diary 데이터를 UI에 설정하는 코드
            binding.title.setText(selectedDiary.title)
            binding.writeContent.setText(selectedDiary.content)
            Log.d("WRITE++", "수정$writeDate")
            Log.d("WRITE++", "수정$writeTime")

            binding.writeDate.text=writeDate
            binding.writeTime.text = writeTime
            // 업데이트를 위해 고유키 설정
            diaryId = selectedDiary.diaryId.toString()

            Log.d("WRITE++", "UPDATE : $diaryId")
            CoroutineScope(Dispatchers.Main).launch {
            // 수정 버전으로 파이어베이스 생성
            initFirebase("update", diaryId)}
        }
        else{
            Log.d("WRITE++", "CREATE")
            CoroutineScope(Dispatchers.Main).launch {
            // 신규 추가 버전으로 파이어베이스 생성
            initFirebase("create", null)}
        }
        
        // 사진 등록
        binding.btnPhoto.setOnClickListener {
            //binding.btnPhoto.visibility = View.INVISIBLE
            //사진 업로드 기능 아직 안됨 ㅜ.ㅜ
            StyleableToast.makeText(requireContext(), "사진 업로드 기능을 준비중이에요 🥲", R.style.myToast).show()

        }
        // 일기 등록
        binding.btnWrite.setOnClickListener {
            binding.btnWrite.visibility = View.INVISIBLE
            binding.writeContent.visibility = View.VISIBLE
        }

        // 최종 등록 버튼
        binding.btnRegister.setOnClickListener {
            if ((binding.title.text.isEmpty()) or (binding.title.text.equals("일정을 입력하세요."))){
                Log.d("WRITE++","빈칸 토스트;;;;")
                StyleableToast.makeText(requireContext(), "제목을 입력해주세요!", R.style.myToast).show()
            }
            else{
//                Log.d("WRITE++","저장 토스트;;;;")
//                StyleableToast.makeText(requireContext(), "저장되었습니다.",R.style.myToast).show()
            }

            Log.d("WRITE++", "Register : $diaryId")
            Log.d("WRITE++", "DATE:"+binding.writeDate.text.toString())
            Log.d("WRITE++", "TIME:"+binding.writeTime.text)
            // 예시로 사용할 다이어리 데이터
            var timestamp = changeDateToTimestamp(binding.writeDate.text.toString(), binding.writeTime.text.toString()).toString()
            Log.d("WRITE++", timestamp)
            if (selectedDiary != null) {
                val diary = Diary(
                    diaryId = diaryId,
                    title =  binding.title.text.toString(),
                    content = binding.writeContent.text.toString(),
                    timestamp = timestamp,
                    photoUrl = photoUrl
                )
                // 해당 경로의 데이터를 업데이트
                diaryRef.setValue(diary)
                    .addOnSuccessListener {
                    // 성공적으로 업데이트된 경우 처리할 로직
                    Log.d("Firebase", "Diary updated successfully")
                        Log.d("WRITE++","저장 토스트;;;;")
                        StyleableToast.makeText(requireContext(), "저장되었습니다.",R.style.myToast).show()
                }
                    .addOnFailureListener { e ->
                        // 업데이트 실패 시 처리할 로직
                        Log.w("WRITE++", "Error updating diary", e)
                    }
            }
            else{
                val diary = Diary(
                    title =  binding.title.text.toString(),
                    content = binding.writeContent.text.toString(),
                    timestamp = timestamp,
                    photoUrl = photoUrl
                )
                // 새로운 다이어리 추가
                val newDiaryRef = diaryRef.push()
                newDiaryRef.setValue(diary)
                val diaryId = newDiaryRef.key // 생성된 고유 키 가져오기
                Log.d("WRITE++", "신규작성후$diaryId.toString()")
                // Diary 객체에 고유 키 설정
                newDiaryRef.child("diaryId").setValue(diaryId)
                    .addOnSuccessListener {
                        Log.d("WRITE++","저장 토스트;;;;")
                        StyleableToast.makeText(requireContext(), "저장되었습니다.",R.style.myToast).show()
                    }
                    .addOnFailureListener {e->
                        // 업데이트 실패 시 처리할 로직
                        Log.w("WRITE++", "Error updating diary", e)
                    }
            }

        }
    }

    private fun changeDateToTimestamp(writeDate: String, writeTime: String): String? {
        // 날짜와 시간을 추출하여 Calendar 객체에 설정
        val calendar = Calendar.getInstance()

        // 날짜 문자열 "2024.06.27 (목)"을 파싱하여 Calendar 객체에 설정
        val datePattern = "yyyy.MM.dd (E)"
        val dateFormat = SimpleDateFormat(datePattern, Locale.getDefault())
        val date = dateFormat.parse(writeDate)

        calendar.time = date

        // 시간 문자열 "오전 06시 18분"을 파싱하여 Calendar 객체에 시간 설정
        val timePattern = "a hh시 mm분"
        val timeFormat = SimpleDateFormat(timePattern, Locale.getDefault())
        val time = timeFormat.parse(writeTime)

        calendar.set(Calendar.HOUR_OF_DAY, time.hours)
        calendar.set(Calendar.MINUTE, time.minutes)

        // Calendar 객체를 "yyyy-MM-dd HH:mm" 형식의 문자열로 변환
        val targetPattern = "yyyy-MM-dd HH:mm"
        val targetFormat = SimpleDateFormat(targetPattern, Locale.getDefault())
        val formattedDateTime = targetFormat.format(calendar.time)
        return formattedDateTime
    }

    private fun changeTimestampToTime(timestamp: String?): String {
        Log.d("WRITE++", "함수 $timestamp")
        // 현재 문자열의 포맷 지정
        val originalFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val dateFormat = SimpleDateFormat("a hh시 mm분", Locale.getDefault())
        // 문자열을 Date 객체로 변환
        val date = originalFormat.parse(timestamp)

        // 새로운 포맷으로 변환
        val formattedDateTime = dateFormat.format(date)
        return formattedDateTime
    }

    private fun changeTimestampToDate(timestamp: String?): String {
        Log.d("WRITE++", "함수 $timestamp")
        // 현재 문자열의 포맷 지정
        val originalFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val dateFormat = SimpleDateFormat("yyyy.MM.dd (E)", Locale.getDefault())
        // 문자열을 Date 객체로 변환
        val date = originalFormat.parse(timestamp)

        // 새로운 포맷으로 변환
        val formattedDateTime = dateFormat.format(date)
        return formattedDateTime
    }

    private suspend fun initFirebase(s: String, key:String?) {
        var key = key
        // 코루틴 스코프 내에서 getUser() 호출
        //lifecycleScope.launch {
            val user = userViewModel.getUser()
            val userId = user.uid
            // userId를 사용하는 로직 추가
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
       // }//
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