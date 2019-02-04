sc.textFile("../unique_tracks.csv")
.map(line => {val cols = line.split(","); (cols(2), 1)})
.reduceByKey(_+_)
.foreach( line => if(line._2 > 150) println(line))
System.exit(0)

