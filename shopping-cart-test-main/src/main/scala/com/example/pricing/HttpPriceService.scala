package com.example.pricing
import cats.effect.IO
import com.example.domain.Cart
import io.circe.generic.auto._
import org.http4s._
import org.http4s.circe.jsonOf
import org.http4s.ember.client.EmberClientBuilder

object HttpPriceService extends PriceService {

  implicit val productDecoder: EntityDecoder[IO, Cart.Product] = jsonOf[IO, Cart.Product]

  override def getPrice(name: String): IO[Option[BigDecimal]] = {
    val url = s"https://raw.githubusercontent.com/mattjanks16/shopping-cart-test-data/main/$name.json"

    EmberClientBuilder
      .default[IO]
      .build
      .use { client =>
        client.expect[Cart.Product](url)
      }
      .map(product => Some(product.price))
      .handleErrorWith(_ => IO.pure(None))
  }
}
