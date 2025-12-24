/*
 * Copyright (c) 2009-2012, 2015, 2016 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.db.capabilities;

import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.IDBConnectionProvider;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import junit.framework.TestCase;

/**
 * This is a simple test case that can be used to analyze how a DBMS handles DML in the middle of a transaction.
 *
 * @author Stefan Winkler
 */
public abstract class AbstractCapabilityTest extends TestCase
{
  public AbstractCapabilityTest(String name)
  {
    super(name);
  }

  public void testDirtyRead() throws Exception
  {
    msg("TEST " + getClass().getSimpleName() + " - DIRTY READ");
    msg("----------------------------------------------------------");
    msg("Transaction 1 changes a value, transaction 2 will read the value.");
    msg("The value of transaction 2 should be UNCHANGED (else we have a dirty read)");

    Thread t = new Thread()
    {
      @Override
      public void run()
      {
        try
        {
          IDBConnectionProvider provider = getConnectionProvider();
          Connection transaction1 = provider.getConnection();
          transaction1.setAutoCommit(false);

          Statement tx1stmt = transaction1.createStatement();
          tx1stmt.executeUpdate("update status_table set status = 'changed' where trans = 'transaction1'");
          msg("Read value (transaction 1) is " + select(transaction1, "select status from status_table where trans = 'transaction1'").toUpperCase());
          sleep(1000);

          transaction1.rollback();
          transaction1.close();
        }
        catch (Exception e)
        {
          throw new Error(e);
        }
      }
    };

    t.start();
    Thread.sleep(300);

    IDBConnectionProvider provider = getConnectionProvider();
    Connection transaction2 = provider.getConnection();

    transaction2.setAutoCommit(false);

    msg("Read value (transaction 2) is " + select(transaction2, "select status from status_table where trans = 'transaction1'").toUpperCase());
    msg("----------------------------------------------------------");
    transaction2.rollback();
    transaction2.close();
  }

  public void testRollback() throws Exception
  {
    msg("TEST " + getClass().getSimpleName() + " - ROLLBACK");
    msg("----------------------------------------------------------");
    msg("Transaction changes a value and does a rollback.");
    msg("The value of after rollback should be UNCHANGED.");

    IDBConnectionProvider provider = getConnectionProvider();
    Connection transaction1 = provider.getConnection();
    transaction1.setAutoCommit(false);

    Statement tx1stmt = transaction1.createStatement();

    try
    {
      tx1stmt.executeUpdate("update status_table set status = 'changed' where trans = 'transaction1'");
    }
    finally
    {
      DBUtil.close(tx1stmt);
    }

    msg("Read value before rollback is " + select(transaction1, "select status from status_table where trans = 'transaction1'").toUpperCase());

    transaction1.rollback();
    transaction1.close();

    Connection view = provider.getConnection();
    msg("Read value after rollback is " + select(view, "select status from status_table where trans = 'transaction1'").toUpperCase());
    view.close();
    msg("----------------------------------------------------------");
  }

  public void testDml() throws Exception
  {
    msg("TEST " + getClass().getSimpleName() + " - DML");
    msg("----------------------------------------------------------");
    msg("Transaction 1 will execute DML, transaction 2 will just change its data.");

    Thread t = new Thread()
    {
      @Override
      public void run()
      {
        try
        {
          IDBConnectionProvider provider = getConnectionProvider();
          Connection transaction2 = provider.getConnection();
          Statement tx2stmt = transaction2.createStatement();
          transaction2.setAutoCommit(false);
          tx2stmt.executeUpdate("update status_table set status = 'changed' where trans = 'transaction2'");
          tx2stmt.executeUpdate("update change_table set status = 'changed' where trans = 'transaction2'");
          tx2stmt.close();
          sleep(1000);
          transaction2.rollback();
          transaction2.close();
        }
        catch (Exception e)
        {
          throw new Error(e);
        }
      }
    };

    t.start();
    Thread.sleep(100);

    IDBConnectionProvider provider = getConnectionProvider();
    Connection transaction1 = provider.getConnection();
    transaction1.setAutoCommit(false);

    Statement tx1stmt = transaction1.createStatement();

    tx1stmt.executeUpdate("update status_table set status = 'changed' where trans = 'transaction1'");
    tx1stmt.executeUpdate("update change_table set status = 'changed' where trans = 'transaction1'");

    tx1stmt.execute("alter table change_table add new_column varchar(255) default 'added column present'");

    tx1stmt.close();

    transaction1.rollback();
    transaction1.close();

    t.join();

    Connection view = provider.getConnection();

    msg("transaction1: unchanged table record is " + select(view, "select status from status_table where trans = 'transaction1'").toUpperCase());
    msg("transaction2: unchanged table record is " + select(view, "select status from status_table where trans = 'transaction2'").toUpperCase());

    msg("transaction1: changed table record is " + select(view, "select status from change_table where trans = 'transaction1'").toUpperCase());
    msg("transaction2: changed table record is " + select(view, "select status from change_table where trans = 'transaction2'").toUpperCase());

    String present = "present";
    try
    {
      select(view, "select new_column from change_table where trans = 'transaction2'");
    }
    catch (SQLException e)
    {
      present = "not present";
    }

    msg("Added column is " + present.toUpperCase());
    view.close();

    msg("----------------------------------------------------------");
  }

  @Override
  protected void setUp() throws Exception
  {
    // create table
    Connection conn = getConnectionProvider().getConnection();
    conn.setAutoCommit(false);
    Statement stmt = conn.createStatement();

    // make sure tables don't exist!
    try
    {
      stmt.execute("drop table status_table");
    }
    catch (Exception e)
    {
    }

    try
    {
      stmt.execute("drop table change_table");
    }
    catch (Exception e)
    {
    }

    stmt.execute("create table status_table (trans varchar(255), status varchar(255))");
    stmt.execute("insert into status_table values ('transaction1', 'unchanged')");
    stmt.execute("insert into status_table values ('transaction2', 'unchanged')");

    stmt.execute("create table change_table (trans varchar(255), status varchar(255))");
    stmt.execute("insert into change_table values ('transaction1', 'unchanged')");
    stmt.execute("insert into change_table values ('transaction2', 'unchanged')");

    conn.commit();
    stmt.close();
    conn.close();
  }

  @Override
  protected void tearDown() throws Exception
  {
    Connection conn = getConnectionProvider().getConnection();
    conn.setAutoCommit(true);
    Statement stmt = conn.createStatement();

    stmt.execute("drop table status_table");
    stmt.execute("drop table change_table");

    stmt.close();
    conn.close();
  }

  protected abstract IDBConnectionProvider getConnectionProvider();

  private void msg(String string)
  {
    System.out.println(string);
  }

  private String select(Connection conn, String sql) throws SQLException
  {
    ResultSet rs = null;
    try
    {
      rs = conn.createStatement().executeQuery(sql);
      rs.next();
      return rs.getString(1);
    }
    finally
    {
      if (rs != null)
      {
        try
        {
          rs.close();
        }
        catch (SQLException ex)
        {
          // NOP
        }
      }
    }
  }

  @SuppressWarnings("unused")
  private void sqlDump(Connection conn, String sql)
  {
    ResultSet rs = null;
    try
    {
      System.out.format("Dumping output of %s\n", sql); //$NON-NLS-1$
      rs = conn.createStatement().executeQuery(sql);
      int numCol = rs.getMetaData().getColumnCount();

      StringBuilder row = new StringBuilder(" ");
      for (int c = 1; c <= numCol; c++)
      {
        row.append(String.format("%15s | ", rs.getMetaData().getColumnLabel(c))); //$NON-NLS-1$
      }

      System.out.println(row.toString());

      row = new StringBuilder();
      for (int c = 1; c <= numCol; c++)
      {
        row.append("-----------------+"); //$NON-NLS-1$
      }

      System.out.println(row.toString());

      while (rs.next())
      {
        row = new StringBuilder(" ");
        for (int c = 1; c <= numCol; c++)
        {
          row.append(String.format("%15s | ", rs.getString(c))); //$NON-NLS-1$
        }

        System.out.println(row.toString());
      }

      row = new StringBuilder();
      for (int c = 1; c <= numCol; c++)
      {
        row.append("-----------------+"); //$NON-NLS-1$
      }

      System.out.println(row.toString());
    }
    catch (SQLException ex)
    {
      // NOP
    }
    finally
    {
      if (rs != null)
      {
        try
        {
          rs.close();
        }
        catch (SQLException ex)
        {
          // NOP
        }
      }
    }
  }
}
