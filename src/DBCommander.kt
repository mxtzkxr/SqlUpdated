import dbworking.*
import java.io.IOException

class DBCommander {
    private val container = HashMap<String, Any>()
    private val cmdList = "Список с описанием доступных команд:\n" +
            "'stop' - завершает работу программы\n" +
            "'dblist' - возвращает список бд, доступных на сервере\n" +
            "'connect' - процедура подключения к серверу\n" +
            "'reconnect' - процедура переподключения к серверу\n" +
            "'disconnect' - отключиться от сервера\n" +
            "'create table' - процедура создания и заполнения таблиц в бд через файл .xlsx\n" +
            "'create database' - создать БД\n" +
            "'delete database' - удалить БД\n" +
            "'use' - выбор бд\n" +
            "'delete' - удаление выбранного бд\n" +
            "'sql' - выполнение запроса по заданию\n"


    init {
        Communicator.writeLine("Запуск...")
        Communicator.writeLine("Ожидание команды")
        loop@ while (true){
            when (Communicator.readLine("-> ").trim().toLowerCase()){
                "stop" -> {
                    stop()
                    break@loop
                }
                "dblist" ->{
                    getDatabases()
                }
                "connect" ->{
                    connect()
                }
                "reconnect"->{
                    reconnect()
                }
                "disconnect" -> {
                    disconnect()
                }
                "help" -> {
                    Communicator.writeLine(cmdList)
                }
                "create database"->{
                    createDB()
                }
                "create table"->{
                    createTables()
                }
                "use"->{
                    useDB()
                }
                "delete database"->{
                    deleteDB()
                }
                "sql"->{
                    sqlRequest()
                }
                else -> {
                    Communicator.writeLine("Вы ничего не ввели :)")
                }
            }
        }
        Communicator.writeLine("Завершение работы...")
    }

    private fun deleteDB(){
        if(!container.containsKey("server")){
            Communicator.writeLine("Для данной команды требуется подключение к серверу")
            return
        }
        val server = container["server"] as MySQLServer
        if(!server.isConnected){
            Communicator.writeLine("Отсутствует подключение к серверу :(")
            return
        }
        if(!container.containsKey("dbh")){
            container["dbh"] = DBHelper(server)
        }
        val dbh = container["dbh"] as DBHelper
        dbh.deleteDataBase(Communicator.readLine("Введите имя базы данных для удаления: "))
    }

    private fun useDB(){
        if(!container.containsKey("server")){
            Communicator.writeLine("Для данной команды требуется подключение к серверу")
            return
        }
        val server = container["server"] as MySQLServer
        if(!server.isConnected){
            Communicator.writeLine("Отсутствует подключение к серверу :(")
            return
        }
        if(!container.containsKey("dbh")){
            container["dbh"] = DBHelper(server)
        }
        val dbh = container["dbh"] as DBHelper
        dbh.useDataBase(Communicator.readLine("Введите имя базы данных для выбора: "))
        //sqlRequest()
    }

    private fun createDB(){
        if(!container.containsKey("server")){
            Communicator.writeLine("Для данной команды требуется подключение к серверу")
            return
        }
        val server = container["server"] as MySQLServer
        if(!server.isConnected){
            Communicator.writeLine("Отсутствует подключение к серверу :(")
            return
        }
        if(!container.containsKey("dbh")){
            container["dbh"] = DBHelper(server)
        }
        val dbh = container["dbh"] as DBHelper
        dbh.createDataBase(Communicator.readLine("Введите имя базы данных: "))
    }

    private fun stop(){
        if(container.containsKey("server")){
            val server = container["server"] as MySQLServer
            server.closeConnection()
        }
    }

    private fun disconnect(){
        if(!container.containsKey("server")){
            Communicator.writeLine("Чтобы отключиться, нужно для начала подключиться :)")
            return
        }
        val server = container["server"] as SQLServer
        server.closeConnection()
        container.remove("server")
        Communicator.writeLine("Успешное отключение от сервера!")
    }

    private fun reconnect(){
        if(!container.containsKey("server")){
            Communicator.writeLine("Вы не подключены ни к какому серверу!")
            return
        }
        val server = container["server"] as SQLServer
        val host = Communicator.readLine("Введите имя хоста (по умолчанию 'localhost')\n-> ").trim()
        val port = Communicator.readLine("Введите порт (по умолчанию '3306')\n-> ").trim()
        val username = Communicator.readLine("Введите имя пользователя (по умолчанию 'root')\n-> ").trim()
        val password = Communicator.readLine("Введите пароль (по умолчанию 'root')\n-> ").trim()
        server.resetHost(if(host.isBlank()) "localhost" else host)
        server.resetPort(if(port.isBlank() || port.toIntOrNull() == null) 3306 else port.toInt())
        server.resetUsername(if(username.isBlank()) "root" else username)
        server.resetPassword(if(password.isBlank()) "root" else password)
        if(server.connect()){
            Communicator.writeLine("Вы успешно переподключились к серверу: ${server.sHost}: ${server.sPort}")
        }
    }

    private fun connect(){
        if(container.containsKey("server")){
            val server = container["server"] as SQLServer
            if(!server.isConnected){
                if(!server.connect()){
                    Communicator.writeLine("Не удается подключиться к серверу :(")
                    Communicator.writeLine("Попробуйте команду 'reconnect' для переподключения")
                }
            }
            Communicator.writeLine("Вы уже подключены к серверу: ${server.sHost}: ${server.sPort}")
            return
        }
        val host = Communicator.readLine("Введите имя хоста (по умолчанию 'localhost')\n-> ").trim()
        val port = Communicator.readLine("Введите порт (по умолчанию '3306')\n-> ").trim()
        val username = Communicator.readLine("Введите имя пользователя (по умолчанию 'root')\n-> ").trim()
        val password = Communicator.readLine("Введите пароль (по умолчанию 'root')\n-> ").trim()
        container["server"] = MySQLServer(
            if(host.isBlank()) "localhost" else host,
            if(port.isBlank() || port.toIntOrNull() == null) 3306 else port.toInt(),
            if(username.isBlank()) "root" else username,
            if(password.isBlank()) "root" else password
        )
    }

    private fun getDatabases(){
        if(!container.containsKey("server")){
            Communicator.writeLine("Для данной команды требуется подключение к серверу")
            return
        }
        val server = container["server"] as SQLServer
        if(!server.isConnected){
            Communicator.writeLine("Отсутствует подключение к серверу :(")
            return
        }
        if(!container.containsKey("dbh")){
            container["dbh"] = DBHelper(server)
        }
        val dbh = container["dbh"] as DBHelper
        Communicator.writeLine("")
        Communicator.writeLine(dbh.displayDBs())
    }

    private fun createTables(){
        if(!container.containsKey("server")){
            Communicator.writeLine("Подключитесь к серверу!")
            return
        }
        val server = container["server"] as SQLServer
        if(!server.isConnected){
            Communicator.writeLine("Отсутствует соединение с сервером!")
            return
        }
        if(!container.containsKey("dbh")){
            container["dbh"] = DBHelper(server)
        }
        if(!container.containsKey("tables")){
            container["tables"] = ExcelTableWorker()
        }
        if(!container.containsKey("data")){
            container["data"] = ExcelDataWorker()
        }
        val tabs = container["tables"] as DataWorker
        val data = container["data"] as DataWorker
        val dbh = container["dbh"] as DBHandler
        val pth = Communicator.readLine("Введите путь к файлу .xlsx: ")
        try {
            tabs.getDataFrom(pth)
            tabs.loadDataTo(dbh)
            data.getDataFrom(pth)
            data.loadDataTo(dbh)
        }catch (ex:IOException){
            Communicator.writeLine("Не удалось найти файл!!!")
        }

    }
    private fun sqlRequest(){
        if(!container.containsKey("server")){
            Communicator.writeLine("Подключитесь к серверу!")
            return
        }
        val server = container["server"] as SQLServer
        if(!server.isConnected){
            Communicator.writeLine("Отсутствует соединение с сервером!")
            return
        }
        val res = server.executeQuery("SELECT\n" +
                "students.id,\n" +
                "last_name,\n" +
                "CONCAT(students.last_name, ' ', SUBSTR(students.first_name, 1, 1), '.', SUBSTR(students.mid_name,1,1),\n" +
                "IF(students.mid_name IS NULL OR TRIM(students.mid_name) = '', '', '.')\n" +
                ") AS full_name,\n" +
                "IF(stipend IS NULL, 'а нету ничего, дааа', stipend) AS stipend\n" +
                "\n" +
                "FROM students\n" +
                "LEFT JOIN (SELECT\n" +
                "stud_id,\n" +
                "gr_id,\n" +
                "CASE\n" +
                "\tWHEN min_score >= 86 AND max_attempt = 1 \n" +
                "    \tTHEN 'повышенная'\n" +
                "\tWHEN min_score >= 71 AND min_score < 86 AND max_attempt = 1\n" +
                "    \tTHEN 'обычная'\n" +
                "\tELSE 'а нету ничего, дааа'\n" +
                "END AS stipend\n" +
                "\n" +
                "FROM(SELECT\n" +
                "stud_id,\n" +
                "gr_id,\n" +
                "previous_semester,\n" +
                "MIN(score) AS min_score,\n" +
                "MAX(score) AS max_score,\n" +
                "MAX(attempt) AS max_attempt,\n" +
                "COUNT(discipline) AS discipline_count\n" +
                "FROM(\n" +
                "SELECT\n" +
                "stud_id,\n" +
                "gr_id,\n" +
                "previous_semester,\n" +
                "disciplines.name AS discipline,\n" +
                "disciplines_plans.reporting_form AS reporting,\n" +
                "performance.score AS score,\n" +
                "performance.attempt AS attempt\n" +
                "FROM (SELECT\n" +
                "students.id AS stud_id,\n" +
                "semester.previous_semester AS previous_semester,\n" +
                "gr_id\n" +
                "FROM students LEFT JOIN (SELECT year, \n" +
                "groups.id AS gr_id, \n" +
                "2*(YEAR(NOW()) - year) - (CASE WHEN MONTH(NOW()) < 6 AND MONTH(NOW()) > 1 THEN 1 WHEN MONTH(NOW()) = 1 THEN 2 ELSE 0 END) AS `previous_semester` \n" +
                "FROM `groups` LEFT JOIN `academic_plans` \n" +
                "ON `groups`.`academic_plan_id` = `academic_plans`.`id`) AS semester\n" +
                "ON students.group_id = semester.gr_id) \n" +
                "AS full_stud \n" +
                "\n" +
                "LEFT JOIN performance ON stud_id = performance.student_id\n" +
                "\n" +
                "LEFT JOIN disciplines_plans ON \n" +
                "performance.disciplines_plan_id = disciplines_plans.id AND \n" +
                "disciplines_plans.semester_number = previous_semester\n" +
                "\n" +
                "LEFT JOIN disciplines ON disciplines_plans.discipline_id = disciplines.id\n" +
                "WHERE TRIM(disciplines_plans.reporting_form) = 'экзамен'\n" +
                "ORDER BY gr_id\n" +
                ") AS info\n" +
                "GROUP BY stud_id, gr_id, previous_semester) AS stp) AS new_stp \n" +
                "ON students.id = new_stp.stud_id\n" +
                "ORDER BY gr_id, full_name")
        System.out.println("id______last_name__________full_name________stipend")
        while (res?.next() == true) {
            System.out.println(
                    res.getString("id")+" | "+
                    res.getString("last_name")+" | "+
                    res.getString("full_name")+" | "+
                    res.getString("stipend")
            )
        }
    }
}