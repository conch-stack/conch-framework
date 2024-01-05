package com.nabob.conch.akka.demos.union;

public class MessageFinished {

 private volatile Boolean finished = false;

 public Boolean getFinished() {

  return finished;

 }

 public void setFinished(Boolean finished) {

  this.finished = finished;

 }

}