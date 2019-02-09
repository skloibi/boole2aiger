package aig

import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths}
import java.util.stream.Collectors

object Boole2Aiger {

  def invalidArgument(msg: String) =
    new IllegalArgumentException(msg)

  def main(args: Array[String]): Unit = {

    /*
    Input error handling
     */
    val fileName = args match {
      case Array(name, _ *) =>
        if (name.endsWith(".boole")) name
        else throw invalidArgument("File must be a 'boole' file")
      case _                => throw invalidArgument("Boole file required")
    }

    val path = Paths.get(fileName)

    if (!Files.isRegularFile(path))
      throw invalidArgument(s"The path '$path' does not denote a valid file")

    val booleExpression = Files.readAllLines(path)
      .stream()
      .collect(Collectors.joining(""))

    val aigExpression = boole2aig(booleExpression)

    // for debugging purposes
    println(aigExpression)

    // String -> ASCII
    val bytes = aigExpression.getBytes(StandardCharsets.US_ASCII)

    // use the input file name + extension ".aag" as output file name
    val aigFile = path
      .toAbsolutePath
      .getParent
      .resolve(fileName.replace(".boole", ".aag"))

    Files.write(aigFile, bytes)
  }

  def boole2aig(boole: String): String = {
    val parser = new BooleParser

    val result = parser.parseAll(parser.expression, boole)

    // create the M I L O A header
    val i = parser.getVariables.size
    val l = 0
    val o = 1
    val a = parser.getGates.size

    val m = i + l + a

    val header = s"aag $m $i $l $o $a\n"

    // write the input declarations
    val variables = parser.getVariables.values.map(v => s"$v\n").mkString

    // declare the output (this may be inverted)
    val output = result.get + "\n"

    // declare the gates / expressions
    val expressions = parser.getGates
      .map(and => s"${and.id} ${and.left} ${and.right}\n")
      .mkString

    header + variables + output + expressions
  }

}
