package pt.isec.tp_1

import android.util.Log
import pt.isec.tp_1.databinding.ActivitySingleplayerBinding
import java.util.*
import kotlin.random.Random

//var currentLevelTest : Int = 2

/**
 * This class is to create a board, populate it and send to the UI
 */
class Tab (currentLevel1 : Int) : java.io.Serializable{


    //var tab : Array<Array<Any>> = arrayOf(arrayOf(0,0,0,0,0))

    private val currentLevel : Int = currentLevel1

    private var tab : Array<Array<Any>> = arrayOf(
        arrayOf(1,2,3,4,5),
        arrayOf(1,2,3,4,5),
        arrayOf(1,2,3,4,5),
        arrayOf(1,2,3,4,5),
        arrayOf(1,2,3,4,5)
    )

    private lateinit var validExpressionsMap : Map<MutableList<Any>,Double>


    private var caracteres : Array<Char>? = Levels.getOperators(currentLevel);

    /**
     * This method is to return the map of validExpressions, which contains a mutable list
     * with the valid expressions and a value with their result
     * @return Map<MutableList<Any>,Double>
     */
    fun getValidExpressionsMap() : Map<MutableList<Any>,Double>{
        return validExpressionsMap
    }

    public fun getTab() : Array<Array<Any>>{
        return tab;
    }

    /**
     * This method is to populate the tab with random data, according to the current level
     */
    fun populateTab() {
        for (i in 0..4) {
            for (j in 0..4) {
                if (i % 2 != 0) {
                    if (j % 2 == 0) //linha impar sem numeros
                        //tab[i][j] = caracteres?.get(((Math.random() * (caracteres!!.size-1)+1).toInt()))!!
                        tab[i][j] = caracteres!!.get(Random.nextInt(0, caracteres!!.size)).toChar()
                    else
                        tab[i][j] = ' '
                    continue
                }
                if (j % 2 == 0) {
                    // se posição par [0,2,4], coloca número
                    //tab[i][j] = (Math.random() * (Levels.getValues(currentLevelTest)!! - 1 + 1) + 1).toInt()
                    tab[i][j] = Random.nextInt(1,Levels.getValues(currentLevel)!!).toInt()
                } else
                    //tab[i][j] = caracteres?.get((Math.random() * (caracteres!!.size-1)+0).toInt())!!
                    tab[i][j] = caracteres!!.get(Random.nextInt(0, caracteres!!.size)).toChar()
            }
        }
        //populates the map
        //also we have to have the normal expression from right-left or up-down
        //and the left-right and down-up
        //according to the professor, regardless the direction of the movement, the result
        //will be always calculated from left-right and up-down
        validExpressionsMap = mapOf(
            //linhas
            mutableListOf(tab[0][0],tab[0][1],tab[0][2],tab[0][3],tab[0][4]) to calculaExpressao(mutableListOf(tab[0][0],tab[0][1],tab[0][2],tab[0][3],tab[0][4])),
            mutableListOf(tab[0][4],tab[0][3],tab[0][2],tab[0][1],tab[0][0]) to calculaExpressao(mutableListOf(tab[0][0],tab[0][1],tab[0][2],tab[0][3],tab[0][4])),

            mutableListOf(tab[2][0],tab[2][1],tab[2][2],tab[2][3],tab[2][4]) to calculaExpressao(mutableListOf(tab[2][0],tab[2][1],tab[2][2],tab[2][3],tab[2][4])),
            mutableListOf(tab[2][4],tab[2][3],tab[2][2],tab[2][1],tab[2][0]) to calculaExpressao(mutableListOf(tab[2][0],tab[2][1],tab[2][2],tab[2][3],tab[2][4])),

            mutableListOf(tab[4][0],tab[4][1],tab[4][2],tab[4][3],tab[4][4]) to calculaExpressao(mutableListOf(tab[4][0],tab[4][1],tab[4][2],tab[4][3],tab[4][4])),
            mutableListOf(tab[4][4],tab[4][3],tab[4][2],tab[4][1],tab[4][0]) to calculaExpressao(mutableListOf(tab[4][0],tab[4][1],tab[4][2],tab[4][3],tab[4][4])),


            //colunas
            mutableListOf(tab[0][0],tab[1][0],tab[2][0],tab[3][0],tab[4][0]) to calculaExpressao(mutableListOf(tab[0][0],tab[1][0],tab[2][0],tab[3][0],tab[4][0])),
            mutableListOf(tab[4][0],tab[3][0],tab[2][0],tab[1][0],tab[0][0]) to calculaExpressao(mutableListOf(tab[0][0],tab[1][0],tab[2][0],tab[3][0],tab[4][0])),

            mutableListOf(tab[0][2],tab[1][2],tab[2][2],tab[3][2],tab[4][2]) to calculaExpressao(mutableListOf(tab[0][2],tab[1][2],tab[2][2],tab[3][2],tab[4][2])),
            mutableListOf(tab[4][2],tab[3][2],tab[2][2],tab[1][2],tab[0][2]) to calculaExpressao(mutableListOf(tab[0][2],tab[1][2],tab[2][2],tab[3][2],tab[4][2])),

            mutableListOf(tab[0][4],tab[1][4],tab[2][4],tab[3][4],tab[4][4]) to calculaExpressao(mutableListOf(tab[0][4],tab[1][4],tab[2][4],tab[3][4],tab[4][4])),
            mutableListOf(tab[4][4],tab[3][4],tab[2][4],tab[1][4],tab[0][4]) to calculaExpressao(mutableListOf(tab[0][4],tab[1][4],tab[2][4],tab[3][4],tab[4][4]))

        ) as Map<MutableList<Any>, Double>
    }


    /**
     *This function receives a mutable list with the expression selected
     * and its going to check if its in the map of the valid expressions,
     * if it is then its going to return the value of that expression
     * @param MutableList<Any>
     * @return Double
     */
    fun validExpressions(mutableList: MutableList<Any>) : Double? {
        //for(i in 0..4){
        //    for(j in 0 .. 4){
        //        Log.i("teste", mutableList.toString())
        //    }
        //}
        //Log.i("teste2","o valor de return foi : " + (validExpressionsMap.get(mutableList)))


        val result2 = calculaExpressao(mutableList)
        //Log.i("teste2", "o valor de return2 foi : $result2")

        val score = validExpressionsMap.get(mutableList)

        return score
    }

    /**
     * This method is going to calculate the result of a given expression, in the populateTab
     * for later to store in the map
     * @param MutableList<Any>
     * @return Double
     */
    public fun calculaExpressao(mutableList: MutableList<Any>) : Double?{
        var result1 : Double = 0.0
        var result2 : Double = 0.0
        //1+6-3          1+6*2      1*2+9

        //caso 1+2-3
        if((mutableList[1] == '+' || mutableList[1] == '-') && (mutableList[3] == '+' || mutableList[3] == '-')){
            if(mutableList[1] == '+')
                result1 = ((mutableList[0] as Int) + (mutableList[2] as Int)).toDouble()
            else if(mutableList[1] == '-')
                result1 = ((mutableList[0] as Int) - (mutableList[2] as Int)).toDouble()
            if(mutableList[3] == '+')
                result2 = (result1 + (mutableList[4] as Int)).toDouble()
            else if(mutableList[3] == '-')
                result2 = (result1 - (mutableList[4] as Int)).toDouble()
            return result2
        }
        //caso 1*2*3
        //atencao, n ha protecoes contra divisao por 0 pq n e suposto o tab gerar 0s
        else if((mutableList[1] == '*' || mutableList[1] == '/') && (mutableList[3] == '*' || mutableList[3] == '/')) {
            if (mutableList[1] == '*')
                result1 = ((mutableList[0] as Int) * (mutableList[2] as Int)).toDouble()
            else if (mutableList[1] == '/')
                result1 = ((mutableList[0] as Int) / (mutableList[2] as Int)).toDouble()
            if (mutableList[3] == '*')
                result2 = (result1 * (mutableList[4] as Int)).toDouble()
            else if (mutableList[3] == '/')
                result2 = (result1 / (mutableList[4] as Int)).toDouble()
            return result2
        }
        //caso 1*2+3
        else if((mutableList[1] == '*' || mutableList[1] == '/') && (mutableList[3] == '+' || mutableList[3] == '-')){
            if(mutableList[1] == '*')
                result1 = ((mutableList[0] as Int ) * (mutableList[2] as Int)).toDouble()
            else if(mutableList[1] == '/')
                result1 = ((mutableList[0] as Int ) / (mutableList[2] as Int)).toDouble()
            if(mutableList[3] == '+')
                result2 = (result1 + (mutableList[4] as Int)).toDouble()
            else if(mutableList[3] == '-')
                result2 = (result1 - (mutableList[4] as Int)).toDouble()
            return result2
        }
        //caso 1+2*3
        else if((mutableList[1] == '+' || mutableList[1] == '-') && (mutableList[3] == '*' || mutableList[3] == '/')){
            if(mutableList[3] == '*')
                result1 = ((mutableList[2] as Int ) * (mutableList[4] as Int)).toDouble()
            else if(mutableList[3] == '/')
                result1 = ((mutableList[2] as Int ) / (mutableList[4] as Int)).toDouble()
            if(mutableList[1] == '+')
                result2 = (result1 + (mutableList[0] as Int)).toDouble()
            else if(mutableList[1] == '-')
                result2 = (result1 - (mutableList[0] as Int)).toDouble()
            return result2
        }


        return 0.0
    }
}