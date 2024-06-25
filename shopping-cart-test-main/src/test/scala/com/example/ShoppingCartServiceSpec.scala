package com.example

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import com.example.domain.Cart.ShoppingCart
import com.example.pricing.{InMemoryPriceService, PriceService}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class ShoppingCartServiceSpec extends AnyWordSpec with Matchers {

  val priceService: PriceService = InMemoryPriceService
  val emptyCart: ShoppingCart = ShoppingCart(Map.empty)
  val shoppingCartService: IO[ShoppingCartService] = ShoppingCartService(priceService, emptyCart)

  "ShoppingCartService" should {
    "return valid cart after adding valid item" in {
      (
        for {
          service <- shoppingCartService
          result <- service.addProduct("cornflakes", 2)
        } yield result match {
          case Right(cart) =>
            cart.subtotal shouldEqual BigDecimal(5.04)
            cart.tax shouldEqual BigDecimal(0.63)
            cart.total shouldEqual BigDecimal(5.67)
          case Left(_) => fail("error in adding valid item to cart")
        }
      ).unsafeRunSync()
    }

    "add multiple valid item" in {
      (
        for {
          service <- shoppingCartService
          _ <- service.addProduct("cornflakes", 2)
          result <- service.addProduct("weetabix", 1)
        } yield result match {
          case Right(cart) =>
            cart.subtotal shouldEqual BigDecimal(15.02)
            cart.tax shouldEqual BigDecimal(1.88)
            cart.total shouldEqual BigDecimal(16.90)
          case Left(_) => fail("error in adding valid items to cart")
        }
      ).unsafeRunSync()
    }

    "return valid cart" in {
      (
        for {
          service <- shoppingCartService
          _ <- service.addProduct("cornflakes", 2)
          cart <- service.getCart
        } yield {
          cart.subtotal shouldEqual BigDecimal(5.04)
          cart.tax shouldEqual BigDecimal(0.63)
          cart.total shouldEqual BigDecimal(5.67)
        }
      ).unsafeRunSync()
    }

    "handle no price available" in {
      (
        for {
          service <- shoppingCartService
          result <- service.addProduct("item_without_price", 1)
        } yield result match {
          case Right(_)    => fail("should not return cart on failed entry")
          case Left(error) => error shouldEqual PriceNotFound
        }
      ).unsafeRunSync()
    }
  }
}
