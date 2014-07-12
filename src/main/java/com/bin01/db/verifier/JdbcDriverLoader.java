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

import java.net.URLClassLoader;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.google.common.base.Throwables;

public class JdbcDriverLoader {
  
  /**
   * Resolves and loads the jdbc drivers based on the given configuration for both test and control
   * 
   * @param config
   */
  public void load(VerifierConfig config) {
    loadDriver(config, config.getTestJdbcDriverMavenCoordinates(), config.getTestJdbcDriverName());
    loadDriver(config, config.getControlJdbcDriverMavenCoordinates(), config.getControlJdbcDriverName());
  }

  private void loadDriver(VerifierConfig config, String jdbcDriverMavenCoordinates, String jdbcDriverName) {
    try {
      URLClassLoader classLoader = ExternalClassLoader.getClassLoaderForCoordinates(config, jdbcDriverMavenCoordinates, jdbcDriverName);
      Driver driver = (Driver) Class.forName(jdbcDriverName, true, classLoader).newInstance();
      DriverManager.registerDriver(new DelegatingDriver(driver));
    } catch (InstantiationException | IllegalAccessException | SQLException | ClassNotFoundException e) {
      throw Throwables.propagate(e);
    }
  }
  
}
