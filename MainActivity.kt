package fr.ro.recipemanager

import DatabaseManager
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import org.json.JSONException
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    private lateinit var EmailEditText: EditText
    private lateinit var PasswordEditText: EditText
    private lateinit var connectBtn: Button
    private lateinit var createAccountBtn: TextView
    private var email: String? = null
    private var mot_de_passe: String? = null
    private var databaseManager: DatabaseManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        EmailEditText = findViewById(R.id.EmailEditText)
        PasswordEditText = findViewById(R.id.PasswordEditText)
        connectBtn = findViewById(R.id.connectBtn)
        createAccountBtn = findViewById(R.id.createAccountBtn)
        databaseManager = DatabaseManager(applicationContext)

        connectBtn.setOnClickListener {
            email = EmailEditText.text.toString()
            mot_de_passe = PasswordEditText.text.toString()
            connectUser(email!!, mot_de_passe!!)
        }

        createAccountBtn.setOnClickListener {
            val intent = Intent(this@MainActivity, CreateAccountActivity::class.java)
            startActivity(intent)
        }
    }

    private fun onApiResponse(response: JSONObject) {
        try {
            val success = response.getBoolean("success")
            if (success) {
                val user = response.getJSONObject("user")
                val id_utilisateur = user.getString("id_utilisateur") // Récupérez l'ID_UTILISATEUR depuis la réponse JSON
                val prenom = user.getString("prenom") // Récupérer le prénom de l'utilisateur depuis la réponse JSON




                val intent = Intent(this@MainActivity, Acceuil::class.java)
                intent.putExtra("id_utilisateur", id_utilisateur) // Ajoutez l'ID_UTILISATEUR à l'intent
                intent.putExtra("email", email)
                intent.putExtra("prenom", prenom) // Ajouter le prénom à l'intent
                startActivity(intent)
                finish()
            } else {
                val error = response.getString("error")
                Toast.makeText(this@MainActivity, error, Toast.LENGTH_SHORT).show()
            }
        } catch (e: JSONException) {
            e.printStackTrace()
            Toast.makeText(this@MainActivity, e.toString(), Toast.LENGTH_LONG).show()
        }
    }


    private fun connectUser(email: String, mot_de_passe: String) {
        val url = "http://192.168.56.1:8080/actions/connectAccount.php"
        val params = HashMap<String, String>()
        params["email"] = email
        params["mot_de_passe"] = mot_de_passe
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.POST, url, (params as Map<*,*>?)?.let { JSONObject(it) },
            { response ->
                onApiResponse(response)
                Toast.makeText(this@MainActivity, "connexion réussie", Toast.LENGTH_SHORT).show()
            },
            { error ->
                val errorMessage = error?.message ?: "Erreur inconnue lors de la connexion"
                Toast.makeText(this@MainActivity, "Erreur lors de la connexion: $errorMessage", Toast.LENGTH_SHORT).show()
            }
        )
        databaseManager!!.queue.add(jsonObjectRequest)
    }
}
