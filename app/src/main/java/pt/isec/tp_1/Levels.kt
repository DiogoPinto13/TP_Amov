package pt.isec.tp_1

//todos os gets vao receber o numero do nivel
//e vao retornar as coisas certas pro determinado nivel passado
class Levels{
    companion object{
        private val numLevels : Int = 3
        //gama de valores
        private val values : Map<Int, Int> = mapOf(
            1 to 9,
            2 to 99,
            3 to 999
        )
        //operadores
        private val operators : Map<Int, Array<Char>> = mapOf(
            1 to arrayOf<Char>('+','-'),
            2 to arrayOf<Char>('+','-','*'),
            3 to arrayOf<Char>('+','-','*','/')
        )
        //numero de tabuleiros por cada nivel
        private val numBoards : Map <Int, Int> = mapOf(
            1 to 5,
            2 to 7,
            3 to 9
        )
        //tempo para fazer todos os tabuleiros por cada nivel
        private val timeBoard : Map <Int, Int> = mapOf(
            0 to 5, //THE PAUSE
            1 to 60,
            2 to 50,
            3 to 50
        )
        //tempo bonus por cada tabuleiro bem feito pra cada nivel
        private val bonusTime : Map<Int, Int> = mapOf(
            1 to 3,
            2 to 4,
            3 to 5
        )

        //GETTERS
        public fun getValues(level : Int) : Int? {
            return values.get(level)
        }
        public fun getOperators(level : Int) : Array<Char>?{
            return operators.get(level)
        }
        public fun getNumBoards(level : Int) : Int?{
            return numBoards.get(level)
        }
        public fun getTimeBoard(level : Int) : Int?{
            return timeBoard.get(level)
        }
        public fun getBonusTIme(level : Int) : Int?{
            return bonusTime.get(level)
        }
        public fun getNumLevels() : Int{
            return numLevels
        }
    }
}