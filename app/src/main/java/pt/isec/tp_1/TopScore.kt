package pt.isec.tp_1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.sync.Mutex
import pt.isec.tp_1.databinding.ActivityTopScoreBinding

const val TAG = "TopScoreEvent"


class TopScore : AppCompatActivity() {
    private lateinit var binding : ActivityTopScoreBinding
    private var listenerRegistration1 : ListenerRegistration? = null
    private var listenerRegistration2 : ListenerRegistration? = null
    private var listTopScores = mutableListOf<Int>()
    private var listTopTimes = mutableListOf<Int>()
    val mutex = Mutex()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTopScoreBinding.inflate(layoutInflater)
        setContentView(binding.root)
        startObserver { topScores, topTimes ->
            // This code block will be executed when the scores and times have been retrieved
            writeOnUI(topScores, topTimes)
        }
    }


    fun startObserver(callback: (List<Int>, List<Int>) -> Unit) {
        val db = Firebase.firestore
        val scoresQuery = db.collectionGroup("Scores")
        listenerRegistration1 = scoresQuery.addSnapshotListener { querySnapshot, e ->
            if (e != null) {
                return@addSnapshotListener
            }
            if (querySnapshot != null) {
                listTopScores.clear()
                for (docSS in querySnapshot) {
                    if (docSS.exists()) {
                        val nrgames = docSS.getLong("nrgames")
                        val topscore = docSS.getLong("topscore")
                        //Log.i(TAG, "nrGames $nrgames : nrScore $topscore")
                        listTopScores.add(topscore!!.toInt())

                    }
                }
                //callback(listTopScores, listTopTimes)
            }
        }
        val timesQuery = db.collectionGroup("Times")
        listenerRegistration2 = timesQuery.addSnapshotListener { querySnapshot, e ->
            if (e != null) {
                return@addSnapshotListener
            }
            if (querySnapshot != null) {
                listTopTimes.clear()
                for (docSS in querySnapshot) {
                    if (docSS.exists()) {
                        val nrgames = docSS.getLong("nrgames")
                        val time = docSS.getLong("toptimes")
                        listTopTimes.add(time!!.toInt())
                        //Log.i(TAG, "nrGames $nrgames : nrTime $time")
                    }
                }
                callback(listTopScores, listTopTimes)
            }
        }
    }

    /**
     * This function will retrieve all the data stored in the list, will ordenate it and
     * write in the correct textviews the values of the TOP 5
     */
    fun writeOnUI(topScores: List<Int>, topTimes: List<Int>) {
        if(topScores.size < 5 || topTimes.size < 5)
            return
        var newTopScores : MutableList<Int> = topScores as MutableList<Int>
        var newTopTimes : MutableList<Int> = topTimes as MutableList<Int>

        newTopScores.sortDescending()
        newTopTimes.sort()

        val textViewsSore = listOf(binding.scoreFirst,binding.scoreSecond,binding.scoreThird, binding.scoreFourth,binding.scoreFifth)
        val textViewsTime = listOf(binding.timeFirst,binding.timeSecond,binding.timeThird,binding.timeFourth,binding.timeFifth)
        var index : Int = 0


        for(textview in textViewsSore){
            textview.text = newTopScores[index].toString()
            index++
        }
        index = 0
        for(textview in textViewsTime){
            textview.text = newTopTimes[index].toString()
            index++
        }

    }

    override fun onPause(){
        super.onPause()
        stopObserver()
    }
    fun stopObserver(){
        listenerRegistration1?.remove()
        listenerRegistration2?.remove()
    }
}