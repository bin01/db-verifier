/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bin01.db.verifier;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import com.google.common.base.Predicate;
import com.google.common.base.Throwables;
import com.google.common.collect.FluentIterable;

/**
 * 
 *
 */
public class JsonVerifierQueriesProvider implements VerifierQueriesProvider {
  private InputStream jsonStream;

  public JsonVerifierQueriesProvider(InputStream jsonStream) {
    this.jsonStream = jsonStream;
  }
  
  @Override
  public List<QueryPair> getQueriesBySuite(final String suite, int limit) {
    try {
      DefaultObjectMapper mapper = new DefaultObjectMapper();
      QueryPairs queryPairs = mapper.readValue(jsonStream, QueryPairs.class);
      Predicate<QueryPair> suiteEq = new Predicate<QueryPair>() {
        @Override
        public boolean apply(QueryPair input) {
          return input.getSuite().equalsIgnoreCase(suite);
        }
      };
      return FluentIterable.<QueryPair>from(queryPairs.getQueryPairs())
          .filter(suiteEq)
          .limit(limit)
          .toList();
    } catch (IOException e) {
      throw Throwables.propagate(e);
    }
  }

}
