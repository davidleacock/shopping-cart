package com.example

import cats.effect.{IO, IOApp}
import com.example.domain.Cart.ShoppingCart
import com.example.pricing.HttpPriceService

/*
  NOTES:
  As per the readme there was no external api requested so when the application runs there is no way to add/update/get
  a cart. This run is just to show that the system can run.  Please see ShoppingCartServiceSpec for how to use service.

  The ShoppingCart domain object is only responsible for taking in a quantity and a known price and updating itself in an
  immutable way.  The act of determining the price is outside of the responsibility of the ShoppingCart, it is also
  something that can fail as it reaches out to the outside world for its information. This is why I created the PriceService trait
  to encapsulate this effectful behaviour. I created an InMemory version as well for testing as I didn't want my tests to
  rely on the site being active to work.

  The ShoppingCartService is what takes the domain objects along with the PriceService and produce business value and lets
  the user work with a cart without knowing the price of something. It also allows any errors in price or cart to be mapped to
  a more user-friendly domain specific error. I made use of the Ref type in order to manipulate the immutable
  cart but also give the service the ability to return this to the caller if they needed to inspect the cart state using the getCart method
  all while under the IO context. I had thought about using the State monad but thought that may have been assuming too much as far as
  architectural decisions go.

  Thank you for your time, David
 */
object Main extends IOApp.Simple {
  def run: IO[Unit] = {
    for {
      priceService <- IO(HttpPriceService)
      initialCart = ShoppingCart(Map.empty)
      _ <- ShoppingCartService(priceService, initialCart)
      _ <- IO(println("ShoppingCartService running..."))
      _ <- IO.never[Unit]
    } yield ()
  }
}
