/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.net4j.db.tests;

import org.eclipse.net4j.db.BatchingIDChanger;
import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.IDBAdapter;
import org.eclipse.net4j.db.IDBAdapterID;
import org.eclipse.net4j.db.IDBConnection;
import org.eclipse.net4j.db.IDBDatabase;
import org.eclipse.net4j.db.StatementBatcher;
import org.eclipse.net4j.db.ddl.IDBField;
import org.eclipse.net4j.db.ddl.IDBTable;
import org.eclipse.net4j.db.derby.EmbeddedDerbyAdapter;
import org.eclipse.net4j.db.h2.H2Adapter;
import org.eclipse.net4j.db.hsqldb.HSQLDBAdapter;
import org.eclipse.net4j.db.mysql.MYSQLAdapter;
import org.eclipse.net4j.internal.db.DBAdapterDescriptor;
import org.eclipse.net4j.internal.db.DBAdapterID;
import org.eclipse.net4j.internal.db.DBAdapterRegistry;
import org.eclipse.net4j.spi.db.DelegatingDBAdapter;

import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import junit.framework.TestCase;

/**
 * Focused tests for adapter identity and registry behavior.
 *
 * @author Eike Stepper
 */
public class DBAdapterRegistryTest extends TestCase
{
  public void testVersionedIdentityAndLookup()
  {
    DBAdapterRegistry registry = new DBAdapterRegistry();
    registry.register(new VersionedAdapter("MySQL", "8"));
    registry.register(new VersionedAdapter("mysql", "8.4"));
    registry.register(new VersionedAdapter("mysql", "5.1.5"));

    assertEquals("8.4", registry.get("MYSQL").getVersion());
    assertEquals("8", registry.getAdapter(new DBAdapterID("mysql", "8.0.0")).getVersion());
    assertEquals("8.4", registry.getAdapter("MYSQL", "8.4").getVersion());
    assertEquals(3, registry.getAdapterIDs("mysql").length);
    assertEquals("5.1.5", registry.getAdapters("mysql")[0].getVersion());
    assertEquals("8.4", registry.getAdapters("mysql")[2].getVersion());

    try
    {
      registry.register(new VersionedAdapter("MYSQL", "8.0.0"));
      fail("Expected duplicate logical identity");
    }
    catch (IllegalStateException expected)
    {
      // Expected.
    }
  }

  public void testMetadataQueryDoesNotInstantiateDescriptor()
  {
    final int[] creations = { 0 };
    DBAdapterRegistry registry = new DBAdapterRegistry();
    registry.addDescriptor(new DBAdapterDescriptor("h2", "2.4")
    {
      @Override
      public IDBAdapter createDBAdapter()
      {
        ++creations[0];
        return new H2Adapter();
      }
    });

    IDBAdapterID[] ids = registry.getAdapterIDs();
    assertEquals(0, creations[0]);
    assertEquals("h2", ids[0].getName());
    assertEquals("2.4", ids[0].getVersion());

    IDBAdapter adapter = registry.get("h2");
    assertEquals(1, creations[0]);
    assertEquals("2.4", adapter.getVersion());
    assertTrue(adapter instanceof DelegatingDBAdapter);
  }

  public void testConnectionResolutionMaterializesOnlyTheMatchingDescriptor()
  {
    final int[] creations = { 0 };
    DBAdapterRegistry registry = new DBAdapterRegistry();
    registry.addDescriptor(new DBAdapterDescriptor("h2", "2.4")
    {
      @Override
      public IDBAdapter createDBAdapter()
      {
        ++creations[0];
        return new VersionedAdapter("different-identity", "99");
      }
    });
    registry.addDescriptor(new DBAdapterDescriptor("mysql", "8")
    {
      @Override
      public IDBAdapter createDBAdapter()
      {
        ++creations[0];
        return new H2Adapter();
      }
    });

    IDBAdapter adapter = registry.getAdapter(connection("H2"));
    assertEquals(1, creations[0]);
    assertEquals("h2", adapter.getName());
    assertEquals("2.4", adapter.getVersion());
  }

  public void testInventedProductResolvesProgrammaticAndLazyRegistrations()
  {
    DBAdapterRegistry registry = new DBAdapterRegistry();
    assertNull(registry.getAdapter(connection("AcmeDB")));
    SQLTestAdapter adapter = new SQLTestAdapter();
    registry.register(adapter);
    assertSame(adapter, registry.getAdapter(connection("AcmeDB")));

    final int[] selectedCreations = { 0 };
    final int[] unrelatedCreations = { 0 };
    DBAdapterRegistry lazyRegistry = new DBAdapterRegistry();
    lazyRegistry.addDescriptor(new DBAdapterDescriptor("acme-lazy", "1", new String[] { "AcmeDB" }, null, null, null)
    {
      @Override
      public IDBAdapter createDBAdapter()
      {
        ++selectedCreations[0];
        return new VersionedAdapter("wrong-constructed-identity", "9");
      }
    });
    lazyRegistry.addDescriptor(new DBAdapterDescriptor("unrelated", "1")
    {
      @Override
      public IDBAdapter createDBAdapter()
      {
        ++unrelatedCreations[0];
        return new H2Adapter();
      }
    });
    assertNull(lazyRegistry.getAdapter(connection("Unknown Product")));
    assertEquals(0, selectedCreations[0]);
    assertEquals(0, unrelatedCreations[0]);

    IDBAdapter lazyAdapter = lazyRegistry.getAdapter(connection("AcmeDB"));
    assertEquals(1, selectedCreations[0]);
    assertEquals(0, unrelatedCreations[0]);
    assertEquals("acme-lazy", lazyAdapter.getName());
    assertSame(lazyAdapter, lazyRegistry.getAdapter(connection("AcmeDB")));
  }

  public void testDirectProductMatchWinsAndAmbiguousAliasesReturnNull()
  {
    DBAdapterRegistry registry = new DBAdapterRegistry();
    registry.register(new RecognitionAdapter("mysql", "1", new String[] { "MySQL", "MariaDB" }, null, null, null));
    registry.register(new RecognitionAdapter("mariadb", "1", new String[] { "MariaDB" }, null, null, null));
    registry.register(new RecognitionAdapter("acme-a", "1", new String[] { "SharedDB" }, null, null, null));
    registry.register(new RecognitionAdapter("acme-b", "1", new String[] { "SharedDB" }, null, null, null));

    assertEquals("mariadb", registry.getAdapter(connection("MariaDB")).getName());
    assertNull(registry.getAdapter(connection("SharedDB")));
  }

  public void testDatabaseProductPatternMatchesDeclaredVariants()
  {
    DBAdapterRegistry registry = new DBAdapterRegistry();
    registry.register(new RecognitionAdapter("db2", "1", new String[] { "DB2" }, "DB2/.+", null, null));

    assertEquals("db2", registry.getAdapter(connection("DB2/LINUXX8664")).getName());
  }

  public void testDatabaseProductPatternMatchesDB2PlatformName()
  {
    DBAdapterRegistry registry = new DBAdapterRegistry();
    registry.register(new RecognitionAdapter("db2", "1", new String[] { "DB2", "IBM DB2", "DB2 UDB" }, "DB2/.+|DB2 for Linux, Unix and Windows", null, null));

    assertEquals("db2", registry.getAdapter(connection("DB2")).getName());
    assertEquals("db2", registry.getAdapter(connection("IBM DB2")).getName());
    assertEquals("db2", registry.getAdapter(connection("DB2 UDB")).getName());
    assertEquals("db2", registry.getAdapter(connection("DB2 for Linux, Unix and Windows")).getName());
  }

  public void testBuiltInAdaptersRecognizeProductsWhenRegisteredDirectly()
  {
    DBAdapterRegistry registry = new DBAdapterRegistry();
    registry.register(new MYSQLAdapter());
    registry.register(new HSQLDBAdapter());
    registry.register(new EmbeddedDerbyAdapter());

    assertEquals("mysql", registry.getAdapter(connection("MariaDB")).getName());
    assertEquals("hsqldb", registry.getAdapter(connection("HSQL Database Engine")).getName());
    assertEquals("derby-embedded", registry.getAdapter(connection("Apache Derby", "jdbc:derby:memory:db", "Apache Derby Embedded JDBC Driver")).getName());
    assertNull(registry.getAdapter(connection("Apache Derby", "jdbc:derby://localhost/db", "Apache Derby Network Client")));
  }

  public void testDelegatingAdapterRetainsRecognitionMetadata()
  {
    RecognitionAdapter delegate = new RecognitionAdapter("delegate-name", "1", new String[] { "AcmeDB" }, "AcmeDB/.+", "jdbc:acme:.*", "Acme Driver .+");
    IDBAdapter adapter = new DelegatingDBAdapter(delegate, "descriptor-name", "2");
    DBAdapterRegistry registry = new DBAdapterRegistry();
    registry.register(adapter);

    assertEquals("descriptor-name", adapter.getName());
    assertEquals("2", adapter.getVersion());
    assertEquals("descriptor-name", registry.getAdapter(connection("AcmeDB/product", "jdbc:acme:db", "Acme Driver 1")).getName());
    assertNull(registry.getAdapter(connection("AcmeDB/product", "jdbc:other:db", "Acme Driver 1")));
    assertNull(registry.getAdapter(connection("AcmeDB/product", "jdbc:acme:db", "Other Driver")));
  }

  public void testExistingAdapterInheritsDefaultRecognitionMetadata()
  {
    IDBAdapter adapter = new H2Adapter();

    assertEquals("h2", adapter.getDatabaseProductNames()[0]);
    assertNull(adapter.getDatabaseProductNamePattern());
    assertNull(adapter.getDatabaseURLPattern());
    assertNull(adapter.getDatabaseDriverNamePattern());
  }

  public void testNumericVersionSemantics()
  {
    IDBAdapterID eight = new VersionedAdapter("mysql", "8");
    IDBAdapterID eightZero = new VersionedAdapter("MySQL", "8.0.0");
    IDBAdapterID eightFour = new VersionedAdapter("mysql", "08.004.0");
    assertEquals(0, DBAdapterID.copy(eight).compareTo(DBAdapterID.copy(eightZero)));
    assertEquals(0, DBAdapterID.copy(eightFour).compareTo(DBAdapterID.copy(new VersionedAdapter("MYSQL", "8.4"))));
    assertTrue(DBAdapterID.copy(eight).compareTo(DBAdapterID.copy(new VersionedAdapter("mysql", "8.0.1"))) < 0);
  }

  public void testConnectionMetadataResolutionAndAliases()
  {
    DBAdapterRegistry registry = new DBAdapterRegistry();
    registry.register(new VersionedAdapter("h2", "1"));
    registry.register(new VersionedAdapter("h2", "2"));
    registry.register(new RecognitionAdapter("mysql", "8", new String[] { "MySQL", "MariaDB" }, null, null, null));
    registry.register(new RecognitionAdapter("hsqldb", "2", new String[] { "HSQL Database Engine" }, null, null, null));

    assertEquals("2", registry.getAdapter(connection("H2")).getVersion());
    assertEquals("mysql", registry.getAdapter(connection(" MariaDB ")).getName());
    assertEquals("hsqldb", registry.getAdapter(connection("HSQL Database Engine")).getName());
    assertNull(registry.getAdapter(connection("Unknown Database")));
  }

  public void testRealH2ConnectionResolution() throws Exception
  {
    DBAdapterRegistry registry = new DBAdapterRegistry();
    H2Adapter adapter = new H2Adapter();
    registry.register(adapter);

    try (Connection connection = DriverManager.getConnection("jdbc:h2:mem:registry-test"))
    {
      assertSame(adapter, registry.getAdapter(connection));
    }
  }

  public void testDerbyResolutionRequiresUnambiguousMetadata()
  {
    DBAdapterRegistry registry = new DBAdapterRegistry();
    registry.addDescriptor(new DBAdapterDescriptor("derby-embedded", "1", new String[] { "Apache Derby", "Derby" }, null, null, "jdbc:derby:(?!//).*")
    {
      @Override
      public IDBAdapter createDBAdapter()
      {
        return new VersionedAdapter(getName(), getVersion());
      }
    });
    registry.addDescriptor(new DBAdapterDescriptor("derby-client", "1", new String[] { "Apache Derby", "Derby" }, null, null, "jdbc:derby://.*")
    {
      @Override
      public IDBAdapter createDBAdapter()
      {
        return new VersionedAdapter(getName(), getVersion());
      }
    });

    assertNull(registry.getAdapter(connection("Apache Derby")));
    assertEquals("derby-client", registry.getAdapter(connection("Apache Derby", "jdbc:derby://localhost/db", "Apache Derby Network Client")).getName());
    assertEquals("derby-embedded", registry.getAdapter(connection("Apache Derby", "jdbc:derby:memory:db", "Apache Derby Embedded JDBC Driver")).getName());
  }

  public void testConnectionMetadataFailureIsWrapped()
  {
    DBAdapterRegistry registry = new DBAdapterRegistry();
    SQLException failure = new SQLException("metadata unavailable");
    try
    {
      registry.getAdapter(connection(failure));
      fail("Expected DBException");
    }
    catch (org.eclipse.net4j.db.DBException expected)
    {
      assertSame(failure, expected.getCause());
    }
  }

  public void testAssociatedAdapterTakesPrecedence()
  {
    DBAdapterRegistry registry = new DBAdapterRegistry();
    VersionedAdapter associated = new VersionedAdapter("unregistered", "9");
    IDBDatabase database = (IDBDatabase)Proxy.newProxyInstance(IDBDatabase.class.getClassLoader(), new Class<?>[] { IDBDatabase.class },
        (proxy, method, args) -> "getAdapter".equals(method.getName()) ? associated : null);
    Connection connection = (Connection)Proxy.newProxyInstance(IDBConnection.class.getClassLoader(), new Class<?>[] { IDBConnection.class },
        (proxy, method, args) -> "getDatabase".equals(method.getName()) ? database : null);

    assertSame(associated, registry.getAdapter(connection));
  }

  public void testBatchingIDChangerUsesResolvedAdapterForOrdinaryJDBCConnection()
  {
    String[] sql = { null };
    ensureSQLTestAdapterRegistered();
    Connection connection = sqlConnection(sql, true);
    assertFalse(connection instanceof IDBConnection);
    IDBTable table = (IDBTable)Proxy.newProxyInstance(IDBTable.class.getClassLoader(), new Class<?>[] { IDBTable.class },
        (proxy, method, args) -> "getName".equals(method.getName()) ? "Acme Table" : null);
    IDBField field = (IDBField)Proxy.newProxyInstance(IDBField.class.getClassLoader(), new Class<?>[] { IDBField.class }, (proxy, method, args) -> {
      if ("getName".equals(method.getName()))
      {
        return "key";
      }

      return "getTable".equals(method.getName()) ? table : null;
    });
    StatementBatcher batcher = new StatementBatcher(connection);
    new BatchingIDChanger(batcher, field).handleSingleChange(1, 2);

    assertEquals("UPDATE `Acme Table` SET `key`=2 WHERE `key`=1", sql[0]);
  }

  public void testDBUtilUsesResolvedAdapterForOrdinaryJDBCConnection()
  {
    String[] sql = { null };
    ensureSQLTestAdapterRegistered();
    Connection connection = sqlConnection(sql, false);
    assertFalse(connection instanceof IDBConnection);

    assertEquals(1, DBUtil.clearTable(connection, "Mixed Name"));
    assertEquals("DELETE FROM `Mixed Name`", sql[0]);
  }

  private static Connection connection(String productName)
  {
    return connection(productName, "", "");
  }

  private static Connection connection(String productName, String url, String driverName)
  {
    return connection(productName, url, driverName, null);
  }

  private static Connection connection(SQLException failure)
  {
    return connection(null, "", "", failure);
  }

  private static Connection connection(String productName, String url, String driverName, SQLException failure)
  {
    DatabaseMetaData metadata = (DatabaseMetaData)Proxy.newProxyInstance(DatabaseMetaData.class.getClassLoader(), new Class<?>[] { DatabaseMetaData.class },
        (proxy, method, args) -> {
          if ("getDatabaseProductName".equals(method.getName()))
          {
            if (failure != null)
            {
              throw failure;
            }

            return productName;
          }

          if ("getURL".equals(method.getName()))
          {
            return url;
          }

          if ("getDriverName".equals(method.getName()))
          {
            return driverName;
          }

          return null;
        });

    return (Connection)Proxy.newProxyInstance(Connection.class.getClassLoader(), new Class<?>[] { Connection.class },
        (proxy, method, args) -> "getMetaData".equals(method.getName()) ? metadata : null);
  }

  private static Connection sqlConnection(String[] sql, boolean batch)
  {
    Statement statement = (Statement)Proxy.newProxyInstance(Statement.class.getClassLoader(), new Class<?>[] { Statement.class }, (proxy, method, args) -> {
      if ("executeUpdate".equals(method.getName()))
      {
        sql[0] = (String)args[0];
        return 1;
      }

      if ("addBatch".equals(method.getName()) && batch)
      {
        sql[0] = (String)args[0];
      }

      if ("executeBatch".equals(method.getName()))
      {
        return new int[] { 1 };
      }

      return null;
    });

    DatabaseMetaData metadata = (DatabaseMetaData)Proxy.newProxyInstance(DatabaseMetaData.class.getClassLoader(), new Class<?>[] { DatabaseMetaData.class },
        (proxy, method, args) -> "getDatabaseProductName".equals(method.getName()) ? "AcmeDB" : null);
    return (Connection)Proxy.newProxyInstance(Connection.class.getClassLoader(), new Class<?>[] { Connection.class }, (proxy, method, args) -> {
      if ("getMetaData".equals(method.getName()))
      {
        return metadata;
      }

      return "createStatement".equals(method.getName()) ? statement : null;
    });
  }

  private static synchronized void ensureSQLTestAdapterRegistered()
  {
    IDBAdapter.Registry registry = IDBAdapter.Registry.INSTANCE;
    DBAdapterID id = new DBAdapterID("codex-test-acmedb", "1");
    IDBAdapter adapter = registry.get(id);
    if (adapter == null)
    {
      registry.putIfAbsent(id.getName(), new SQLTestAdapter());
      adapter = registry.get(id);
    }

    assertTrue("Unexpected adapter registered for " + id, adapter instanceof SQLTestAdapter);
    assertEquals(id.getName(), adapter.getName());
    assertEquals(id.getVersion(), adapter.getVersion());
    assertEquals("AcmeDB", adapter.getDatabaseProductNames()[0]);
    assertEquals("`", ((SQLTestAdapter)adapter).getTestIdentifierQuote());
  }

  /**
   * @author Eike Stepper
   */
  private static class VersionedAdapter extends H2Adapter
  {
    public VersionedAdapter(String name, String version)
    {
      super(name, version);
    }
  }

  /**
   * @author Eike Stepper
   */
  private static class RecognitionAdapter extends VersionedAdapter
  {
    private final String[] productNames;

    private final String productNamePattern;

    private final String urlPattern;

    private final String driverNamePattern;

    RecognitionAdapter(String name, String version, String[] productNames, String productNamePattern, String urlPattern, String driverNamePattern)
    {
      super(name, version);
      this.productNames = productNames;
      this.productNamePattern = productNamePattern;
      this.urlPattern = urlPattern;
      this.driverNamePattern = driverNamePattern;
    }

    @Override
    public String[] getDatabaseProductNames()
    {
      return productNames;
    }

    @Override
    public String getDatabaseProductNamePattern()
    {
      return productNamePattern;
    }

    @Override
    public String getDatabaseURLPattern()
    {
      return urlPattern;
    }

    @Override
    public String getDatabaseDriverNamePattern()
    {
      return driverNamePattern;
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class SQLTestAdapter extends RecognitionAdapter
  {
    SQLTestAdapter()
    {
      super("codex-test-acmedb", "1", new String[] { "AcmeDB" }, null, null, null);
    }

    @Override
    protected String getIdentifierQuote()
    {
      return "`";
    }

    String getTestIdentifierQuote()
    {
      return getIdentifierQuote();
    }

    @Override
    public String quoteTableName(IDBTable table)
    {
      return quoteIdentifier(table.getName());
    }
  }
}
