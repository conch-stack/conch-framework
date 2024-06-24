package com.nabob.conch.akka.persessionv2;

import akka.actor.typed.ActorRef;

import java.math.BigDecimal;

public class Hotel2 {

  public static class RequestPrice {
    public final ActorRef<Price> replyTo;

    public RequestPrice(ActorRef<Price> replyTo) {
      this.replyTo = replyTo;
    }
  }

  public static class Price {
    public final String hotel;
    public final BigDecimal price;

    public Price(String hotel, BigDecimal price) {
      this.hotel = hotel;
      this.price = price;
    }
  }
}