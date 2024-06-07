package fr.ro.recipemanager

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import java.io.ByteArrayOutputStream

class AddRecipeActivity : AppCompatActivity() {

    private lateinit var previewImage: ImageView
    private lateinit var titreInput: EditText
    private lateinit var descriptionInput: EditText
    private lateinit var ingredientInput: EditText
    private lateinit var etapesInput: EditText
    private lateinit var prepaSpinner: Spinner
    private lateinit var cuissonSpinner: Spinner
    private val PICK_IMAGE_REQUEST = 1
    private var imageUri: Uri? = null
    private lateinit var requestQueue: RequestQueue
    private var id_utilisateur: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_add_recipe)

        requestQueue = Volley.newRequestQueue(this)

        titreInput = findViewById(R.id.titre_input)
        descriptionInput = findViewById(R.id.description_input)
        ingredientInput = findViewById(R.id.ingredient_input)
        etapesInput = findViewById(R.id.etapes_input)
        prepaSpinner = findViewById(R.id.prepa_spinner)
        cuissonSpinner = findViewById(R.id.cuisson_spinner)
        val uploadButton: Button = findViewById(R.id.upload_button)
        previewImage = findViewById(R.id.preview_image)
        val confirmButton: Button = findViewById(R.id.confirm_button)

        uploadButton.setOnClickListener {
            openImageChooser()
        }
        confirmButton.setOnClickListener {
            addRecipe()
        }

        // Récupération de l'ID utilisateur depuis l'intent
        id_utilisateur = intent.getStringExtra("id_utilisateur")

        prepaSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }

        cuissonSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }
    }

    private fun openImageChooser() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/jpeg", "image/jpg", "image/png"))
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            imageUri = data.data
            previewImage.setImageURI(imageUri)
        }
    }

    private fun addRecipe() {
        val titre = titreInput.text.toString()
        val description = descriptionInput.text.toString()
        val ingredients = ingredientInput.text.toString()
        val etapesPreparation = etapesInput.text.toString()
        val tempsPreparation = prepaSpinner.selectedItem.toString()
        val tempsCuisson = cuissonSpinner.selectedItem.toString()
        val idAuteur = id_utilisateur?.toInt() ?: 0

        // Vérification des champs vides
        if (titre.isEmpty() || description.isEmpty() || ingredients.isEmpty() || etapesPreparation.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show()
            return
        }

        // Convertir l'image en base64
        val imageBase64 = if (imageUri != null) {
            val inputStream = contentResolver.openInputStream(imageUri!!)
            val byteArray = inputStream?.readBytes()
            if (byteArray != null) {
                android.util.Base64.encodeToString(byteArray, android.util.Base64.DEFAULT)
            } else {
                ""
            }
        } else {
            "" // Utiliser une chaîne vide si aucune image n'est fournie
        }

        val recipeData = JSONObject()
        recipeData.put("titre", titre)
        recipeData.put("description", description)
        recipeData.put("ingredients", ingredients)
        recipeData.put("etapes_preparation", etapesPreparation)
        recipeData.put("temps_preparation", tempsPreparation)
        recipeData.put("temps_cuisson", tempsCuisson)
        recipeData.put("image", imageBase64)
        recipeData.put("id_auteur", idAuteur)

        val url = "http://192.168.56.1:8080/actions/addRecipe.php"
        val jsonObjectRequest = JsonObjectRequest(Request.Method.POST, url, recipeData,
            Response.Listener { response ->
                Toast.makeText(this, "Recette ajoutée avec succès!", Toast.LENGTH_SHORT).show()
            },
            Response.ErrorListener { error ->
                Toast.makeText(this, "Erreur: ${error.message}", Toast.LENGTH_SHORT).show()
            })

        requestQueue.add(jsonObjectRequest)
    }
}
