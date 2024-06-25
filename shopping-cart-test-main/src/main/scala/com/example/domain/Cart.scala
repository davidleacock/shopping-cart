package com.example.domain

import scala.math.BigDecimal.RoundingMode

object Cart {

  case class Product(title: String, price: BigDecimal)

  case class ShoppingCart(products: Map[Product, Int]) {

    def addProduct(product: Product, quantity: Int): ShoppingCart = {
      if (quantity >= 1) {
        val updatedQuantity = products.getOrElse(product, 0) + quantity
        this.copy(products = products.updated(product, updatedQuantity))
      } else this
    }

    def subtotal: BigDecimal =
      products.foldLeft(BigDecimal(0)) { case (total, (product, quantity)) =>
        total + (product.price * quantity)
      }

    def tax: BigDecimal = (subtotal * 0.125).setScale(2, RoundingMode.HALF_UP)

    def total: BigDecimal = subtotal + tax
  }
}
