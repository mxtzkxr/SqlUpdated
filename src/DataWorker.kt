interface DataWorker {
    fun getDataFrom(path: String)
    fun loadDataTo(source: Any)
}