package com.nabob.conch.akka.persessionv2;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import com.nabob.conch.akka.utils.JsonUtil;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class HotelCustomer2 extends AbstractBehavior<HotelCustomer2.Command> {

  interface Command {}

  public static class Quote {
    public final String hotel;
    public final BigDecimal price;

    public Quote(String hotel, BigDecimal price) {
      this.hotel = hotel;
      this.price = price;
    }
  }

  public static class AggregatedQuotes implements Command {
    public final List<Quote> quotes;

    public AggregatedQuotes(List<Quote> quotes) {
      this.quotes = quotes;
    }
  }

  public static Behavior<Command> create(ActorRef<Hotel1.RequestQuote> hotel1, ActorRef<Hotel2.RequestPrice> hotel2) {
    return Behaviors.setup(context -> new HotelCustomer2(context, hotel1, hotel2));
  }

  public HotelCustomer2(ActorContext<Command> context,
                        ActorRef<Hotel1.RequestQuote> hotel1,
                        ActorRef<Hotel2.RequestPrice> hotel2) {
    super(context);

    Consumer<ActorRef<Object>> sendRequests =
            new Consumer<ActorRef<Object>>() {
                @Override
                public void accept(ActorRef<Object> replyTo) {
                    hotel1.tell(new Hotel1.RequestQuote(replyTo.narrow()));
                    hotel2.tell(new Hotel2.RequestPrice(replyTo.narrow()));
                }
            };

    int expectedReplies = 2;
    // Object since no common type between Hotel1 and Hotel2
    context.spawnAnonymous(
        Aggregator.create(
            Object.class,
            sendRequests,
            expectedReplies,
            context.getSelf(),
            this::aggregateReplies,
            Duration.ofSeconds(5)));
  }

  private AggregatedQuotes aggregateReplies(List<Object> replies) {
    List<Quote> quotes =
        replies.stream()
            .map(
                r -> {
                  // The hotels have different protocols with different replies,
                  // convert them to `HotelCustomer.Quote` that this actor understands.
                  if (r instanceof Hotel1.Quote) {
                    Hotel1.Quote q = (Hotel1.Quote) r;
                    return new Quote(q.hotel, q.price);
                  } else if (r instanceof Hotel2.Price) {
                    Hotel2.Price p = (Hotel2.Price) r;
                    return new Quote(p.hotel, p.price);
                  } else {
                    throw new IllegalArgumentException("Unknown reply " + r);
                  }
                })
            .sorted((a, b) -> b.price.compareTo(a.price))
            .collect(Collectors.toList());

    return new AggregatedQuotes(quotes);
  }

  @Override
  public Receive<Command> createReceive() {
    return newReceiveBuilder()
        .onMessage(AggregatedQuotes.class, this::onAggregatedQuotes)
        .build();
  }

  private Behavior<Command> onAggregatedQuotes(AggregatedQuotes aggregated) {
    if (aggregated.quotes.isEmpty()) getContext().getLog().info("Best Quote N/A");
    else getContext().getLog().info("Best {}", JsonUtil.object2Json(aggregated.quotes));
    return this;
  }
}