package dbworking

import DataWorker
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths

class ExcelDataWorker : DataWorker {
    private var wb: XSSFWorkbook? = null
    override fun getDataFrom(path: String) {
        val pth = path.trim()
        if(!Files.exists(Paths.get(pth)) || !Files.isRegularFile(Paths.get(pth))){
            throw IOException()
        }
        wb = XSSFWorkbook(pth)
    }

    override fun loadDataTo(source: Any) {
        val dbh = source as DBHandler
        wb!!.sheetIterator().forEach {
            val fields = StringBuilder()
            val values = StringBuilder()
            val rowNum = it.physicalNumberOfRows
            val cellNum = it.getRow(0).physicalNumberOfCells
            for(i in 0 until rowNum){
                values.clear()
                val row = it.getRow(i) ?: break
                if(i == 0){
                    for(j in 0 until cellNum){
                        val cell = row.getCell(j) ?: break
                        if(j == cellNum - 1){
                            fields.append("`$cell`")
                            continue
                        }
                        fields.append("`$cell`, ")
                    }
                    continue
                }
                for(j in 0 until cellNum){
                    val cell = row.getCell(j) ?: break
                    if(j == cellNum - 1){
                        values.append("'$cell'")
                        continue
                    }
                    values.append("'$cell', ")
                }
                dbh.insertData(it.sheetName, fields.toString(), values.toString())
            }
        }
    }
}