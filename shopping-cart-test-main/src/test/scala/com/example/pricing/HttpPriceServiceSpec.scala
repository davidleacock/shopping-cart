package com.example.pricing

import cats.effect.unsafe.implicits.global
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class HttpPriceServiceSpec extends AnyWordSpec with Matchers {

  val priceService: PriceService = HttpPriceService

  "HttpPriceService" should {
    "return a number when the product is valid" in {
      (
        for {
          result <- priceService.getPrice("cheerios")
        } yield result match {
          case Some(price) => price shouldBe a[BigDecimal]
          case None        => fail("endpoint failed to find valid item")
        }
      ).unsafeRunSync()
    }

    "return None when service returns 404 if item not found" in {
      (
        for {
          result <- priceService.getPrice("invalid_item")
        } yield result match {
          case None    => succeed
          case Some(_) => fail("endpoint should not have returned valid price")
        }
      ).unsafeRunSync()
    }
  }
}
