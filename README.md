# play-json-meta

[![Build Status](https://travis-ci.org/kkrzys/play-json-meta.svg?branch=master)](https://travis-ci.org/kkrzys/play-json-meta)

This is an extension to play-json module of play framework.

## Problem
There is always necessity to write explicitly implicit formats for models that are needed to transform entity from/to json.

## Solution
**@genFormats** annotation was created to solve this problem. This annotation generate implicit formats automatically.

## Example

Instead of writing this:

```scala
case class Comment(
                    _id: Option[Int],
                    content: String,
                    user: String,
                    postId: Int
                  )
                  
object Comment {
  val commentReads: Reads[Comment] = ((JsPath \ "_id").readNullable[Int] and (JsPath \ "content").read[String] and (JsPath \ "user").read[String] and (JsPath \ "postId").read[Int]) (Comment.apply _)
  val commentWrites: Writes[Comment] = ((JsPath \ "_id").writeNullable[Int] and (JsPath \ "content").write[String] and (JsPath \ "user").write[String] and (JsPath \ "postId").write[Int]) (unlift(Comment.unapply))
  implicit val commentFormat: Format[Comment] = Format(commentReads, commentWrites)
  implicit val commentOFormat: OFormat[Comment] = Json.format[Comment]
}
```

you can write only:

```scala
@genFormats
case class Comment(
                    _id: Option[Int],
                    content: String,
                    user: String,
                    postId: Int
                  )
```

and then companion object with all implicit values will be generated automatically.
