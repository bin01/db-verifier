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

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * DriverManager expects all {@link Driver} to loaded by the system class loader. The workaround for
 * this is to create a delegate class that implements java.sql.Driver. This delegate class will do
 * nothing but call the methods of an instance of a JDBC driver that we loaded dynamically.
 * 
 * @see {@link https://stackoverflow.com/questions/288828/how-to-use-a-jdbc-driver-from-an-arbitrary-location}
 */
public class DelegatingDriver implements Driver {
  private final Driver driver;

  public DelegatingDriver(Driver driver) {
    if (driver == null) {
      throw new IllegalArgumentException("Driver must not be null.");
    }
    this.driver = driver;
  }

  public Connection connect(String url, Properties info) throws SQLException {
    return driver.connect(url, info);
  }

  public boolean acceptsURL(String url) throws SQLException {
    return driver.acceptsURL(url);
  }

  public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException {
    return driver.getPropertyInfo(url, info);
  }

  public int getMajorVersion() {
    return driver.getMajorVersion();
  }

  public int getMinorVersion() {
    return driver.getMinorVersion();
  }

  public boolean jdbcCompliant() {
    return driver.jdbcCompliant();
  }

  @Override
  public Logger getParentLogger() throws SQLFeatureNotSupportedException {
    throw new SQLFeatureNotSupportedException();
  }
}
