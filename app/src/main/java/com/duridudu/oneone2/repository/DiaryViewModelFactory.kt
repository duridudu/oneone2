import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.duridudu.oneone2.repository.DiaryRepository
import com.duridudu.oneone2.viewmodel.DiaryViewModel

class DiaryViewModelFactory(private val repository: DiaryRepository) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DiaryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DiaryViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }


}
