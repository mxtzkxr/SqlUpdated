import console.DBConsole

fun main(){
    val console = DBConsole()
    Communicator.setCommunication(console)
    val cmd = DBCommander()
}