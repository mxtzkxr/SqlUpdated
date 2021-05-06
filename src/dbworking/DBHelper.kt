package dbworking

import Communicator
import java.lang.StringBuilder


class DBHelper(private val server: SQLServer) : DBHandler {

    override fun createDataBase(dbName: String) {
        server.execute("CREATE DATABASE IF NOT EXISTS `$dbName`")
        Communicator.writeLine("База данных `$dbName` успешно создана!")
    }

    override fun useDataBase(dbName: String) {
        server.execute("USE `$dbName`")
        Communicator.writeLine("Выбрана база: `$dbName`")
    }

    override fun createTable(tabName: String, fields: String) {
        server.execute("CREATE TABLE IF NOT EXISTS `$tabName`($fields)")
        Communicator.writeLine("Таблица `$tabName` успешно создана!")
    }

    override fun makeConnection(fromTab: String, fromField: String, toTab: String, toField: String) {

    }

    override fun insertData(tabName: String, fields: String, values: String) {
        server.execute("INSERT INTO `$tabName` ($fields) VALUES ($values)")
    }

    override fun displayDBs(): String {
        val dbs = server.executeQuery("SHOW DATABASES") ?: return ""
        val sb = StringBuilder()
        while(dbs.next()){
            sb.append(dbs.getString(1) + "\n")
        }
        return sb.toString().trim()
    }

    override fun deleteDataBase(dbName: String) {
        server.execute("DROP DATABASE IF EXISTS `$dbName`")
    }

}