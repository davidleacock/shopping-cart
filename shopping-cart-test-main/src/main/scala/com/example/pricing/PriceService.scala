package com.example.pricing

import cats.effect.IO

trait PriceService {
  def getPrice(name: String): IO[Option[BigDecimal]]
}
