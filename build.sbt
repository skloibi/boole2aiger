name := "model-checking"

version := "0.1"

scalaVersion := "2.12.8"

// https://mvnrepository.com/artifact/org.scala-lang.modules/scala-parser-combinators
libraryDependencies += "org.scala-lang.modules" %% "scala-parser-combinators" % "1.1.1"

name := "boole2aiger"
mainClass in assembly := Some("aig.Boole2Aiger")

assemblyJarName in assembly := "boole2aig.jar"
