package pt.isec.tp_1

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.graphics.*
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Source
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex

//import pt.isec.tp_1.myActivity.databinding.R
//import pt.isec.tp_1.myActivity.databinding.databinding.ActivityMainBinding
//import pt.isec.tp_1.myActivity.databinding.databinding.ActivitySingleplayerBinding
import pt.isec.tp_1.databinding.ActivitySingleplayerBinding
import java.io.Serializable

//to store data
data class ActivityState(
    var tab : Tab,     //this will store the currentTab
    var currentTab : Int,   //the number of the currentTab in a Level
    var currentLevel : Int,
    var currentTime : Int,  //just the current time we have left
    var points : Int,
    var gameState: GameState,
    var totalTime : Int,
    var listTopScore : MutableList<Int>,
    var listTopTimes : MutableList<Int>,
    var correctAnswears : Int

) : Serializable

enum class GameState {
    PLAYING, PAUSE
}

const val TAG_DAREA = "DrawingAreaEvent"

class GestureDetection @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
) : View(context,attrs), GestureDetector.OnGestureListener {
    lateinit var binding : ActivitySingleplayerBinding

    constructor(context: Context, backColor : Int, binding1: ActivitySingleplayerBinding, selectedTV1 : MutableList<Any>) : this(context) {
        setBackgroundColor(backColor)
        binding = binding1
        selectedTV = selectedTV1
    }
    var selectedTV = mutableListOf<Any>()



    override fun onDraw(canvas: Canvas?) {
        Log.i("Ondraw", "drwaing")

        super.onDraw(canvas)
    }


    /* Gesture Detector */

    private val gestureDetector : GestureDetector by lazy {
        GestureDetector(context, this)
    }

    //override fun onTouchEvent(event: MotionEvent?): Boolean {
    //    if (gestureDetector.onTouchEvent(event!!))
    //        return true
    //    return super.onTouchEvent(event)
    //}

    override fun performClick(): Boolean {
        return super.performClick()
    }



    override fun onDown(e: MotionEvent): Boolean {
        Log.i(TAG_DAREA, "onDown: ")
        return true
    }

    override fun onShowPress(e: MotionEvent) {
        Log.i(TAG_DAREA, "onShowPress: ")
    }

    override fun onSingleTapUp(e: MotionEvent): Boolean {
        Log.i(TAG_DAREA, "onSingleTapUp: ")
        return false
    }
    override fun onLongPress(e: MotionEvent) {
        Log.i(TAG_DAREA, "onLongPress: ")
    }

    override fun onScroll(
        e1: MotionEvent,
        e2: MotionEvent,
        distanceX: Float,
        distanceY: Float
    ): Boolean {
        Log.i(TAG_DAREA, "onScroll: ")
        return false
    }

    override fun onFling(
        e1: MotionEvent,
        e2: MotionEvent,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        Log.i(TAG_DAREA, "onFling: ")
        return true
    }


    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val action = event.actionMasked
        if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_MOVE) {
            val x = event.x
            val y = event.y
            val textViews = listOf(binding.tv1,binding.tv2,binding.tv3,binding.tv4,binding.tv5,
                binding.tv6,binding.tv7,binding.tv8,binding.tv9,binding.tv10,
                binding.tv11,binding.tv12,binding.tv13,binding.tv14,binding.tv15,
                binding.tv16,binding.tv17,binding.tv18,binding.tv19,binding.tv20,
                binding.tv21,binding.tv22,binding.tv23,binding.tv24,binding.tv25)

            for (textView in textViews) {
                val parent = textView.parent as LinearLayout
                val left = textView.x + textView.translationX + parent.left
                val top = textView.y + textView.translationY + parent.top
                val right = left + textView.width
                val bottom = top + textView.height
                if (x >= left && x <= right && y >= top && y <= bottom) {
                    // Retrieve the text of the text view
                    val text = textView.text.toString()
                    if (text.isNotEmpty()) {
                        val value: Any = when {
                            text.toIntOrNull() != null -> text.toInt()
                            text.length == 1 -> text.toCharArray()[0]
                            else -> throw IllegalArgumentException("Invalid input")
                        }
                        // Add the value to the end of the selectedTV list
                        selectedTV.add(value)
                    }
                    Log.d("MyCustomView", "Text: $text")
                }
            }
        }
        return true
    }

}


//lateinit var detector : GestureDetection


class Singleplayer : AppCompatActivity(){
    var selectedTV = mutableListOf<Any>()
    private lateinit var binding: ActivitySingleplayerBinding
    lateinit var gestureArea: GestureDetection
    private lateinit var tab : Tab
    private var currentTab : Int = 1
    private var currentLevel : Int = 1
    private var currentTime : Int = 0
    private var points : Int = 0
    private var gameState : GameState = GameState.PLAYING
    private var totalTime : Int = 0
    private var listTopScores = mutableListOf<Int>()
    private var listTopTimes = mutableListOf<Int>()
    private var listenerRegistration1 : ListenerRegistration? = null
    private var listenerRegistration2 : ListenerRegistration? = null
    private var correctAnswears : Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //addInitialDataToFirestore()
        binding = ActivitySingleplayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        tab = Tab(currentLevel)
        startBoard(binding,tab ,currentTab, currentLevel, currentTime)
        gestureArea = GestureDetection(this,Color.RED,binding, selectedTV)
        timer(Levels.getTimeBoard(currentLevel)!!,binding,TimerAction.START)

        savedInstanceState?.let {
            val state = it.getSerializable("activity_state") as ActivityState
            // restore activity state using the values in the ActivityState object
            tab = state.tab
            currentTab = state.currentTab
            currentLevel = state.currentLevel
            currentTime = state.currentTime
            points = state.points
            gameState = state.gameState
            totalTime = state.totalTime
            listTopScores = state.listTopScore
            listTopTimes = state.listTopTimes
            correctAnswears = state.correctAnswears

            startBoardAfterRotate(binding,tab ,currentTab, currentLevel, currentTime, points)
            timer(currentTime,binding,TimerAction.START)
        }

    }


    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if(gameState == GameState.PLAYING){
            binding.gridLayout.setOnTouchListener{view,motionEvent ->
                gestureArea.onTouchEvent(motionEvent)
            }
            showSelectedTV()
        }
        return super.onTouchEvent(event)
    }

    /**
     * This function is to pick up the selectedTextViews and send it to the Tab to check if its
     * a valid expression and if so, to return the result. Before sending it, it makes sure
     * that is sending in the proper form so it doenst break.
     */
    fun showSelectedTV(){
        //selectedTV.forEach {
        //        selected->Log.i("test","selecionado $selected")
        //}

        if(selectedTV.size >= 5){
            val newList = mutableListOf<Any>()
            var previousElement: Any? = null
            selectedTV.forEach { element ->
                if (element != previousElement) {
                    newList.add(element)
                }
                previousElement = element
            }
            if(newList.size == 5){
                try{
                    var result : Double? = tab.validExpressions(newList.toMutableList())
                    checkIfBestScore(result)
                    selectedTV.clear()
                }
                catch (e : Exception){
                    Log.i(TAG_DAREA, "exception $e")
                }
            }
            selectedTV.clear()
        }
    }

    /**
     * Check if the selected expression has the highest result
     */
    fun checkIfBestScore(resultToCompare : Double?){

        if(resultToCompare == (tab.getValidExpressionsMap()).maxBy { it.value }.value){
            points += 2
            correctAnswears++
            currentTab++
            val toast = Toast.makeText(applicationContext,getString(R.string.game_correct_ans), Toast.LENGTH_SHORT).show()
            synchronized(mutex){
                totalTime += (Levels.getTimeBoard(currentLevel)!! - currentTime)
            }
            timer(currentTime,binding,TimerAction.ADD)
            passToNextBoard(binding)
        }
        else{
            //we need to see if its the second highest value... if not then it was an invalid move

            val sortedList = tab.getValidExpressionsMap().toList().sortedByDescending { (_,value)->value }
            if(resultToCompare == (sortedList.getOrNull(2)?.second)){
                points += 1
                passToNextBoard(binding)
                val toast = Toast.makeText(applicationContext, getString(R.string.game_second_best_ans), Toast.LENGTH_SHORT).show()
            }

            else{
                passToNextBoard(binding)
                //val toast = Toast.makeText(applicationContext, getString(R.string.game_wrong_ans), Toast.LENGTH_SHORT).show()
            }
        }
        binding.score.text = points.toString()
    }

    /**
     * This method is to put the values of the board in the textviews
     */
    fun sendTabToUi(binding1: ActivitySingleplayerBinding){
        binding = binding1
        //val binding = ActivitySingleplayerBinding.bind(findViewById(R.id.constraintLayout))
        var textViews = listOf(binding.tv1,binding.tv2,binding.tv3,binding.tv4,binding.tv5,
            binding.tv6,binding.tv7,binding.tv8,binding.tv9,binding.tv10,
            binding.tv11,binding.tv12,binding.tv13,binding.tv14,binding.tv15,
            binding.tv16,binding.tv17,binding.tv18,binding.tv19,binding.tv20,
            binding.tv21,binding.tv22,binding.tv23,binding.tv24,binding.tv25)
        var index : Int = 0
        //textViews.iterator().forEach {
        //    //it.text = tab[0][0].toString()
        //}
        for(i in 0 ..4){
            for(j in 0 .. 4){
                //textViews.forEach {
                //    it.setText("5")
                //}
                //var caracter : String = tab[i][j] as String
                textViews[index].text = tab.getTab()[i][j].toString()
                //textViews[index].setText(caracter)
                index++
            }
        }
    }



    enum class TimerAction {
        START, STOP, RESET, PAUSE, RESUME, ADD
    }

    var timerJob : Job? = null
    var timerPaused = false
    val mutex = Mutex()

    /**
     * This function is intend to create a coroutine that will have a timer
     */
    fun timer(timeInCurrentLevel: Int, binding: ActivitySingleplayerBinding, action: TimerAction){
        when(action){
            TimerAction.START -> {
                timerJob?.cancel()
                timerPaused = false
                timerJob = GlobalScope.launch {
                    var currentTime1 = timeInCurrentLevel
                    while (currentTime1 >= 0) {
                        if(!timerPaused){
                            this@Singleplayer.runOnUiThread {
                                binding.timeLeft.text = currentTime1.toString()
                            }
                            delay(1000)
                            currentTime1--
                            synchronized(mutex) {
                                // critical section
                                currentTime = currentTime1
                            }
                        }
                    }
                    observerPause()
                }
            }
            TimerAction.STOP -> {
                timerJob?.cancel()
            }
            TimerAction.RESET -> {
                timerJob?.cancel()
                timer(timeInCurrentLevel, binding, TimerAction.START)
            }
            TimerAction.PAUSE -> {
                timerPaused = true
            }
            TimerAction.RESUME -> {
                timerPaused = false
            }
            TimerAction.ADD -> {
                synchronized(mutex) {
                    // critical section
                    if(currentTime + Levels.getBonusTIme(currentLevel)!! <= Levels.getTimeBoard(currentLevel)!!){
                        currentTime += Levels.getBonusTIme(currentLevel)!!
                        timer(currentTime, binding, TimerAction.START)
                    }
                    else{
                        currentTime = Levels.getTimeBoard(currentLevel)!!
                        timer(currentTime, binding, TimerAction.START)
                    }
                }
            }
        }
    }

    /**
     * This function is used to performed the logic of passing for the next board and/or next level etc
     */
    fun passToNextBoard(binding: ActivitySingleplayerBinding){
        //check if its time to pass to the next level

        if(correctAnswears == Levels.getNumBoards(currentLevel)!! + 1){
            currentTab = 1

            correctAnswears = 1
            if(currentLevel < Levels.getNumLevels()){
                currentLevel += 1
                gameState = GameState.PAUSE
                synchronized(mutex){
                    totalTime += (Levels.getTimeBoard(currentLevel)!! - currentTime)
                    currentTime = 0
                }
                val toast = Toast.makeText(applicationContext, getString(R.string.game_pass_next_board), Toast.LENGTH_SHORT).show()
                timer(Levels.getTimeBoard(0)!!,binding,TimerAction.RESET)
            }
            else{
                synchronized(mutex){
                    totalTime += (Levels.getTimeBoard(currentLevel)!! - currentTime)
                }
                val toast = Toast.makeText(applicationContext, getString(R.string.game_game_over_win), Toast.LENGTH_SHORT).show()
                addDataToFirestore()
            }
        }
        else{
            startBoard(binding,tab ,currentTab, currentLevel, currentTime)
            //val toast = Toast.makeText(applicationContext,getString(R.string.game_game_over_lost), Toast.LENGTH_SHORT).show()
            //synchronized(mutex){
            //    totalTime += (Levels.getTimeBoard(currentLevel)!! - currentTime)
            //}
            //addDataToFirestore()
        }

        //else{
        //    //pass to the next board in the same level
        //    startBoard(binding,tab ,currentTab, currentLevel, currentTime)
        //}
    }


    fun observerPause(){
        if(gameState == GameState.PAUSE){
            timer(Levels.getTimeBoard(currentLevel)!!,binding,TimerAction.RESET)
            startBoard(binding,tab ,currentTab, currentLevel, currentTime)
            gameState = GameState.PLAYING
        }
        else{
            timer(Levels.getTimeBoard(currentLevel)!!,binding,TimerAction.STOP)
            runOnUiThread(){
                val toast = Toast.makeText(applicationContext,getString(R.string.game_game_over_lost), Toast.LENGTH_SHORT).show()
                synchronized(mutex){
                    totalTime += (Levels.getTimeBoard(currentLevel)!! - currentTime)
                }
                addDataToFirestore()
            }
        }
    }



    /**
     * This will be the function responsible for the logic of passing to a new board,
     * pass to a new level, etc
     */
    fun startBoard(
        binding: ActivitySingleplayerBinding,
        tab: Tab,
        currentTab: Int,
        currentLevel: Int,
        currentTime: Int
    ) {

        this.tab = Tab(currentLevel)
        this.tab.populateTab()
        sendTabToUi(binding)
        binding.nivelAtual.text = currentTab.toString() + "/" + Levels.getNumBoards(currentLevel).toString()
        binding.nivel?.text = currentLevel.toString()

        //resetTimer(Levels.getTimeBoard(currentLevel)!!, binding)
        //data = Data(tab)
    }

    fun startBoardAfterRotate(
        binding: ActivitySingleplayerBinding,
        tab: Tab,
        currentTab: Int,
        currentLevel: Int,
        currentTime: Int,
        points : Int
    ) {
        this.tab = tab
        sendTabToUi(binding)
        binding.nivelAtual.text = currentTab.toString() + "/" + Levels.getNumBoards(currentLevel).toString()
        binding.nivel?.text = currentLevel.toString()
        binding.score?.text = points.toString()
        //resetTimer(Levels.getTimeBoard(currentLevel)!!, binding)
        //data = Data(tab)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        val state = ActivityState(tab, currentTab, currentLevel, currentTime,
            points, gameState, totalTime, listTopScores,listTopTimes, correctAnswears)
        outState.putSerializable("activity_state", state)
    }

    override fun onBackPressed() {
        //this will only appear IF the game is PAUSED
        if(gameState == GameState.PAUSE){
            val builder = AlertDialog.Builder(this)
            builder.setMessage(getString(R.string.game_confirm_leave))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.game_yes_opt)) { _, _ -> killactivity() }
                .setNegativeButton(getString(R.string.game_no_opt)) { dialog, _ -> dialog.cancel() }
            val alert = builder.create()
            alert.show()
        }
        else{
            killactivity()
        }
    }

    fun killactivity(){
        timer(Levels.getTimeBoard(currentLevel)!!,binding,TimerAction.STOP)
        stopObserver()
        finish()
    }

    override fun onPause(){
        super.onPause()
        stopObserver()
    }
    fun stopObserver(){
        listenerRegistration1?.remove()
        listenerRegistration2?.remove()
    }

    //FIREBASE STUFF
    /**
     * This function is to set in the beginning the default values
     */
    fun addInitialDataToFirestore() {
        val db = Firebase.firestore
        val scores = hashMapOf(
            "nrgames" to 0,
            "topscore" to 0
        )
        val times = hashMapOf(
            "nrgames" to 0,
            "toptimes" to 0
        )
        //TOP SCORES
        db.collection("Scores").document("Top1").set(scores)
            .addOnSuccessListener {
                Log.i(TAG, "addDataToFirestore: Success")
            }.
            addOnFailureListener { e->
                Log.i(TAG, "addDataToFirestore: ${e.message}")
            }
        db.collection("Scores").document("Top2").set(scores)
            .addOnSuccessListener {
                Log.i(TAG, "addDataToFirestore: Success")
            }.
            addOnFailureListener { e->
                Log.i(TAG, "addDataToFirestore: ${e.message}")
            }
        db.collection("Scores").document("Top3").set(scores)
            .addOnSuccessListener {
                Log.i(TAG, "addDataToFirestore: Success")
            }.
            addOnFailureListener { e->
                Log.i(TAG, "addDataToFirestore: ${e.message}")
            }
        db.collection("Scores").document("Top4").set(scores)
            .addOnSuccessListener {
                Log.i(TAG, "addDataToFirestore: Success")
            }.
            addOnFailureListener { e->
                Log.i(TAG, "addDataToFirestore: ${e.message}")
            }
        db.collection("Scores").document("Top5").set(scores)
            .addOnSuccessListener {
                Log.i(TAG, "addDataToFirestore: Success")
            }.
            addOnFailureListener { e->
                Log.i(TAG, "addDataToFirestore: ${e.message}")
            }
        //TOP TIME
        db.collection("Times").document("Top1").set(times)
            .addOnSuccessListener {
                Log.i(TAG, "addDataToFirestore: Success")
            }.
            addOnFailureListener { e->
                Log.i(TAG, "addDataToFirestore: ${e.message}")
            }
        db.collection("Times").document("Top2").set(times)
            .addOnSuccessListener {
                Log.i(TAG, "addDataToFirestore: Success")
            }.
            addOnFailureListener { e->
                Log.i(TAG, "addDataToFirestore: ${e.message}")
            }
        db.collection("Times").document("Top3").set(times)
            .addOnSuccessListener {
                Log.i(TAG, "addDataToFirestore: Success")
            }.
            addOnFailureListener { e->
                Log.i(TAG, "addDataToFirestore: ${e.message}")
            }
        db.collection("Times").document("Top4").set(times)
            .addOnSuccessListener {
                Log.i(TAG, "addDataToFirestore: Success")
            }.
            addOnFailureListener { e->
                Log.i(TAG, "addDataToFirestore: ${e.message}")
            }
        db.collection("Times").document("Top5").set(times)
            .addOnSuccessListener {
                Log.i(TAG, "addDataToFirestore: Success")
            }.
            addOnFailureListener { e->
                Log.i(TAG, "addDataToFirestore: ${e.message}")
            }
    }

    fun addDataToFirestore() {
        try{
            startObserver { topScores, topTimes ->
                // This code block will be executed when the scores and times have been retrieved
                updateDataFirebase(topScores, topTimes)
                onBackPressed()
            }
        }
        catch (e : Exception){
            Log.i(TAG, "exception: $e")
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

    fun updateDataFirebase(topScores: List<Int>, topTimes: List<Int>) {
        if(topScores.size < 5 || topTimes.size < 5)
            return

        val db = Firebase.firestore
        var newTopScores : MutableList<Int> = topScores as MutableList<Int>
        var newTopTimes : MutableList<Int> = topTimes as MutableList<Int>

        //adds the new values
        newTopScores.add(points)
        synchronized(mutex){
            newTopTimes.add(totalTime)
        }

        //order all of them
        newTopScores.sortDescending()
        newTopTimes.sort()
        //takes only the 5 first
        val tempFirstFiveScores = newTopScores.take(5)
        val tempFirstFiveTimes = newTopTimes.take(5)
        //clears the lists
        newTopScores.clear()
        newTopTimes.clear()
        //puts the correct values
        newTopScores = tempFirstFiveScores as MutableList<Int>
        newTopTimes = tempFirstFiveTimes as MutableList<Int>
        //now its time to update the database
        val documents = listOf("Top1", "Top2", "Top3", "Top4", "Top5")

        for ((index, score) in newTopScores.withIndex()) {
            val data = hashMapOf(
                "nrgames" to 0,
                "topscore" to score
            )
            val document = db.collection("Scores").document(documents[index])
            document.set(data)
                .addOnSuccessListener {
                    Log.i(TAG, "Data updated successfully")
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Error updating data: ${e.message}")
                }
        }

        for ((index, time) in newTopTimes.withIndex()) {
            val data = hashMapOf(
                "nrgames" to 0,
                "toptimes" to time
            )
            val document = db.collection("Times").document(documents[index])
            document.set(data)
                .addOnSuccessListener {
                    Log.i(TAG, "Data updated successfully")
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Error updating data: ${e.message}")
                }
        }
    }

}

