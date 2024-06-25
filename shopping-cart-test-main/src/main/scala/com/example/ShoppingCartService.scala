package com.example

import cats.effect.{IO, Ref}
import com.example.domain.Cart.{Product, ShoppingCart}
import com.example.pricing.PriceService

class ShoppingCartService(priceService: PriceService, cartRef: Ref[IO, ShoppingCart]) {

  def addProduct(name: String, quantity: Int): IO[Either[ShoppingCartError, ShoppingCart]] =
    priceService.getPrice(name).flatMap {
      case Some(price) =>
        val product = Product(name, price)

        cartRef
          .modify { cart =>
            val updatedCart = cart.addProduct(product, quantity)
            (updatedCart, Right(updatedCart))
          }
          .handleErrorWith { error =>
            IO.pure(Left(CartNotUpdated(error.getMessage)))
          }

      case None => IO.pure(Left(PriceNotFound))
    }

  def getCart: IO[ShoppingCart] = cartRef.get
}

object ShoppingCartService {
  def apply(priceService: PriceService, initialCart: ShoppingCart): IO[ShoppingCartService] =
    Ref.of[IO, ShoppingCart](initialCart).map(new ShoppingCartService(priceService, _))
}

trait ShoppingCartError
case object PriceNotFound extends ShoppingCartError
case class CartNotUpdated(reason: String) extends ShoppingCartError
