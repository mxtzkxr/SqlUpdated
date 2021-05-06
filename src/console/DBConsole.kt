package console

import Communication
import java.util.*

class DBConsole: Communication {
    override fun write(message: String) {
        print(message)
    }

    override fun writeLine(message: String) {
        print("$message\n")
    }

    override fun readLine(message: String): String {
        print(message)
        return Scanner(System.`in`).nextLine()
    }

}