package pt.isec.tp_1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import pt.isec.tp_1.databinding.ActivityHowToPlayBinding


//import pt.isec.tp_1.myActivity.databinding.R

class HowToPlay : AppCompatActivity() {
    lateinit var binding : ActivityHowToPlayBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHowToPlayBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}