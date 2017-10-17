package com.gudden.maven.model;

/**
 TokenStreams read tokens one at a time from a stream of input.
 */
public interface TokenStream {
   /**
    Returns the next token from the stream, or null if there is no token
    available.
    */
   public String nextToken();

   /**
    Returns true if the stream has tokens remaining.
    */
   public boolean hasNextToken();
}
