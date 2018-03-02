package meta.annotations

import scala.meta._
import scala.meta.testkit._
import org.scalatest.{BeforeAndAfter, FunSuite, TestSuite}

import scala.collection.immutable.Seq

class GenFormatsTest extends FunSuite with BeforeAndAfter with MetaTestKit {

  private var typeName: Type.Name = _
  private var paramss: Seq[Seq[Term.Param]] = _
  private var seqOfReadsAndWritesValues: Seq[(Term, Term)] = _

  before {
    //case class Comment(_id: Option[Int], content: String, user: String, postId: Int)
    typeName = Type.Name("Comment")
    paramss = List(List(Term.Param(Nil, Term.Name("_id"), Some(Type.Apply(Type.Name("Option"), Seq(Type.Name("Int")))), None), Term.Param(Nil, Term.Name("content"), Some(Type.Name("String")), None), Term.Param(Nil, Term.Name("user"), Some(Type.Name("String")), None), Term.Param(Nil, Term.Name("postId"), Some(Type.Name("Int")), None)))
    seqOfReadsAndWritesValues = GenFormatsImpl.getSeqOfReadsAndWritesValues(paramss)
  }

  test("@genFormats creates reads val") {
    val obtained = GenFormatsImpl.createReadsVal(typeName, paramss, seqOfReadsAndWritesValues)
    val expected = q"""val commentReads: Reads[Comment] = ((JsPath \ "_id").readNullable[Int] and (JsPath \ "content").read[String] and (JsPath \ "user").read[String] and (JsPath \ "postId").read[Int]) (Comment.apply _)"""

    assertStructurallyEqual(obtained, expected)
  }

  test("@genFormats creates writes val") {
    val obtained = GenFormatsImpl.createWritesVal(typeName, paramss, seqOfReadsAndWritesValues)
    val expected = q"""val commentWrites: Writes[Comment] = ((JsPath \ "_id").writeNullable[Int] and (JsPath \ "content").write[String] and (JsPath \ "user").write[String] and (JsPath \ "postId").write[Int]) (unlift(Comment.unapply))"""

    assertStructurallyEqual(obtained, expected)
  }

  test("@genFormats creates format implicit val") {
    val obtained = GenFormatsImpl.createFormatImplicitVal(typeName)
    val expected = q"""implicit val commentFormat: Format[Comment] = Format(commentReads, commentWrites)"""

    assertStructurallyEqual(obtained, expected)
  }

  test("@genFormats creates oFormat implicit val") {
    val obtained = GenFormatsImpl.createOFormatImplicitVal(typeName)
    val expected = q"""implicit val commentOFormat: OFormat[Comment] = Json.format[Comment]"""

    assertStructurallyEqual(obtained, expected)
  }
}

trait MetaTestKit { _: TestSuite =>
  final def assertStructurallyEqual(obtained: Tree, expected: Tree): Unit = {
    StructurallyEqual(obtained, expected) match {
      case Left(AnyDiff(x, y)) =>
        fail(s"""Not Structurally equal!:
                |obtained: $x
                |expected: $y
             """.stripMargin)
      case _ =>
    }
  }
}
