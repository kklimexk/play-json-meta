package meta.annotations

import scala.annotation.StaticAnnotation
import scala.collection.immutable.Seq
import scala.meta._

class genFormats extends StaticAnnotation {
  inline def apply(defn: Any): Any = meta {
    defn match {
      // companion object exists
      case Term.Block(
      Seq(cls@Defn.Class(mods, name, _, ctor, _), companion: Defn.Object)) if mods.map(_.toString()).contains(Mod.Case().toString()) =>
        val seqOfReadsAndWritesValues: Seq[(Term, Term)] = GenFormatsImpl.getSeqOfReadsAndWritesValues(ctor.paramss)

        val readsVal = GenFormatsImpl.createReadsVal(name, ctor.paramss, seqOfReadsAndWritesValues)
        val writesVal = GenFormatsImpl.createWritesVal(name, ctor.paramss, seqOfReadsAndWritesValues)
        val formatImplicitVal = GenFormatsImpl.createFormatImplicitVal(name)
        val formatOImplicitVal = GenFormatsImpl.createOFormatImplicitVal(name)

        val templateStats: Seq[Stat] =
          readsVal +: writesVal +: formatImplicitVal +: formatOImplicitVal +: companion.templ.stats.getOrElse(Nil)
        val newCompanion = companion.copy(
          templ = companion.templ.copy(stats = Some(templateStats)))
        Term.Block(Seq(cls, newCompanion))
      // companion object does not exists
      case cls@Defn.Class(mods, name, _, ctor, _) if mods.map(_.toString()).contains(Mod.Case().toString()) =>
        val seqOfReadsAndWritesValues: Seq[(Term, Term)] = GenFormatsImpl.getSeqOfReadsAndWritesValues(ctor.paramss)

        val readsVal = GenFormatsImpl.createReadsVal(name, ctor.paramss, seqOfReadsAndWritesValues)
        val writesVal = GenFormatsImpl.createWritesVal(name, ctor.paramss, seqOfReadsAndWritesValues)
        val formatImplicitVal = GenFormatsImpl.createFormatImplicitVal(name)
        val formatOImplicitVal = GenFormatsImpl.createOFormatImplicitVal(name)

        val body = Seq(q"$readsVal", q"$writesVal", q"$formatImplicitVal", q"$formatOImplicitVal")

        val companion =
          q"""object ${Term.Name(name.value)} {
                                import play.api.libs.functional.syntax._
                                ..$body
                            }"""
        Term.Block(Seq(cls, companion))
      case _ =>
        println(defn.structure)
        abort("@genFormats must annotate a case class.")
    }
  }
}
