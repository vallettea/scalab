 import spark.SparkContext
 import spark.SparkContext._
 import spark.util.Vector
 import org.apache.log4j.Logger
 import org.apache.log4j.Level
 import scala.util.Random
 import scala.io.Source


 object Cluster {
   def parseVector(line: String): Vector = {
       return new Vector(line.split(',').map(_.toDouble))
   }
   def closestPoint(p: Vector, centers: Array[Vector]): Int = {
     var index = 0
     var bestIndex = 0
     var closest = Double.PositiveInfinity
     for (i <- 0 until centers.length) {
       val tempDist = p.squaredDist(centers(i))
       if (tempDist < closest) {
         closest = tempDist
         bestIndex = i
       }
     }
     return bestIndex
   }
   def average(ps: Seq[Vector]) : Vector = {
     val numVectors = ps.size
     var out = new Vector(ps(0).elements)
     for (i <- 1 until numVectors) {
       out += ps(i)
     }
     out / numVectors
   }
   // Add any new functions you need here
   def main(args: Array[String]) {
     Logger.getLogger("spark").setLevel(Level.WARN)
     val sc = new SparkContext("local", "Cluster", "/home/vagrant/alexandre/sparks")


     val K = 3
     val convergeDist = 1e-6
     val file = sc.textFile("iris.data")


     val data = file.map(line => {
       val Array(sepalLength, sepalWidth, petalLength, petalWidth, specy) = line.trim.split(",")
       specy -> new Vector(Array(sepalLength, sepalWidth, petalLength, petalWidth).map(_.toDouble))
     })


     val count = data.count()
     println("Number of records " + count)
     // Your code goes here
     var centroids = data.takeSample(false, K, 42).map(x => x._2)
     var tempDist = 1.0
     do {
       var closest = data.map(p => (closestPoint(p._2, centroids), p._2))
       var pointsGroup = closest.groupByKey()
       var newCentroids = pointsGroup.mapValues(ps => average(ps)).collectAsMap()
       tempDist = 0.0
       for (i <- 0 until K) {
         tempDist += centroids(i).squaredDist(newCentroids(i))
       }
       for (newP <- newCentroids) {
         centroids(newP._1) = newP._2
       }
       println("Finished iteration (delta = " + tempDist + ")")
     } while (tempDist > convergeDist)
     println("Clusters:")
     val numArticles = 10
     for((centroid, centroidI) <- centroids.zipWithIndex) {
       // print numArticles articles which are assigned to this centroid’s cluster
       data.filter(p => (closestPoint(p._2, centroids) == centroidI)).take(numArticles).foreach(
           x => println(x._1))
       println()
     }
     sc.stop()
     System.exit(0)
   }
 }