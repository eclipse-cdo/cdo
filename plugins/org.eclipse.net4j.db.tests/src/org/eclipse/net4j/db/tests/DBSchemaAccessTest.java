/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.net4j.db.tests;

import org.eclipse.net4j.db.DBType;
import org.eclipse.net4j.db.IDBAdapter;
import org.eclipse.net4j.db.IDBConnection;
import org.eclipse.net4j.db.IDBPreparedStatement;
import org.eclipse.net4j.db.IDBPreparedStatement.ReuseProbability;
import org.eclipse.net4j.db.IDBSchemaTransaction;
import org.eclipse.net4j.db.ddl.IDBTable;
import org.eclipse.net4j.internal.db.DBConnection;
import org.eclipse.net4j.internal.db.DBDatabase;
import org.eclipse.net4j.spi.db.DBAdapter;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.util.concurrent.CountDownLatch;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;

/**
 * Tests the schema access lock that keeps schema transactions of a {@link DBDatabase} apart from the prepared
 * statements that its {@link DBConnection connections} have checked out.
 * <p>
 * A schema writer is a thread that commits a schema transaction, like a DB store that creates a missing mapping table
 * while other commits are writing data. The database runs on stub JDBC connections, so no real database is needed.
 *
 * @author Eike Stepper
 */
public class DBSchemaAccessTest extends TestCase
{
  protected static final long WAIT_MILLIS = 10000L;

  protected DBDatabase database;

  @Override
  protected void setUp() throws Exception
  {
    super.setUp();
    database = new DBDatabase((IDBAdapter)new TestAdapter(), DBSchemaAccessTest::createConnection, "TEST", false, false);
  }

  @Override
  protected void tearDown() throws Exception
  {
    database.close();
    database = null;
    super.tearDown();
  }

  /**
   * A connection that already holds a statement prepares another one while a schema writer waits for the first one.
   * The second statement must not queue behind the writer, or the connection and the writer wait for each other until
   * the schema access timeout expires.
   */
  public void testNestedStatementWhileSchemaWriterWaits() throws Exception
  {
    IDBConnection connection = database.getConnection();
    IDBPreparedStatement statement1 = connection.prepareStatement("SELECT 1", ReuseProbability.HIGH);

    Worker writer = startSchemaWriter("TABLE1");
    awaitWaiting(writer);

    IDBPreparedStatement statement2 = connection.prepareStatement("SELECT 2", ReuseProbability.HIGH);

    // A connection without checked-out statements still queues behind the waiting writer.
    IDBConnection otherConnection = database.getConnection();
    Worker reader = start("reader", () -> otherConnection.prepareStatement("SELECT 3", ReuseProbability.HIGH).close());
    awaitWaiting(reader);

    statement2.close();
    assertTrue(writer.isAlive());

    statement1.close();
    awaitSuccess(writer);
    awaitSuccess(reader);
  }

  /**
   * A schema writer that fails while waiting must not release the read access of a statement that is still checked
   * out.
   */
  public void testFailedSchemaWriterKeepsReadAccess() throws Exception
  {
    IDBConnection connection = database.getConnection();
    IDBPreparedStatement statement = connection.prepareStatement("SELECT 1", ReuseProbability.HIGH);

    Worker failingWriter = startSchemaWriter("TABLE1");
    awaitWaiting(failingWriter);
    failingWriter.interrupt();
    awaitInterrupted(failingWriter);

    Worker writer = startSchemaWriter("TABLE2");
    awaitWaiting(writer);

    statement.close();
    awaitSuccess(writer);
  }

  /**
   * A write request that fails while waiting must not be counted as a waiting writer afterwards. Otherwise later readers
   * stop sharing the active read access and queue behind it.
   */
  public void testFailedWriteRequestDoesNotBlockReaders() throws Exception
  {
    IDBConnection connection = database.getConnection();
    IDBPreparedStatement statement = connection.prepareStatement("SELECT 1", ReuseProbability.HIGH);

    Worker failingWriter = start("writer", () -> database.endSchemaAccess(database.beginSchemaAccess(true)));
    awaitWaiting(failingWriter);
    failingWriter.interrupt();
    awaitInterrupted(failingWriter);

    IDBConnection otherConnection = database.getConnection();
    otherConnection.prepareStatement("SELECT 2", ReuseProbability.HIGH).close();

    statement.close();
  }

  /**
   * Ending a schema access with a token that is not active must fail and must leave the active access intact.
   */
  public void testEndSchemaAccessRejectsInactiveToken() throws Exception
  {
    assertInactiveToken(new Object());

    IDBConnection connection = database.getConnection();
    IDBPreparedStatement statement = connection.prepareStatement("SELECT 1", ReuseProbability.HIGH);
    assertInactiveToken(new Object());
    assertInactiveToken(null);

    Worker writer = startSchemaWriter("TABLE1");
    awaitWaiting(writer);

    statement.close();
    awaitSuccess(writer);
  }

  /**
   * Releasing one reader token twice must not decrement the reader count belonging to another reader.
   */
  public void testRepeatedReaderTokenReleaseDoesNotReleaseAnotherReader() throws Exception
  {
    Object firstToken = database.beginSchemaAccess(false);
    Object secondToken = database.beginSchemaAccess(false);
    assertNotSame(firstToken, secondToken);

    database.endSchemaAccess(firstToken);
    assertInactiveToken(firstToken);

    Worker writer = startSchemaWriter("TABLE1");
    awaitWaiting(writer);

    database.endSchemaAccess(secondToken);
    awaitSuccess(writer);
  }

  /**
   * Closing a statement more than once must release its schema access and return it to the statement cache only once.
   */
  public void testRepeatedCloseReleasesStatementOnce() throws Exception
  {
    IDBConnection connection = database.getConnection();
    IDBPreparedStatement statement1 = connection.prepareStatement("SELECT 1", ReuseProbability.HIGH);

    IDBConnection otherConnection = database.getConnection();
    IDBPreparedStatement statement2 = otherConnection.prepareStatement("SELECT 2", ReuseProbability.HIGH);
    statement2.close();
    statement2.close();

    IDBPreparedStatement statement3 = otherConnection.prepareStatement("SELECT 2", ReuseProbability.HIGH);
    IDBPreparedStatement statement4 = otherConnection.prepareStatement("SELECT 2", ReuseProbability.HIGH);
    assertNotSame(statement3, statement4);
    statement4.close();
    statement3.close();

    Worker writer = startSchemaWriter("TABLE1");
    awaitWaiting(writer);

    statement1.close();
    awaitSuccess(writer);
  }

  /**
   * A close from an earlier checkout must not release the read access held by a later checkout of the same cached SQL.
   */
  public void testStaleStatementCloseDoesNotReleaseNewCheckout() throws Exception
  {
    IDBConnection connection = database.getConnection();
    IDBPreparedStatement staleStatement = connection.prepareStatement("SELECT 1", ReuseProbability.HIGH);
    staleStatement.close();

    IDBPreparedStatement activeStatement = connection.prepareStatement("SELECT 1", ReuseProbability.HIGH);
    assertNotSame(staleStatement, activeStatement);
    staleStatement.close();

    Worker writer = startSchemaWriter("TABLE1");
    awaitWaiting(writer);

    activeStatement.close();
    awaitSuccess(writer);
  }

  /**
   * Concurrent closes of the same checkout decrement the connection's shared access count only once.
   */
  public void testConcurrentStatementClosesReleaseOnce() throws Exception
  {
    IDBConnection connection = database.getConnection();
    IDBPreparedStatement statement1 = connection.prepareStatement("SELECT 1", ReuseProbability.HIGH);
    IDBPreparedStatement statement2 = connection.prepareStatement("SELECT 2", ReuseProbability.HIGH);
    CountDownLatch start = new CountDownLatch(1);

    Worker close1 = start("close-1", () -> {
      start.await();
      statement1.close();
    });
    Worker close2 = start("close-2", () -> {
      start.await();
      statement1.close();
    });
    start.countDown();
    awaitSuccess(close1);
    awaitSuccess(close2);

    Worker writer = startSchemaWriter("TABLE1");
    awaitWaiting(writer);

    statement2.close();
    awaitSuccess(writer);
  }

  private void assertInactiveToken(Object token)
  {
    try
    {
      database.endSchemaAccess(token);
      fail("IllegalStateException expected");
    }
    catch (IllegalStateException expected)
    {
      // SUCCESS
    }
  }

  protected Worker startSchemaWriter(String tableName)
  {
    return start("schema-writer-" + tableName, () -> {
      IDBConnection connection = database.getConnection();

      try
      {
        IDBSchemaTransaction schemaTransaction = connection.openSchemaTransaction();

        try
        {
          IDBTable table = schemaTransaction.getWorkingCopy().addTable(tableName);
          table.addField("ID", DBType.INTEGER);
          schemaTransaction.commit();
        }
        finally
        {
          schemaTransaction.close();
        }
      }
      finally
      {
        connection.close();
      }
    });
  }

  protected static Worker start(String name, Task task)
  {
    Worker worker = new Worker(name, task);
    worker.start();
    return worker;
  }

  protected static void awaitWaiting(Thread thread) throws InterruptedException
  {
    long end = System.currentTimeMillis() + WAIT_MILLIS;

    for (;;)
    {
      Thread.State state = thread.getState();
      if (state == Thread.State.WAITING || state == Thread.State.TIMED_WAITING)
      {
        for (StackTraceElement frame : thread.getStackTrace())
        {
          if (DBDatabase.class.getName().equals(frame.getClassName()) && "beginSchemaAccess".equals(frame.getMethodName())) //$NON-NLS-1$
          {
            return;
          }
        }
      }

      if (state == Thread.State.TERMINATED)
      {
        fail(thread.getName() + " did not wait for schema access");
      }

      if (System.currentTimeMillis() > end)
      {
        fail(thread.getName() + " did not start waiting for schema access");
      }

      Thread.sleep(10L);
    }
  }

  protected static void awaitSuccess(Worker worker) throws InterruptedException
  {
    worker.join(WAIT_MILLIS);
    assertFalse(worker.getName() + " did not finish", worker.isAlive());

    Throwable failure = worker.getFailure();
    if (failure != null)
    {
      AssertionFailedError error = new AssertionFailedError(worker.getName() + " failed: " + failure);
      error.initCause(failure);
      throw error;
    }
  }

  protected static void awaitInterrupted(Worker worker) throws InterruptedException
  {
    worker.join(WAIT_MILLIS);
    assertFalse(worker.getName() + " did not finish", worker.isAlive());

    Throwable failure = worker.getFailure();

    for (Throwable cause = failure; cause != null; cause = cause.getCause())
    {
      if (cause instanceof InterruptedException)
      {
        return;
      }
    }

    AssertionFailedError error = new AssertionFailedError(worker.getName() + " was not interrupted: " + failure);
    error.initCause(failure);
    throw error;
  }

  private static Connection createConnection()
  {
    Connection[] connection = { null };
    connection[0] = (Connection)createStub(Connection.class, connection);
    return connection[0];
  }

  /**
   * Returns a stub of a JDBC interface. Nested JDBC objects are stubs, too, result sets are empty and updates do
   * nothing.
   */
  private static Object createStub(Class<?> type, Connection[] connection)
  {
    InvocationHandler handler = (proxy, method, args) -> {
      switch (method.getName())
      {
      case "equals":
        return proxy == args[0];

      case "hashCode":
        return System.identityHashCode(proxy);

      case "toString":
        return type.getSimpleName() + "Stub";

      case "getConnection":
        return connection[0];

      default:
        break;
      }

      Class<?> returnType = method.getReturnType();
      if (returnType.isPrimitive())
      {
        return returnType == void.class ? null : Array.get(Array.newInstance(returnType, 1), 0);
      }

      if (returnType == String.class)
      {
        return "";
      }

      if (returnType.isArray())
      {
        return Array.newInstance(returnType.getComponentType(), 0);
      }

      if (returnType.isInterface() && returnType.getName().startsWith("java.sql."))
      {
        return createStub(returnType, connection);
      }

      return null;
    };

    return Proxy.newProxyInstance(DBSchemaAccessTest.class.getClassLoader(), new Class<?>[] { type }, handler);
  }

  /**
   * Work that a {@link Worker} runs; it may throw checked JDBC and interruption exceptions.
   *
   * @author Eike Stepper
   */
  @FunctionalInterface
  protected interface Task
  {
    public void run() throws Exception;
  }

  /**
   * A daemon thread that runs a {@link Task} and remembers its failure for the test thread.
   *
   * @author Eike Stepper
   */
  protected static final class Worker extends Thread
  {
    private final Task task;

    private volatile Throwable failure;

    public Worker(String name, Task task)
    {
      super(name);
      this.task = task;
      setDaemon(true);
    }

    public Throwable getFailure()
    {
      return failure;
    }

    @Override
    public void run()
    {
      try
      {
        task.run();
      }
      catch (Throwable ex)
      {
        failure = ex;
      }
    }
  }

  /**
   * A generic adapter that sends its DDL to the stub connections.
   *
   * @author Eike Stepper
   */
  private static final class TestAdapter extends DBAdapter
  {
    public TestAdapter()
    {
      super("TEST", "1.0");
    }

    @Override
    public String[] getReservedWords()
    {
      return new String[0];
    }
  }
}
