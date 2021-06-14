import java.util.Properties
import java.sql.DriverManager

class JbdcConnector(_host:String, _port:String, _database:String, _username:String, _password:String){
  val host = _host
  val port = _port
  val database = _database
  val username = _username
  val password = _password
  val url = "jdbc:hive2://"+host+":"+port+"/"+database
  val properties = new Properties()
  properties.setProperty("driverClassName", "org.apache.hive.jdbc.HiveDriver")
  properties.setProperty("user", username)
  properties.setProperty("password", password)
  val connection = DriverManager.getConnection(url, properties)
  val statement = connection.createStatement

  def getTables():Array[String]= {
    var tables = new Array[String](0)
    val resultSet = statement.executeQuery("show tables")
    try {
      while (resultSet.next) {
        val tableName = resultSet.getString(1)
        //输出所有表名
        tables = tables :+ tableName
      }
      tables
    }
  }

  def getCol(table: String):Array[String]={
    var columns = new Array[String](0)
    val resultSet = statement.executeQuery("show columns from "+table)
    try {
      while (resultSet.next) {
        val column = resultSet.getString(1)
        //输出所有表名
        columns=columns:+column
      }
    }
    columns
  }

  def getQuery(Query: String):Array[Array[String]]={
    var resultSet = statement.executeQuery(Query)
    val data = resultSet.getMetaData()
    val colnum = data.getColumnCount()
    var rownum = 0
    while ( {
      resultSet.next
    }) rownum += 1
    resultSet = statement.executeQuery(Query)
    val res= Array.ofDim[String](rownum+1,colnum)
    for( i <- 0 to colnum-1){
      res(0)(i) = data.getColumnName(i+1)
    }
    var tag=1
    while (resultSet.next) {
      for( i <- 0 to colnum-1){
        res(tag)(i) = resultSet.getString(i+1)
      }
      tag+=1
    }
    res
  }

}
