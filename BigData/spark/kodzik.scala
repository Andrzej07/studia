sc.textFile("../unique_tracks.csv")
.map(line => line.split(","))
.foreach( line => if(line(2) == "Queen") println(line(3)))
System.exit(0)

