/*
 * Copyright 2015, Google Inc. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 *    * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *    * Redistributions in binary form must reproduce the above
 * copyright notice, this list of conditions and the following disclaimer
 * in the documentation and/or other materials provided with the
 * distribution.
 *
 *    * Neither the name of Google Inc. nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package io.grpc.examples.experimental.proxy;

import io.grpc.ManagedChannel;
import io.grpc.proxy.Communication;
import io.grpc.proxy.client.ProxyClientBuilder;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 */
public class JavaProxyClient {
  private static final Logger logger = Logger.getLogger(JavaProxyClient.class.getName());

  private final ManagedChannel channel;
  private final ProxyClientBuilder builder;
  

  /** Construct client connecting to HelloWorld server at {@code host:port}. */
  public JavaProxyClient() {
      channel = Communication.DEFAULT.createClientChannel();
	  builder = new ProxyClientBuilder(channel);
  }

  public void shutdown() throws InterruptedException {
    channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
  }

  /** Say hello to server. */
  public void greet(String user1, String user2) {
    try {
      logger.info("Will try to greet " + user1 + " and " + user2 + "...");
      
      HelloRequest request = new HelloRequest(user1);
      HelloRequest request2 = new HelloRequest(user2);
      
      
      
      GreeterService greeterService = builder.get(GreeterService.class);
      HelloResponse response = greeterService.hello(request, request2);
      
      
      
      logger.info("Greeting:------------ " + response.getMessage());
    } catch (RuntimeException e) {
      logger.log(Level.WARNING, "RPC failed", e);
      return;
    }
  }

  public void greetMore(String user1, String user2) {
    try {
      logger.info("Will try to greet " + user1 + " and " + user2 + "...");

      GreeterService greeterService = builder.get(GreeterService.class);
      String response = greeterService.helloMore(user1, user2);



      logger.info("Greeting:------------ " + response);
    } catch (RuntimeException e) {
      logger.log(Level.WARNING, "RPC failed", e);
      return;
    }
  }

  public void greetMulti(String user1, String user2) {
    try {
      logger.info("Will try to greet " + user1 + " and " + user2 + "...");

      GreeterService greeterService = builder.get(GreeterService.class);
      MultiResponse response = greeterService.helloList(user1, user2);


      for (HelloRequest greeting: response.getList()) {
        logger.info("Greeting:------------ " + greeting.getName());
      }
    } catch (RuntimeException e) {
      logger.log(Level.WARNING, "RPC failed", e);
      return;
    }
  }

  public void greetMap(String user1, String user2) {
    try {
      logger.info("Will try to greet " + user1 + " and " + user2 + "...");

      GreeterService greeterService = builder.get(GreeterService.class);
      MapResponse response = greeterService.helloMap(user1, user2);


      logger.info("Greeting:------------ " + user1 + " ... " + response.getMap().get(user1));
      logger.info("Greeting:------------ " + user2 + " ... " + response.getMap().get(user2));
    } catch (RuntimeException e) {
      logger.log(Level.WARNING, "RPC failed", e);
      return;
    }
  }

  public void stuff() {
      logger.info("Doing stuffs....");
    GreeterService greeterService = builder.get(GreeterService.class);
    logger.info("tostring: " + greeterService.toString());

  }
  /**
   * Greet server. If provided, the first element of {@code args} is the name to use in the
   * greeting.
   */
  public static void main(String[] args) throws Exception {
    JavaProxyClient client = new JavaProxyClient();
    try {
      final String user1 = args.length > 0 ? args[0] : "world";
      final String user2 = args.length > 1 ? args[1] : "other world";
      client.greet(user1, user2);
      client.greetMore(user1, user2);
      client.greetMulti(user1, user2);
      client.greetMap(user1, user2);
//      client.stuff();
    } finally {
      client.shutdown();
    }
  }
  
  
  
}
