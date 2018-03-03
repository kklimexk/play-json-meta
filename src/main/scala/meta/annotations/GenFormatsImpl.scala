package meta.annotations

import scala.collection.immutable.Seq
import scala.meta._

object GenFormatsImpl {

  def createReadsVal(name: Type.Name,
                     paramss: Seq[Seq[Term.Param]],
                     seqOfReadsAndWritesValues: Seq[(Term, Term)]): Defn.Val = {
    val readsName = Pat.Var.Term(Term.Name(name.value.toLowerCase + "Reads"))
    val readsValues = seqOfReadsAndWritesValues.map(_._1).mkString(" and\n").parse[Term].get

    q"""
        val $readsName: Reads[$name] = ($readsValues) (${Term.Name(name.value)}.apply _)
       """
  }

  def createWritesVal(name: Type.Name,
                      paramss: Seq[Seq[Term.Param]],
                      seqOfReadsAndWritesValues: Seq[(Term, Term)]): Defn.Val = {
    val writesName = Pat.Var.Term(Term.Name(name.value.toLowerCase + "Writes"))
    val writesValues = seqOfReadsAndWritesValues.map(_._2).mkString(" and\n").parse[Term].get

    q"""
        val $writesName: Writes[$name] = ($writesValues) (unlift(${Term.Name(name.value)}.unapply))
       """
  }

  def createFormatImplicitVal(name: Type.Name): Defn.Val = {
    val readsArgName = name.value.toLowerCase + "Reads".parse[Term.Arg]
    val writesArgName = name.value.toLowerCase + "Writes".parse[Term.Arg]

    val formatName = Pat.Var.Term(Term.Name(name.value.toLowerCase + "Format"))

    q"""
        implicit val $formatName: Format[$name] = Format(${Term.Name(readsArgName)}, ${Term.Name(writesArgName)})
       """
  }

  def createOFormatImplicitVal(name: Type.Name): Defn.Val = {
    val formatOName = Pat.Var.Term(Term.Name(name.value.toLowerCase + "OFormat"))

    q"""
        implicit val $formatOName: OFormat[$name] = Json.format[$name]
       """
  }

  def extractTermParam(param: Term.Param): (Type.Name, Boolean) = {
    val memberType = Type.Name(param.decltpe.map(_.toString()).get)
    memberType match {
      case Type.Name(value) if value.startsWith("Option") =>
        val memberTypeElements = memberType.value.split(Array('[', ']')).drop(1)
        val res =
          memberTypeElements.mkString("[") + (1 until memberTypeElements.length).foldLeft("")((acc, _) => acc + "]")

        (Type.Name(res), true)
      case _ => (memberType, false)
    }
  }

  def getSeqOfReadsAndWritesValues(paramss: Seq[Seq[Term.Param]]): Seq[(Term, Term)] = {
    paramss.flatMap(_.map { param =>
      val memberName = Term.Name(param.name.value).syntax
      val (memberType, isOption) = GenFormatsImpl.extractTermParam(param)

      if (isOption)
        (q"""(JsPath \ $memberName).readNullable[$memberType]""", q"""(JsPath \ $memberName).writeNullable[$memberType]""")
      else
        (q"""(JsPath \ $memberName).read[$memberType]""", q"""(JsPath \ $memberName).write[$memberType]""")
    })
  }
}
