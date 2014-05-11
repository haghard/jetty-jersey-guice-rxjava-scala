package ru.server.service

import org.scalatest.{Matchers, FunSuite}
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import com.github.pathikrit.dijon._

@RunWith(classOf[JUnitRunner])
class JsonSpec extends FunSuite with Matchers {

  test("Json parser variable interpolation") {
    //val (name, age) = ("Tigri", 7)

    val name = "haghard"
    val age = 999

    import com.github.pathikrit.dijon._

    val cat = json""" { "name": "$name", "age": $age, "hobbies": ["eating", "purring"], "is cat": true } """

    assert(cat.name == name)
    assert(cat.age == age)
    println(cat)
  }

  test("from 1123") {
    val obj = `{}`
    obj.login = "haghard"
    obj.email = "haghard84@gmail.com"
    obj.address = `[]`
    obj.address(0) = "add1"
    obj.address(1) = "add2"

    obj.location = `{}`
    obj.location.xxx = 45
    obj.location.yyy = 90

    println(obj.login.as[String])
    println(obj.email.as[String])
    println(obj.address.as[JsonArray])
    println(obj.location.as[JsonObject])

    obj == parse(obj.toString())
    println(obj)
  }


  test("2342") {
    val vet = `{}`
    vet.name = "Dr. Kitty Specialist"
    vet.phones = `[]`
    val phone = "(650) 493-4233"
    vet.phones(2) = phone
    println(vet.toString)

    vet.address = `{}`
    vet.address.name = "Animal Hospital"
    vet.address.city = "Palo Alto"
    vet.address.zip = 94306
    println(vet.toString)

  }

}
