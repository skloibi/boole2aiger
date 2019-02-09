package aig

/**
  * Simple model for AND gates that stores the gate ID and the child
  * expression identifiers
  *
  * @param id    The ID of the gate
  * @param left  The ID of the left child
  * @param right The ID of the right child
  */
case class And(id: Int, left: Int, right: Int) {
  override def toString: String = s"$id $left $right"
}
