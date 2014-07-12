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
package com.bin01.db.verifier;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class QueryPair {
  @JsonProperty
  private final String suite;
  @JsonProperty
  private final String name;
  @JsonProperty
  private final Query test;
  @JsonProperty
  private final Query control;

  @JsonCreator
  public QueryPair(@JsonProperty("suite") String suite, @JsonProperty("name") String name,
      @JsonProperty("test") Query test, @JsonProperty("control") Query control) {
    this.suite = suite;
    this.name = name;
    this.test = test;
    this.control = control;
  }

  public String getSuite() {
    return suite;
  }

  public String getName() {
    return name;
  }

  public Query getTest() {
    return test;
  }

  public Query getControl() {
    return control;
  }
}
