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

class CreateAccountActivity : AppCompatActivity() {

    //private lateinit var errorCreateAccountTextView: TextView
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var nameEditText: EditText
    private lateinit var lastnameEditText: EditText
    private lateinit var createAccountBtn: Button
    private lateinit var alreadyHasAccountTextView: TextView
    private var databaseManager: DatabaseManager? = null

    private var email: String = ""
    private var mot_de_passe: String = ""
    private var nom: String = ""
    private var prenom: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)

        //errorCreateAccountTextView = findViewById(R.id.errorCreateAccountTextView)
        emailEditText = findViewById(R.id.createEmailEditText)
        passwordEditText = findViewById(R.id.createPasswordEditText)
        nameEditText = findViewById(R.id.createNameEditText)
        lastnameEditText = findViewById(R.id.createLastNameEditText)
        createAccountBtn = findViewById(R.id.createAccountBtn)
        alreadyHasAccountTextView = findViewById(R.id.alreadyHasAccountBtn)

        // Ajoutez les écouteurs d'événements pour les boutons ici
        createAccountBtn.setOnClickListener { view ->
            // Traitement du clic sur le bouton "createAccountBtn"
            email = emailEditText.text.toString()
            mot_de_passe = passwordEditText.text.toString()
            nom = nameEditText.text.toString()
            prenom = lastnameEditText.text.toString()

            createAccount()


        }

        // Redirection vers l'activité de connexion
        alreadyHasAccountTextView.setOnClickListener { view ->
            // Traitement du clic sur le bouton "alreadyHasAccountTextView"
            val mainActivity = Intent(this, MainActivity::class.java)
            startActivity(mainActivity)
        }
    }

    private fun onApiResponse(response: JSONObject) {
        try {
            val success = response.getBoolean("success")
            if (success) {
                val id_utilisateur = response.getString("id_utilisateur") // Récupérez l'ID_UTILISATEUR depuis la réponse JSON
                val prenom = response.getString("prenom")

                val intent = Intent(this@CreateAccountActivity, Acceuil::class.java)
                intent.putExtra("id_utilisateur", id_utilisateur) // Ajoutez l'ID_UTILISATEUR à l'intent
                intent.putExtra("email", email)
                intent.putExtra("nom", nom)
                intent.putExtra("prenom", prenom)
                startActivity(intent)
                finish()
            } else {
                val error = response.getString("error")
                Toast.makeText(this@CreateAccountActivity, error, Toast.LENGTH_SHORT).show()
            }
        } catch (e: JSONException) {
            e.printStackTrace()
            Toast.makeText(this@CreateAccountActivity, e.toString(), Toast.LENGTH_LONG).show()
        }
    }

    fun createAccount() {
        val url = "http://192.168.56.1:8080/actions/createAccount.php"

        val params = HashMap<String, String>()
        params["email"] = email
        params["mot_de_passe"] = mot_de_passe
        params["nom"] = nom
        params["prenom"] = prenom

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.POST, url, JSONObject(params as Map<*, *>?),
            { response ->
                onApiResponse(response)
                Toast.makeText(this@CreateAccountActivity, "connexion réussie", Toast.LENGTH_SHORT).show()
            },
            { error ->
                val errorMessage = error?.message ?: "Erreur inconnue lors de la connexion"
                Toast.makeText(this@CreateAccountActivity, "Erreur lors de la connexion: $errorMessage", Toast.LENGTH_SHORT).show()
            }
        )
        databaseManager!!.queue.add(jsonObjectRequest)
    }



}
