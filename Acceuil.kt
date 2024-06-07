package fr.ro.recipemanager

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class Acceuil : AppCompatActivity() {

    private lateinit var welcomeTextView: TextView
    private lateinit var logoutButton: ImageButton
    private var prenom: String? = null
    private var id_utilisateur: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_acceuil)

        welcomeTextView = findViewById(R.id.WelcomeTextView)
        logoutButton = findViewById(R.id.logoutBtn)

        prenom = intent.getStringExtra("prenom")
        id_utilisateur = intent.getStringExtra("id_utilisateur")

        welcomeTextView.text = "Bienvenue: $prenom"

        logoutButton.setOnClickListener {
            clearUserSession()

            val intent = Intent(this@Acceuil, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, RecipesFragment())
                .commit()
        }
    }

    private fun clearUserSession() {
        val sharedPreferences: SharedPreferences =
            getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
    }
}
