package pt.isec.tp_1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import pt.isec.tp_1.databinding.ActivityMainBinding


//import pt.isec.tp_1.myActivity.databinding.R
//import pt.isec.tp_1.myActivity.databinding.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.menuButtonSingleplayer.setOnClickListener {
            val intent =Intent(this, Singleplayer::class.java)
            startActivity(intent)
        }
        binding.menuButtonMultiplayer.setOnClickListener {
            val intent = Intent(this, Credits::class.java)
            startActivity(intent)
        }
        binding.menuButtonHowToPlay.setOnClickListener {
            val intent = Intent(this, HowToPlay::class.java)
            startActivity(intent)
        }
        binding.menuButtonTopscore.setOnClickListener {
            val intent = Intent(this, TopScore::class.java)
            startActivity(intent)
        }
        binding.menuButtonSettings.setOnClickListener {
            val intent = Intent(this, EditProfile::class.java)
            startActivity(intent)
        }



    }
}