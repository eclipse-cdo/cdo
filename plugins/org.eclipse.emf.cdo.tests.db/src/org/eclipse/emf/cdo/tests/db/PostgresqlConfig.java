/*
 * Copyright (c) 2011-2013, 2016, 2017, 2019, 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.db;

import org.eclipse.net4j.db.IDBAdapter;
import org.eclipse.net4j.db.postgresql.PostgreSQLAdapter;

import org.postgresql.ds.PGSimpleDataSource;

import javax.sql.DataSource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * @author Victor Roldan Betancort
 */
public class PostgresqlConfig extends AbstractSetupDBConfig
{
  public static final String DB_ADAPTER_NAME = "Postgresql";

  private static final String PROPERTIES_SYSTEM_PROPERTY = "psql.properties";

  private static final String HOST_PROPERTY = "psql.host";

  private static final String PORT_PROPERTY = "psql.port";

  private static final String USER_PROPERTY = "psql.user";

  private static final String PASSWORD_PROPERTY = "psql.password";

  private static final String VERSION_PROPERTY = "psql.version";

  private static final String DEFAULT_HOST = "localhost";

  private static final int DEFAULT_PORT = 5432;

  private static final String DEFAULT_USER = "postgres";

  private static final String DEFAULT_PASSWORD = "postgres";

  private static final int DEFAULT_SERVER_MAJOR_VERSION = 18;

  /**
   * @deprecated Use the {@code psql.*} configuration properties. This constant is retained for source compatibility.
   */
  @Deprecated
  public static final String HOST = DEFAULT_HOST;

  /**
   * @deprecated Use the {@code psql.*} configuration properties. This constant is retained for source compatibility.
   */
  @Deprecated
  public static final String USER = DEFAULT_USER;

  /**
   * @deprecated Use the {@code psql.*} configuration properties. This constant is retained for source compatibility.
   */
  @Deprecated
  public static final String PASS = DEFAULT_PASSWORD;

  public static final String SETUP_DATABASE_NAME = "postgres";

  private static final long serialVersionUID = 1L;

  private static final PostgreSQLSettings SETTINGS = PostgreSQLSettings.load();

  public PostgresqlConfig()
  {
    super(DB_ADAPTER_NAME);
  }

  @Override
  protected String getDBAdapterName()
  {
    return DB_ADAPTER_NAME;
  }

  @Override
  protected IDBAdapter createDBAdapter()
  {
    return new PostgreSQLAdapter();
  }

  @Override
  protected DataSource createDataSourceForDB(String dbName) throws SQLException
  {
    PGSimpleDataSource dataSource = new PGSimpleDataSource();
    dataSource.setServerNames(new String[] { SETTINGS.host });
    dataSource.setPortNumbers(new int[] { SETTINGS.port });
    dataSource.setDatabaseName(dbName == null ? SETUP_DATABASE_NAME : dbName);
    dataSource.setUser(SETTINGS.user);
    if (SETTINGS.password != null)
    {
      dataSource.setPassword(SETTINGS.password);
    }

    return dataSource;
  }

  @Override
  protected void dropDatabase(Connection connection, Statement stmt, String dbName) throws SQLException
  {
    stmt.execute("DROP DATABASE IF EXISTS " + dbName);
  }

  /**
   * @author Eike Stepper
   */
  private static final class PostgreSQLSettings
  {
    private final String host;

    private final int port;

    private final String user;

    private final String password;

    @SuppressWarnings("unused")
    private final int expectedServerMajorVersion;

    private PostgreSQLSettings(String host, int port, String user, String password, int expectedServerMajorVersion)
    {
      this.host = host;
      this.port = port;
      this.user = user;
      this.password = password;
      this.expectedServerMajorVersion = expectedServerMajorVersion;
    }

    private static PostgreSQLSettings load()
    {
      Properties fileProperties = new Properties();
      boolean explicitProperties = System.getProperty(PROPERTIES_SYSTEM_PROPERTY) != null;
      File propertiesFile = resolvePropertiesFile(explicitProperties);

      if (propertiesFile != null)
      {
        if (!propertiesFile.isFile())
        {
          if (explicitProperties)
          {
            throw new IllegalStateException("PostgreSQL properties file does not exist: " + propertiesFile.getAbsolutePath());
          }

          createDefaultPropertiesFile(propertiesFile);
        }

        try (InputStream input = new FileInputStream(propertiesFile))
        {
          fileProperties.load(input);
        }
        catch (IOException ex)
        {
          throw new IllegalStateException("Unable to read PostgreSQL properties file: " + propertiesFile.getAbsolutePath(), ex);
        }
      }

      String host = getValue(HOST_PROPERTY, fileProperties, DEFAULT_HOST).trim();
      int port = parseInteger(PORT_PROPERTY, getValue(PORT_PROPERTY, fileProperties, Integer.toString(DEFAULT_PORT)).trim());
      String user = getValue(USER_PROPERTY, fileProperties, DEFAULT_USER).trim();
      String password = getValue(PASSWORD_PROPERTY, fileProperties, DEFAULT_PASSWORD);
      int version = parseInteger(VERSION_PROPERTY, getValue(VERSION_PROPERTY, fileProperties, Integer.toString(DEFAULT_SERVER_MAJOR_VERSION)).trim());

      if (host.length() == 0)
      {
        throw new IllegalStateException("The PostgreSQL property " + HOST_PROPERTY + " must not be empty");
      }

      if (user.length() == 0)
      {
        throw new IllegalStateException("The PostgreSQL property " + USER_PROPERTY + " must not be empty");
      }

      if (port < 1 || port > 65535)
      {
        throw new IllegalStateException("The PostgreSQL property " + PORT_PROPERTY + " must be between 1 and 65535: " + port);
      }

      if (version < 1)
      {
        throw new IllegalStateException("The PostgreSQL property " + VERSION_PROPERTY + " must be a positive server major version: " + version);
      }

      return new PostgreSQLSettings(host, port, user, password, version);
    }

    private static String getValue(String propertyName, Properties fileProperties, String defaultValue)
    {
      String value = System.getProperty(propertyName);
      if (value == null)
      {
        value = fileProperties.getProperty(propertyName);
      }

      return value == null ? defaultValue : value;
    }

    private static int parseInteger(String propertyName, String value)
    {
      try
      {
        return Integer.parseInt(value);
      }
      catch (NumberFormatException ex)
      {
        throw new IllegalStateException("The PostgreSQL property " + propertyName + " must be an integer: " + value, ex);
      }
    }

    private static File resolvePropertiesFile(boolean explicitProperties)
    {
      if (explicitProperties)
      {
        return new File(System.getProperty(PROPERTIES_SYSTEM_PROPERTY));
      }

      File projectRelativeFile = new File("infra/postgresql/config.properties");
      File repositoryRelativeFile = new File("plugins/org.eclipse.emf.cdo.tests.db/infra/postgresql/config.properties");

      if (projectRelativeFile.isFile() || new File("infra/postgresql/config.properties.template").isFile())
      {
        return projectRelativeFile;
      }

      if (repositoryRelativeFile.isFile() || new File("plugins/org.eclipse.emf.cdo.tests.db/infra/postgresql/config.properties.template").isFile())
      {
        return repositoryRelativeFile;
      }

      return null;
    }

    private static void createDefaultPropertiesFile(File propertiesFile)
    {
      File template = new File(propertiesFile.getParentFile(), "config.properties.template");
      if (!template.isFile())
      {
        return;
      }

      File parent = propertiesFile.getParentFile();
      if (parent != null && !parent.isDirectory() && !parent.mkdirs())
      {
        throw new IllegalStateException("Unable to create PostgreSQL properties directory: " + parent.getAbsolutePath());
      }

      try (InputStream input = new FileInputStream(template); FileOutputStream output = new FileOutputStream(propertiesFile))
      {
        byte[] buffer = new byte[4096];
        int count;
        while ((count = input.read(buffer)) != -1)
        {
          output.write(buffer, 0, count);
        }
      }
      catch (IOException ex)
      {
        throw new IllegalStateException("Unable to create PostgreSQL properties file: " + propertiesFile.getAbsolutePath(), ex);
      }
    }
  }
}
