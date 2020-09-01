/*
 * Copyright (c) 2010-2013, 2016, 2019, 2020 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.db;

import org.eclipse.emf.cdo.server.db.IDBStore;
import org.eclipse.emf.cdo.server.internal.db.DBStore;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;

import org.eclipse.net4j.db.DBType;
import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.IDBDatabase;
import org.eclipse.net4j.db.ddl.IDBSchema;
import org.eclipse.net4j.db.ddl.IDBTable;
import org.eclipse.net4j.db.mysql.MYSQLAdapter;
import org.eclipse.net4j.spi.db.DBAdapter;
import org.eclipse.net4j.util.collection.Pair;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;
import org.eclipse.net4j.util.io.ExtendedIOUtil;
import org.eclipse.net4j.util.io.IOUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;

/**
 * @author Eike Stepper
 */
public class Net4jDBTest extends AbstractCDOTest
{
  private static final String FIELD_NAME = "testField";

  private transient ArrayList<Pair<DBType, Object>> columns = new ArrayList<>();

  @Override
  protected void doTearDown() throws Exception
  {
    columns.clear();
    super.doTearDown();
  }

  public void testBigInt() throws Exception
  {
    registerColumn(DBType.BIGINT, Long.MAX_VALUE);
    registerColumn(DBType.BIGINT, Long.MIN_VALUE);
    registerColumn(DBType.BIGINT, 0L);
    registerColumn(DBType.BIGINT, 42L);
    doTest(getName());
  }

  public void testBinary() throws Exception
  {
    if (!isDB("oracle", "mysql"))
    {
      registerColumn(DBType.BINARY, new byte[0]);
    }

    if (!isDB("mysql"))
    {
      byte[] data = new byte[100];
      for (int i = 0; i < data.length; i++)
      {
        data[i] = (byte)(Math.random() * (Byte.MAX_VALUE - Byte.MIN_VALUE) + Byte.MIN_VALUE);
      }

      registerColumn(DBType.BINARY, data);
    }

    doTest(getName());
  }

  public void testVarBinary() throws Exception
  {
    if (!isDB("oracle"))
    {
      registerColumn(DBType.VARBINARY, new byte[0]);
    }

    byte[] data = new byte[100];
    for (int i = 0; i < data.length; i++)
    {
      data[i] = (byte)(Math.random() * (Byte.MAX_VALUE - Byte.MIN_VALUE) + Byte.MIN_VALUE);
    }

    registerColumn(DBType.VARBINARY, data);
    doTest(getName());
  }

  public void testLongVarBinary() throws Exception
  {
    if (!isDB("oracle"))
    {
      registerColumn(DBType.LONGVARBINARY, new byte[0]);
    }

    byte[] data = new byte[100];
    for (int i = 0; i < data.length; i++)
    {
      data[i] = (byte)(Math.random() * (Byte.MAX_VALUE - Byte.MIN_VALUE) + Byte.MIN_VALUE);
    }

    registerColumn(DBType.LONGVARBINARY, data);
    doTest(getName());
  }

  public void testBit() throws Exception
  {
    registerColumn(DBType.BIT, true);
    registerColumn(DBType.BIT, false);
    doTest(getName());
  }

  public void testBlob() throws Exception
  {
    byte[] data = new byte[1000000];
    for (int i = 0; i < data.length; i++)
    {
      data[i] = (byte)(Math.random() * (Byte.MAX_VALUE - Byte.MIN_VALUE) + Byte.MIN_VALUE);
    }

    registerColumn(DBType.BLOB, data);
    doTest(getName());
  }

  @Skips("oracle")
  public void testBlobLength0() throws Exception
  {
    registerColumn(DBType.BLOB, new byte[0]);
    doTest(getName());
  }

  public void testBoolean() throws Exception
  {
    registerColumn(DBType.BOOLEAN, true);
    registerColumn(DBType.BOOLEAN, false);
    doTest(getName());
  }

  public void testChar() throws Exception
  {
    registerColumn(DBType.CHAR, "0");
    registerColumn(DBType.CHAR, "a");

    if (!isDB("db2"))
    {
      registerColumn(DBType.CHAR, "\377");
    }

    if (!isDB("db2", "mysql"))
    {
      registerColumn(DBType.CHAR, "\u1234");
    }

    doTest(getName());
  }

  public void testClob() throws Exception
  {
    registerColumn(DBType.CLOB, "Test");

    StringBuilder b = new StringBuilder();
    for (int i = 0; i < 1000000; i++)
    {
      b.append("x");
    }

    registerColumn(DBType.CLOB, b.toString());
    doTest(getName());
  }

  public void testClobLength0() throws Exception
  {
    registerColumn(DBType.CLOB, "");
    doTest(getName());
  }

  public void testTinyInt() throws Exception
  {
    registerColumn(DBType.TINYINT, Byte.MAX_VALUE);
    registerColumn(DBType.TINYINT, Byte.MIN_VALUE);
    registerColumn(DBType.TINYINT, Byte.valueOf("0"));
    registerColumn(DBType.TINYINT, Integer.valueOf(42).byteValue());
    doTest(getName());
  }

  public void testSmallInt() throws Exception
  {
    registerColumn(DBType.SMALLINT, Short.MAX_VALUE);
    registerColumn(DBType.SMALLINT, Short.MIN_VALUE);
    registerColumn(DBType.SMALLINT, (short)-1);
    registerColumn(DBType.SMALLINT, (short)5);
    doTest(getName());
  }

  public void testInteger() throws Exception
  {
    registerColumn(DBType.INTEGER, Integer.MAX_VALUE);
    registerColumn(DBType.INTEGER, Integer.MIN_VALUE);
    registerColumn(DBType.INTEGER, -1);
    registerColumn(DBType.INTEGER, 5);
    doTest(getName());
  }

  public void testFloat() throws Exception
  {
    if (!isDB("mysql"))
    {
      registerColumn(DBType.FLOAT, Float.MAX_VALUE);
    }

    if (!isDB("db2"))
    {
      registerColumn(DBType.FLOAT, Float.MIN_VALUE);
    }

    registerColumn(DBType.FLOAT, -.1f);
    registerColumn(DBType.FLOAT, 3.33333f);
    doTest(getName());
  }

  public void testReal() throws Exception
  {
    registerColumn(DBType.REAL, Float.MAX_VALUE);

    if (!isDB("db2"))
    {
      registerColumn(DBType.REAL, Float.MIN_VALUE);
    }

    registerColumn(DBType.REAL, -.1f);
    registerColumn(DBType.REAL, 3.33333f);
    doTest(getName());
  }

  public void testDouble() throws Exception
  {
    if (!isDB("oracle"))
    {
      registerColumn(DBType.DOUBLE, Double.valueOf(Double.MAX_VALUE));
    }

    // registerColumn(DBType.DOUBLE, Double.valueOf(Double.MIN_VALUE));
    registerColumn(DBType.DOUBLE, -.1d);
    registerColumn(DBType.DOUBLE, 3.33333d);
    doTest(getName());
  }

  public void _testNumeric() throws Exception
  {
    String numberLiteral1 = "12345678901234567890123456789012";
    String numberLiteral2 = "10000000000000000000000000000000";

    for (int precision = 1; precision < 32; precision++)
    {
      BigInteger numberInteger1 = new BigInteger(numberLiteral1.substring(0, precision));
      BigInteger numberInteger2 = new BigInteger(numberLiteral2.substring(0, precision));

      for (int scale = 0; scale <= precision; scale++)
      {
        BigDecimal numberDecimal1 = new BigDecimal(numberInteger1, scale);
        BigDecimal numberDecimal2 = new BigDecimal(numberInteger2, scale);

        registerColumn(DBType.NUMERIC, numberDecimal1);
        registerColumn(DBType.NUMERIC, numberDecimal2);

        doTest(getName() + precision + "_" + scale);
        columns.clear();
      }
    }
  }

  public void _testDecimal() throws Exception
  {
    String numberLiteral1 = "12345678901234567890123456789012";
    String numberLiteral2 = "10000000000000000000000000000000";

    for (int precision = 1; precision < 32; precision++)
    {
      BigInteger numberInteger1 = new BigInteger(numberLiteral1.substring(0, precision));
      BigInteger numberInteger2 = new BigInteger(numberLiteral2.substring(0, precision));

      for (int scale = 0; scale <= precision; scale++)
      {
        BigDecimal numberDecimal1 = new BigDecimal(numberInteger1, scale);
        BigDecimal numberDecimal2 = new BigDecimal(numberInteger2, scale);

        registerColumn(DBType.DECIMAL, numberDecimal1);
        registerColumn(DBType.DECIMAL, numberDecimal2);

        doTest(getName() + precision + "_" + scale);
        columns.clear();
      }
    }
  }

  public void testVarChar() throws Exception
  {
    registerColumn(DBType.VARCHAR, "");
    // registerColumn(DBType.VARCHAR, null);
    // registerColumn(DBType.VARCHAR, " ");
    // registerColumn(DBType.VARCHAR, "\n");
    // registerColumn(DBType.VARCHAR, "\t");
    // registerColumn(DBType.VARCHAR, "\r");
    // registerColumn(DBType.VARCHAR, "\u1234");
    // registerColumn(DBType.VARCHAR, "The quick brown fox jumps over the lazy dog.");
    // registerColumn(DBType.VARCHAR, "\\,:\",\'");

    doTest(getName());
  }

  public void testLongVarChar() throws Exception
  {
    registerColumn(DBType.LONGVARCHAR, "");

    if (!isDB("oracle")) // Only 1 LONGVARCHAR allowed per table
    {
      registerColumn(DBType.LONGVARCHAR, "\n");
      registerColumn(DBType.LONGVARCHAR, "\t");
      registerColumn(DBType.LONGVARCHAR, "\r");

      if (!isDB(MYSQLAdapter.NAME))
      {
        registerColumn(DBType.LONGVARCHAR, "\u1234");
      }

      registerColumn(DBType.LONGVARCHAR, "The quick brown fox jumps over the lazy dog.");
      registerColumn(DBType.LONGVARCHAR, "\\,:\",\'");
    }

    doTest(getName());
  }

  public void testDate() throws Exception
  {
    registerColumn(DBType.DATE, new GregorianCalendar(2010, 04, 21).getTimeInMillis());
    registerColumn(DBType.DATE, new GregorianCalendar(1950, 04, 21).getTimeInMillis());
    registerColumn(DBType.DATE, new GregorianCalendar(2030, 12, 31).getTimeInMillis());

    if (!isDB("oracle", "db2", "mysql"))
    {
      registerColumn(DBType.DATE, new GregorianCalendar(0, 0, 0).getTimeInMillis());
    }

    doTest(getName());
  }

  public void testTime() throws Exception
  {
    registerColumn(DBType.TIME, HOURS_toMillis(10));
    registerColumn(DBType.TIME, 0l);
    registerColumn(DBType.TIME, HOURS_toMillis(11) + MINUTES_toMillis(59) + TimeUnit.SECONDS.toMillis(59));

    // Following tests fail on H2 as 24h == 1 day => 0
    registerColumn(DBType.TIME, HOURS_toMillis(24));

    doTest(getName());
  }

  public void testTimestamp() throws Exception
  {
    registerColumn(DBType.TIME, HOURS_toMillis(10));
    registerColumn(DBType.TIME, 0l);
    registerColumn(DBType.TIME, HOURS_toMillis(11) + MINUTES_toMillis(59) + TimeUnit.SECONDS.toMillis(59));

    // Following tests fail on H2 as 24h == 1 day => 0
    registerColumn(DBType.TIME, HOURS_toMillis(24));

    doTest(getName());
  }

  private void registerColumn(DBType type, Object value) throws IOException
  {
    testIOSymmetry(type, value);

    Pair<DBType, Object> column = Pair.create(type, value);
    columns.add(column);
  }

  private void testIOSymmetry(DBType type, Object value) throws IOException
  {
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    ExtendedDataOutputStream outs = new ExtendedDataOutputStream(output);
    writeTypeValue(outs, type, value);
    outs.close();
    output.flush();
    byte[] buffer = output.toByteArray();
    output.close();

    ByteArrayInputStream input = new ByteArrayInputStream(buffer);
    ExtendedDataInputStream ins = new ExtendedDataInputStream(input);
    Object actualValue = readTypeValue(ins, type);
    assertEquals(value, actualValue, type, -1);
  }

  private void prepareTable(DBStore store, final String tableName)
  {
    IDBDatabase database = store.getDatabase();
    database.updateSchema(new IDBDatabase.RunnableWithSchema()
    {
      @Override
      public void run(IDBSchema schema)
      {
        IDBTable table = schema.addTable(tableName);
        int c = 0;
        for (Pair<DBType, Object> column : columns)
        {
          switch (column.getElement1())
          {
          case NUMERIC:
          case DECIMAL:
            BigDecimal value = (BigDecimal)column.getElement2();
            table.addField(FIELD_NAME + c++, column.getElement1(), value.precision(), value.scale());
            break;

          case BINARY:
            table.addField(FIELD_NAME + c++, column.getElement1(), 200); // Needed for testBinary().
            break;

          default:
            table.addField(FIELD_NAME + c++, column.getElement1());
            break;
          }
        }
      }
    });
  }

  private void writeValues(Connection connection, String tableName) throws Exception
  {
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    ExtendedDataOutputStream outs = new ExtendedDataOutputStream(output);

    boolean first = true;
    StringBuilder builder = new StringBuilder("INSERT INTO " + tableName + " VALUES (");
    for (Pair<DBType, Object> column : columns)
    {
      Object value = column.getElement2();
      if (value instanceof String)
      {
        String str = (String)value;

        DBStore store = (DBStore)getRepository().getStore();
        IDBDatabase database = store.getDatabase();
        DBAdapter adapter = (DBAdapter)database.getAdapter();
        value = adapter.convertString((PreparedStatement)null, 0, str);
      }

      writeTypeValue(outs, column.getElement1(), value);
      if (first)
      {
        builder.append("?");
        first = false;
      }
      else
      {
        builder.append(", ?");
      }
    }

    builder.append(")");
    String sql = builder.toString();

    outs.close();
    output.flush();
    byte[] buffer = output.toByteArray();
    output.close();

    ByteArrayInputStream input = new ByteArrayInputStream(buffer);
    ExtendedDataInputStream ins = new ExtendedDataInputStream(input);

    PreparedStatement stmt = connection.prepareStatement(sql);
    int c = 1;

    for (Pair<DBType, Object> column : columns)
    {
      column.getElement1().readValueWithResult(ins, stmt, c++, false);
    }

    stmt.executeUpdate();

    stmt.close();
    ins.close();
    input.close();
  }

  private void checkValues(Connection connection, String tableName) throws Exception
  {
    Statement stmt = connection.createStatement();
    ResultSet resultSet = stmt.executeQuery("SELECT * FROM " + tableName);
    assertEquals(true, resultSet.next());

    ByteArrayOutputStream output = new ByteArrayOutputStream();
    ExtendedDataOutputStream outs = new ExtendedDataOutputStream(output);

    int c = 1;
    for (Pair<DBType, Object> column : columns)
    {
      DBType dbType = column.getElement1();
      dbType.writeValue(outs, resultSet, c++, false);
    }

    resultSet.close();
    stmt.close();

    outs.close();
    output.flush();
    byte[] buffer = output.toByteArray();
    output.close();

    ByteArrayInputStream input = new ByteArrayInputStream(buffer);
    ExtendedDataInputStream ins = new ExtendedDataInputStream(input);

    c = 1;
    for (Pair<DBType, Object> column : columns)
    {
      DBType dbType = column.getElement1();
      Object expected = column.getElement2();

      Object actual = readTypeValue(ins, dbType);
      assertEquals(expected, actual, dbType, c++);
    }
  }

  private void assertEquals(Object expected, Object actual, DBType dbType, int c)
  {
    if (expected == null || actual == null)
    {
      assertEquals("Error in column " + c + " with type " + dbType, expected, actual);
      return;
    }

    Class<? extends Object> type = expected.getClass();
    if (type.isArray())
    {
      Class<?> componentType = type.getComponentType();
      if (componentType == byte.class)
      {
        assertEquals("Error in column " + c + " of type " + dbType, true, Arrays.equals((byte[])expected, (byte[])actual));
      }
      else if (componentType == char.class)
      {
        assertEquals("Error in column " + c + " with type " + dbType, true, Arrays.equals((char[])expected, (char[])actual));
      }
      else
      {
        throw new IllegalStateException("Unexpected component type: " + componentType);
      }
    }
    else
    {
      if (dbType == DBType.TIME)
      {
        actual = (Long)actual % 86400000L;
        expected = (Long)expected % 86400000L;
      }

      assertEquals("Error in column " + c + " with type " + dbType, expected, actual);
    }
  }

  private void doTest(String tableName) throws Exception
  {
    if (columns.isEmpty())
    {
      return;
    }

    DBStore store = (DBStore)getRepository().getStore();
    Connection connection = store.getConnection();

    try
    {
      prepareTable(store, tableName);
      writeValues(connection, tableName);
      checkValues(connection, tableName);
    }
    finally
    {
      try
      {
        connection.commit();
      }
      finally
      {
        DBUtil.close(connection);
        connection = null;
        store = null;
      }
    }
  }

  private void writeTypeValue(ExtendedDataOutputStream outs, DBType type, Object value) throws IOException
  {
    switch (type)
    {
    case BOOLEAN:
    case BIT:
      outs.writeBoolean((Boolean)value);
      return;

    case TINYINT:
      outs.writeByte((Byte)value);
      return;

    case CHAR:
      outs.writeString((String)value);
      return;

    case SMALLINT:
      outs.writeShort((Short)value);
      return;

    case INTEGER:
      outs.writeInt((Integer)value);
      return;

    case FLOAT:
      outs.writeFloat((Float)value);
      return;

    case REAL:
      outs.writeFloat((Float)value);
      return;

    case DOUBLE:
      outs.writeDouble((Double)value);
      return;

    case NUMERIC:
    case DECIMAL:
    {
      BigDecimal bigDecimal = (BigDecimal)value;
      outs.writeByteArray(bigDecimal.unscaledValue().toByteArray());
      outs.writeInt(bigDecimal.scale());
      return;
    }

    case VARCHAR:
    case LONGVARCHAR:
      outs.writeString((String)value);
      return;

    case CLOB:
      ExtendedIOUtil.writeCharacterStream(outs, new StringReader((String)value));
      return;

    case BIGINT:
    case DATE:
    case TIME:
    case TIMESTAMP:
      outs.writeLong((Long)value);
      return;

    case BINARY:
    case VARBINARY:
    case LONGVARBINARY:
      outs.writeByteArray((byte[])value);
      return;

    case BLOB:
      ExtendedIOUtil.writeBinaryStream(outs, new ByteArrayInputStream((byte[])value));
      return;

    default:
      throw new UnsupportedOperationException("not implemented");
    }
  }

  private Object readTypeValue(ExtendedDataInputStream ins, DBType type) throws IOException
  {
    switch (type)
    {
    case BOOLEAN:
    case BIT:
      return ins.readBoolean();

    case CHAR:
      return ins.readString();

    case TINYINT:
      return ins.readByte();

    case SMALLINT:
      return ins.readShort();

    case INTEGER:
      return ins.readInt();

    case FLOAT:
    case REAL:
      return ins.readFloat();

    case DOUBLE:
      return ins.readDouble();

    case NUMERIC:
    case DECIMAL:
    {
      byte[] array = ins.readByteArray();
      if (array == null)
      {
        return null;
      }

      BigInteger unscaled = new BigInteger(array);
      int scale = ins.readInt();
      return new BigDecimal(unscaled, scale);
    }

    case VARCHAR:
    case LONGVARCHAR:
      DBStore store = (DBStore)getRepository().getStore();
      IDBDatabase database = store.getDatabase();
      DBAdapter adapter = (DBAdapter)database.getAdapter();
      return adapter.convertString((ResultSet)null, 0, ins.readString());

    case CLOB:
    {
      StringWriter result = new StringWriter();

      try
      {
        ExtendedIOUtil.readCharacterStream(ins, result);
      }
      finally
      {
        IOUtil.close(result);
      }

      return result.toString();
    }

    case DATE:
    case BIGINT:
    case TIME:
    case TIMESTAMP:
      return ins.readLong();

    case BINARY:
    case VARBINARY:
    case LONGVARBINARY:
      return ins.readByteArray();

    case BLOB:
    {
      ByteArrayOutputStream result = new ByteArrayOutputStream();

      try
      {
        ExtendedIOUtil.readBinaryStream(ins, result);
      }
      finally
      {
        IOUtil.close(result);
      }

      return result.toByteArray();
    }

    default:
      throw new UnsupportedOperationException("not implemented");
    }
  }

  private long HOURS_toMillis(int hours)
  {
    return MINUTES_toMillis(60 * hours);
  }

  private long MINUTES_toMillis(int minutes)
  {
    return 1000L * 60L * minutes;
  }

  private boolean isDB(String... dbs)
  {
    for (String db : dbs)
    {
      if (db.equalsIgnoreCase(((IDBStore)getRepository().getStore()).getDBAdapter().getName()))
      {
        return true;
      }
    }

    return false;
  }
}
