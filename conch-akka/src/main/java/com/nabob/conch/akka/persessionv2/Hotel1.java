package com.nabob.conch.akka.persessionv2;

import akka.actor.typed.ActorRef;

import java.math.BigDecimal;

public class Hotel1 {

  public static class RequestQuote {
    public final ActorRef<Quote> replyTo;

    public RequestQuote(ActorRef<Quote> replyTo) {
      this.replyTo = replyTo;
    }
  }

  public static class Quote {
    public final String hotel;
    public final BigDecimal price;

    public Quote(String hotel, BigDecimal price) {
      this.hotel = hotel;
      this.price = price;
    }
  }
}