/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.bin01.db.verifier;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Query {
  @JsonProperty
  private final String catalog;
  @JsonProperty
  private final String schema;
  @JsonProperty
  private final String query;

  @JsonCreator
  public Query(@JsonProperty("catalog") String catalog, @JsonProperty("schema") String schema,
      @JsonProperty("query") String query) {
    this.catalog = catalog;
    this.schema = schema;
    this.query = clean(query);
  }

  public String getCatalog() {
    return catalog;
  }

  public String getSchema() {
    return schema;
  }

  public String getQuery() {
    return query;
  }

  private static String clean(String sql) {
    sql = sql.replaceAll("\t", "  ");
    sql = sql.replaceAll("\n+", "\n");
    sql = sql.trim();
    while (sql.endsWith(";")) {
      sql = sql.substring(0, sql.length() - 1).trim();
    }
    return sql;
  }
}
