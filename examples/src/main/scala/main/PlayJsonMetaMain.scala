package main

import meta.annotations.genFormats
import play.api.libs.json._

@genFormats
case class Comment(
                    _id: Option[Int],
                    content: String,
                    user: String,
                    postId: Int
                  )

object PlayJsonMetaMain {

  import Comment._

  def main(args: Array[String]): Unit = {
    val comment = Comment(
      _id = Some(12),
      content = "Lorem ipsum",
      user = "Alice",
      postId = 7621
    )
    val json = Json.toJson(comment)
    println(json)
  }
}
