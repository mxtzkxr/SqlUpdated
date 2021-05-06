package dbworking

interface DBHandler {
    fun createDataBase(dbName: String)
    fun useDataBase(dbName: String)
    fun createTable(tabName: String, fields: String)
    fun makeConnection(fromTab: String, fromField: String, toTab: String, toField: String)
    fun insertData(tabName: String, fields: String, values: String)
    fun displayDBs() : String
    fun deleteDataBase(dbName: String)
}