import org.apache.spark.SparkConf
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.Seconds

/**
  * Created by lumi.jang on 2017. 1. 10..
  */
object Stream {

  def main(args: Array[String]) {
    if ( args.length < 3 ) {
      System.err.println("<host> <port> <batchInterval> <output filename>")
    }
    val host = args(0)
    val port = args(1).toInt
    val batchInterval = args(2).toInt
    println(s"exec with master=local[4], host=$host, port=$port, batchInterval=$batchInterval sec")

    val conf = new SparkConf().setMaster("local[4]").setAppName("errorlog-streaming")
    val ssc = new StreamingContext(conf, Seconds(batchInterval))
    val lines = ssc.socketTextStream(host, port)
    val errLines = lines.filter(_.contains("error"))

    lines.print
    errLines.print
    errLines.saveAsTextFiles("results/errorLogs")

    println("==== start spark streaming context ====")
    ssc.start()
    println("==== await to terminate spark streaming context ====")
    ssc.awaitTerminationOrTimeout(10000)
    println("==== done! ====")
    ssc.stop()
  }
}
