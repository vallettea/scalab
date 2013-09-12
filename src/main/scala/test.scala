package net.snips.scalab

import org.saddle.io.{CsvFile, CsvParser, CsvParams}
import org.saddle.Index
import scala.collection.JavaConversions._

import org.apache.commons.dbcp.BasicDataSource
import MyPostgresDriver.simple._
import Database.threadLocalSession
import scala.slick.jdbc.{StaticQuery => Q}

object Main extends App { 

	def parseCsv(filename: String, sep: String, nameTable: String, cols: Map[Int, (String, String)]): Unit = {
		//parse csv with saddle
		val file = CsvFile(filename)
		val params = CsvParams(separChar = sep.toCharArray.head, skipLines = 1)
		val titles = cols.values.map(_._1).toArray
		val frame = CsvParser.parsePar(cols.keys.toList, params)(file).setColIndex(Index(titles))
		val cleanedFrame = frame.rdropNA

		//to make type correpondance int postgres and scala
		val sqlTypes = Map("String" -> "varchar", "Double" -> "float8")
		val typeMap = cols.values.toMap
		def formatForSql(valType: String, value: String): String = valType match {
			case "String" => "'" + value + "'"
			case "Double" => value
		} 
		

		implicit val db = {
	        val ds = new BasicDataSource
	        ds.setDriverClassName("org.postgresql.Driver")
	        ds.setUsername("postgres")
	        ds.setUrl("jdbc:postgresql://localhost:5432/osm")
	        Database.forDataSource(ds)
	    }

    	db withSession {
    		//create table shema
    		Q.updateNA("drop table if exists " + nameTable).execute
			Q.updateNA("create table " + nameTable + "("+ cols.map(s => s._2._1 + " " + sqlTypes(s._2._2) + " not null").toList.mkString(", ") + ")").execute

			for ((row, vals) <- cleanedFrame.toRowSeq) {
				try {
					(Q.u + "insert into " + nameTable + " values(" + vals.toSeq.map(s => formatForSql(typeMap(s._1), s._2)).toList.mkString(", ") + ")").execute
				} catch { case e:Exception =>
					println(println("insert into " + nameTable + " values(" + vals.toSeq.map(s => formatForSql(typeMap(s._1), s._2)).toList.mkString(", ") + ")")
)
				}
			}
		}
	}


	parseCsv("laposte.csv", ";", "postes", Map(0 -> ("id", "String"), 1 -> ("name", "String"), 9 -> ("lat", "Double"), 10 -> ("lon", "Double")))
}
