package dbworking

import Communicator
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import java.sql.SQLException

class MySQLServer(host: String, port: Int, username: String, password: String) : SQLServer(host, port, username, password) {
    override val sHost: String
        get() = host
    override val sPort: Int
        get() = port
    private var connection: Connection? = null
    override val urlData: String
        get() = "jdbc:mysql://$host:$port/?serverTimezone=UTC"
    override val isConnected: Boolean
        get() = if(connection != null) connection!!.isValid(1000) else false

    init {
        try {
            Communicator.writeLine("Подключение к: $host: $port")
            connection = DriverManager.getConnection(urlData, username, password)
            Communicator.writeLine("Успешно!")
            Communicator.writeLine("Вы используете MySQL сервер")
        }catch (ex: SQLException){
            Communicator.writeLine("Не удалось подключиться :(")
        }
    }

    override fun connect(): Boolean {
        if(isConnected){
            try {
                connection = DriverManager.getConnection(urlData, username, password)
            }catch (ex: SQLException){
                return false
            }
        }
        return true
    }

    override fun execute(sql: String){
        try {
            connection!!.createStatement().execute(sql)
        }catch (ex: SQLException){
            Communicator.writeLine("Ошибка в формировании запроса:\n\'$sql\'")
        }
    }

    override fun executeQuery(sql: String): ResultSet? {
        return try {
            connection!!.createStatement().executeQuery(sql)
        }catch (ex: SQLException){
            Communicator.writeLine("Ошибка в формировании запроса:\n\'$sql\'")
            null
        }
    }

    override fun closeConnection() : Boolean {
        return try {
            if(isConnected){
                connection!!.close()
            }
            true
        }catch (ex: SQLException){
            false
        }
    }
}