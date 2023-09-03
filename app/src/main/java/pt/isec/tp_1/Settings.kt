package pt.isec.tp_1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import pt.isec.tp_1.databinding.ActivitySettingsBinding

class Settings : AppCompatActivity() {
    lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}