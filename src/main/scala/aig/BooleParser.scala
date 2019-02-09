package aig

import scala.util.matching.Regex
import scala.util.parsing.combinator.RegexParsers

/**
  * Stateful parser that forms an AIG structure from given Boole formula.
  * The parser itself may then be used to access the defined variables and
  * expressions in order to generate the appropriate AIGER header.
  */
class BooleParser extends RegexParsers {

  /**
    * The variable mappings.
    * The occurring variables in the Boole formula are numerated
    * (with even numbers) and have to be stored separately to paste them into
    * the result file.
    */
  private[this] var variables: Map[String, Int] = Map.empty[String, Int]

  /**
    * The global counter that tracks the variable indices.
    */
  private[this] var variableCounter = 2

  /**
    * Increments the variable counter and returns the last ID.
    *
    * @return the last counter value
    */
  def nextVarId: Int = {
    val id = variableCounter
    variableCounter += 2
    id
  }

  /**
    * Mutable field that stores the occurring (AND-)gates.
    */
  private[this] var gates: List[And] = Nil

  /**
    * Helper function that appends the given gate and returns it.
    *
    * @param and The new gate
    * @return the gate that was added
    */
  private[this] def appendGate(and: And): And = {
    gates = gates :+ and
    and
  }

  // endregion
  //---------------------------------------------------------------------------
  // region Parser combinators

  def expression: Parser[Int] = (implies ~ rep("<->" ~> implies)) ^^ {
    case e ~ es => es.foldLeft(e) {
      (a, b) => {
        val and0 = appendGate(And(nextVarId, a, b))
        val and1 = appendGate(And(nextVarId, a ^ 1, b ^ 1))
        val and2 = appendGate(And(nextVarId, and0.id ^ 1, and1.id ^ 1))
        and2.id ^ 1
      }
    }
  }

  private[this] def implies: Parser[Int] = (or ~ opt("->" ~ or | "<-" ~ or)) ^^ {
    case a ~ Some("->" ~ b) => appendGate(And(nextVarId, a, b ^ 1)).id ^ 1
    case a ~ Some("<-" ~ b) => appendGate(And(nextVarId, a ^ 1, b)).id ^ 1
    case a ~ _              => a
  }

  private[this] def or: Parser[Int] = (and ~ rep("|" ~> and)) ^^ {
    case e ~ es => es.foldLeft(e) {
      (a, b) => appendGate(And(nextVarId, a ^ 1, b ^ 1)).id ^ 1
    }
  }

  private[this] def and: Parser[Int] = (not ~ rep("&" ~> not)) ^^ {
    case e ~ es =>
      es.foldLeft(e) {
        (a, b) => appendGate(And(nextVarId, a, b)).id
      }
  }

  private[this] def not: Parser[Int] = basic | (("!" ~> not) ^^ (_ ^ 1))

  private[this] def basic: Parser[Int] = variable | ("(" ~> expression <~ ")")

  private[this] def variable: Parser[Int] =
    """[a-zA-Z0-9\-\_\.\[\]$@]*[a-zA-Z0-9\_\.\[\]$@]""".r ^^ {
      name => {
        variables.getOrElse(name, {
          val id = nextVarId
          variables += (name -> id)
          id
        })
      }
    }

  // endregion
  //---------------------------------------------------------------------------
  // region Getters, overrides

  override def toString: String = {
    variables.foldLeft("") {
      (s, entry) => s"$s${entry._1} => ${entry._2}\n"
    } + gates.foldLeft("") {
      (s, entry) => s"$s$entry\n"
    }
  }

  def getVariables: Map[String, Int] = variables

  def getGates: List[And] = gates

  // endregion
  //---------------------------------------------------------------------------

}
