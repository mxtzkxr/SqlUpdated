class Communicator {
    companion object{
        private var communication: Communication? = null
        fun setCommunication(comm: Communication){
            communication = comm
        }
        fun write(message: String){
            if(communication != null){
                communication!!.write(message)
            }
        }
        fun writeLine(message: String){
            if(communication != null){
                communication!!.writeLine(message)
            }
        }
        fun readLine(message: String) : String{
            if(communication != null){
                return communication!!.readLine(message)
            }
            return ""
        }
    }
}