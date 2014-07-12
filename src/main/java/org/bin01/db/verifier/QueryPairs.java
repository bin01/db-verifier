package org.bin01.db.verifier;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;

public class QueryPairs {
  @JsonProperty("queries")
  private final List<QueryPair> queryPairs;

  @JsonCreator
  public QueryPairs(@JsonProperty("queries") List<QueryPair> queryPairs) {
    this.queryPairs = ImmutableList.copyOf(queryPairs);
  }

  public List<QueryPair> getQueryPairs() {
    return queryPairs;
  }
}
