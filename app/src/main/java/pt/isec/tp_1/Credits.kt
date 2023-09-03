package pt.isec.tp_1

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import pt.isec.tp_1.databinding.ActivityCreditsBinding

class Credits : AppCompatActivity() {
    lateinit var binding: ActivityCreditsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreditsBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}
