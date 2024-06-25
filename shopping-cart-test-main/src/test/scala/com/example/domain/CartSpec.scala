package com.example.domain

import com.example.domain.Cart.{Product, ShoppingCart}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class CartSpec extends AnyWordSpec with Matchers {

  "ShoppingCart" should {
    "handle adding new product" in {
      val emptyCart = ShoppingCart(Map.empty)
      val dummyProduct = Product("n/a", BigDecimal(1))

      val updatedCart = emptyCart.addProduct(dummyProduct, 1)
      updatedCart shouldEqual ShoppingCart(Map(dummyProduct -> 1))
    }

    "apply 12.5% tax" in {
      val emptyCart = ShoppingCart(Map.empty)
      val dummyProduct = Product("n/a", BigDecimal(10.00))

      val updatedCart = emptyCart.addProduct(dummyProduct, 1)
      updatedCart.tax shouldBe BigDecimal(1.25)
    }

    "apply 12.5% tax and apply to subtotal" in {
      val emptyCart = ShoppingCart(Map.empty)
      val dummyProduct = Product("n/a", BigDecimal(10.00))

      val updatedCart = emptyCart.addProduct(dummyProduct, 1)
      updatedCart.total shouldBe BigDecimal(1.25 + 10.00)
    }

    "not add a negative amount of product" in {
      val emptyCart = ShoppingCart(Map.empty)
      val dummyProduct = Product("n/a", BigDecimal(1))

      val updatedCart = emptyCart.addProduct(dummyProduct, -1)
      updatedCart shouldEqual emptyCart
    }
  }
}
