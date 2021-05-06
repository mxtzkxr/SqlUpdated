interface Communication {
    fun write(message: String)
    fun writeLine(message: String)
    fun readLine(message: String) : String
}