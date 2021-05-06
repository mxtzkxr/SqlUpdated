package dbworking

import java.sql.ResultSet

abstract class SQLServer (protected var host: String = "localhost", protected var port: Int = 3306,
protected var username:String = "root", protected var password: String = "root"){
    abstract val sHost: String
    abstract val sPort: Int
    protected abstract val urlData: String
    abstract val isConnected: Boolean
    abstract fun connect() : Boolean
    abstract fun closeConnection() : Boolean
    abstract fun execute(sql: String)
    abstract fun executeQuery(sql: String) : ResultSet?
    fun resetUsername(newUsername: String){
        username = newUsername
    }

    fun resetPassword(newPassword: String){
        password = newPassword
    }

    fun resetHost(newHost: String){
        host = newHost
    }

    fun resetPort(newPort: Int){
        port = newPort
    }
}