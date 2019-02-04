val M = sc.textFile("matrix/M.txt").map(line => {val t = line.split(" "); (t(0).trim.toInt, t(1).trim.toInt, t(2).trim.toDouble)}).toDF("x","y","val")
val N = sc.textFile("matrix/N.txt").map(line => {val t = line.split(" "); (t(0).trim.toInt, t(1).trim.toInt, t(2).trim.toDouble)}).toDF("x2","y2","val2")



val nn = M.join(N, M("x") === N("y2")).rdd
.groupByKey()


System.exit(0)
