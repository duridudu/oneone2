package com.duridudu.oneone2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import com.duridudu.oneone2.databinding.ActivityLoginBinding
import com.duridudu.oneone2.model.User
import com.duridudu.oneone2.viewmodel.UserViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import io.github.muddz.styleabletoast.StyleableToast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var userViewModel: UserViewModel
    private val googleSignInLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            result ->
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            Log.d("LOGIN--", task.toString())
            try {
                // Google 로그인이 성공하면, Firebase로 인증합니다.
                val account = task.getResult(ApiException::class.java)!!
                Log.d("LOGIN--22", account.idToken!!)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google 로그인 실패
                Toast.makeText(this, "Google 로그인에 실패했습니다.", Toast.LENGTH_SHORT).show()
            }

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Log.d("USER0+++", "TEST")
        // ViewModelProvider를 사용하여 ViewModel 초기화
       userViewModel  = ViewModelProvider(this)[UserViewModel::class.java]
        // Firebase Authentication 인스턴스 초기화
        firebaseAuth = FirebaseAuth.getInstance()

        // 이미 로그인되어 있는지 확인
        if (firebaseAuth.currentUser != null) {
            val user = firebaseAuth.currentUser
            // 사용자 정보를 가져와서 ViewModel을 통해 저장
            val name = user?.displayName ?: ""
            val email = user?.email ?: ""
            val photoUrl = user?.photoUrl?.toString() ?: ""
            val uid = user?.uid ?: ""
            val nowUser = User(uid=uid, name = name, email = email, profileurl = photoUrl)


            Log.d("USER++",uid+"+++"+name)
            Log.d("USER++2222",nowUser.uid+"++++"+nowUser.name)
            CoroutineScope(Dispatchers.IO).launch {
                userViewModel.insert(nowUser)
            }


           //val nowUser2:User = userViewModel.getUser()
            //Toast.makeText(this, "환영합니다, ${nowUser2}!", Toast.LENGTH_SHORT).show()

//            userViewModel.insert(nowUser)
            // 이미 로그인된 경우 메인 화면으로 이동
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        // GoogleSignInOptions를 구성합니다.
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestServerAuthCode(getString(R.string.default_web_client_id))
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        // GoogleSignInClient를 초기화합니다.
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // 구글 로그인 버튼 클릭 이벤트 처리
        binding.btnGoogle.setOnClickListener {
            signIn()
        }
    }



    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        googleSignInLauncher.launch(signInIntent)
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        Log.d("LOGIN--3",idToken)
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // 로그인 성공
                    val user = firebaseAuth.currentUser
                    StyleableToast.makeText(applicationContext, "환영합니다, ${user?.displayName}! 🥰", R.style.myToast).show()
                    //Toast.makeText(this, "환영합니다, ${user?.displayName}!", Toast.LENGTH_SHORT).show()
                    // 여기서 로그인 후 화면 전환 등의 작업을 수행할 수 있습니다.
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    // 로그인 실패
                    Toast.makeText(this, "Firebase 인증에 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
            }
    }
}