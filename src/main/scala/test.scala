import scala.math._
import scala.collection.mutable.{Set => MutableSet, Map => MutableMap}
import scala.io._
import java.io._

object Sampler extends App {

    // parsing arguments
    val infile = args(0)
    val withFeatures = args(1)
    val nbRepresentatives = args(2).toInt
    val tmax = args(3).toDouble
    val outfile = args(4)

    val data = parseFile(infile)
    
    val nbSamples = data.length
    val nbFeatures = data.head.length
    println(s"Parsed input file (Nb samples: $nbSamples, Nb features: $nbFeatures)")
    

    def parseFile(file: String): Array[Array[Double]] = {
        val lines = Source.fromFile(file).getLines.filter(_.trim.length > 0)
        val headers: Map[String, Int] = lines.next.split(",").zipWithIndex.toMap
        val indexToKeep = withFeatures.split(",").map(headers(_))
        lines.map(s => try {
            val line = s.split(",")
            Some(indexToKeep.map(line(_)).map(_.toDouble))
        } catch {
            case x: Throwable => {
                println("Error with " + s)
                x.printStackTrace
                println("------------")
                None
            }
        }).filter(!_.isEmpty).map(_.get).toArray
    }


    def boolToInt(b: Boolean): Double = if(b) 1 else 0


    def Dkl(row1:Int, row2:Int): Double = {
        val A = data(row1).map(v => if (v == 0.0d) 1.0e-10 else v)
        val B = data(row2).map(v => if (v == 0.0d) 1.0e-10 else v)
        (0 until nbFeatures).map(i => 
            if (A(i) > B(i)) log(A(i)/B(i)) * A(i)
            else log(B(i)/A(i)) * B(i)
        ).sum
        
    }


    def associatedElements(row: Int, unassociated: MutableSet[Int]): Set[Int] = {
        val entropyMap = unassociated.map(u => (u, Dkl(row, u))).toMap
        var ceil = entropyMap.values.min * tmax
        entropyMap.filter({ case (k,v) => v < ceil}).map(_._1).toSet
    }

    def mutualDiff(row: Int, unassociated: MutableSet[Int]): Double = {
        val LrTheta1 = unassociated
        val LrNew = associatedElements(row, unassociated)
        if (LrNew.size == 0) {
            0.0d
        }
        else {
            val LrTheta2 = unassociated.filter(LrNew.contains(_))
            val module2 = LrTheta2.size
            val PfrTheta2 = (0 until nbFeatures).map(j => LrTheta2.foldLeft(0.0d){(acc, i) => acc + data(i)(j)}).toArray.map(_/module2)

            val module1 = LrTheta1.size
            val PfrTheta1 = (0 until nbFeatures).map(j => LrTheta1.foldLeft(0.0d){(acc, i) => acc + data(i)(j)}).toArray.map(_/module1)
            val dk1 = (0 until PfrTheta2.size).map(i => 
                    PfrTheta2(i) * log(PfrTheta2(i)/PfrTheta1(i))
                ).sum

            val modulenew = LrNew.size
            val PfrThetaNew = (0 until nbFeatures).map(j => LrNew.foldLeft(0.0d){(acc, i) => acc + data(i)(j)}).toArray.map(_/modulenew)
            val dk2 = (0 until PfrThetaNew.size).map(i => 
                    PfrThetaNew(i) * log(PfrThetaNew(i)/PfrTheta1(i))
                ).sum
            (module2 * dk1 + modulenew * dk2)/data.size
        }
    }

    def objective(row: Int, R: MutableSet[Int], unassociated: MutableSet[Int]): Double = {
        val entropy = R.map(r => Dkl(row, r)).min
        // val mti = mutualDiff(row, unassociated)
        entropy //+ mti
    }




    // initialization
    var R = MutableSet[Int](0)
    var Etheta = MutableSet[Int]() ++ (1 until data.length)
    Etheta --= associatedElements(0, Etheta)

    var i = 0
    // R.size < nbRepresentatives | Etheta.size != 0
    while (i < nbRepresentatives - 1) {
        i+=1

        val EAsList = Etheta.toArray.sorted
        val weights = EAsList.par.map(e => objective(e, R, Etheta))

        val maxWeight = weights.max

        val toAdd = weights.zipWithIndex.filter({case (w,e) => w == maxWeight}).map(_._2).toList
        if (toAdd.length > 0) {
            val r = EAsList(toAdd.head)
            R += r
            Etheta -= r
            Etheta --= associatedElements(r, Etheta)
        }

        print(i + "                  \r")

    }
    println(s"Number of representatives found: $i")

    // output
    val out = new PrintWriter(new File(outfile))
    out.write(withFeatures + "\n")
    R.foreach{r => out.write(data(r).toList.mkString(",") + "\n")}
    out.close()


}
    


