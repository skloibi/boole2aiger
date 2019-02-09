# boole2aiger
A simple tool that converts *Boole* files to their corresponding *AIGER* counterparts.
This can be used to generate *AIGER* files from *Limboole* input files.

The parser is written using the [Scala Standard Parser Combinator](https://github.com/scala/scala-parser-combinators) library and accepts formulas of the syntax defined in the [Limboole repository](http://fmv.jku.at/limboole/README).

*Note:* At the moment, this tool only produces *AIGER* files in *ASCII* format.

## See also
* Limboole solver: http://fmv.jku.at/limboole/
* AIGER: http://fmv.jku.at/aiger/
