package com.example.pricing
import cats.effect.IO
import com.example.domain.Cart
import io.circe.generic.auto._
import io.circe.parser._

import scala.io.Source

// Used for ShoppingCartService spec so tests so we can test the service without relying on the price website to be available
object InMemoryPriceService extends PriceService {

  override def getPrice(name: String): IO[Option[BigDecimal]] = {
    val resourcePath = s"/$name.json"
    val resource = Option(getClass.getResourceAsStream(resourcePath))

    resource match {
      case Some(inputStream) =>
        IO {
          val source = Source.fromInputStream(inputStream)
          val json =
            try source.mkString
            finally source.close()
          decode[Cart.Product](json) match {
            case Right(product) => Some(product.price)
            case Left(_)        => None
          }
        }
      case None => IO.pure(None)
    }
  }
}
