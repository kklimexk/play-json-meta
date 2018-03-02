import meta.annotations.genFormats
import org.scalatest.{FunSuite, GivenWhenThen, Matchers}

import play.api.libs.functional.syntax._
import play.api.libs.json._

object TestEntity {
  @genFormats
  case class Comment(
                      _id: Option[Int],
                      content: String,
                      user: String,
                      postId: Int
                    )
}

class PlayJsonMetaTest extends FunSuite with GivenWhenThen with Matchers {
  import TestEntity._

  test("@genFormats annotation") {

    Given("a comment instance")
    val comment = Comment(
      _id = Some(12),
      content = "Lorem ipsum",
      user = "Alice",
      postId = 7621
    )

    When("comment is annotated with genFormats annotation")

    Then("it is possible to convert comment to json")
    val json = Json.toJson(comment)

    And("json should be equal to expected one")
    val expectedJson = "{\"_id\":12,\"content\":\"Lorem ipsum\",\"user\":\"Alice\",\"postId\":7621}"
    json shouldBe Json.parse(expectedJson)
  }
}
