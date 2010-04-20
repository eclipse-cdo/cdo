/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.db;

import org.eclipse.emf.cdo.server.internal.db.DBStore;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;

import org.eclipse.net4j.db.DBType;
import org.eclipse.net4j.db.ddl.IDBSchema;
import org.eclipse.net4j.db.ddl.IDBTable;
import org.eclipse.net4j.util.collection.Pair;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;
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

/**
 * @author Eike Stepper
 */
public class Net4jDBTest extends AbstractCDOTest
{
  private static final String TABLE_NAME = "testTable";

  private static final String FIELD_NAME = "testField";

  private DBStore store;

  private ArrayList<Pair<DBType, Object>> columns = new ArrayList<Pair<DBType, Object>>();

  private Connection connection;

  public void testBigInt() throws Exception
  {
    registerColumn(DBType.BIGINT, Long.MAX_VALUE);
    registerColumn(DBType.BIGINT, Long.MIN_VALUE);
    registerColumn(DBType.BIGINT, 0L);
    registerColumn(DBType.BIGINT, 42L);
    doTest();
  }

  public void testBinary() throws Exception
  {
    registerColumn(DBType.BINARY, new byte[0]);

    byte[] data = new byte[100];
    for (int i = 0; i < data.length; i++)
    {
      data[i] = (byte)(Math.random() * (Byte.MAX_VALUE - Byte.MIN_VALUE) + Byte.MIN_VALUE);
    }

    registerColumn(DBType.BINARY, data);
    doTest();
  }

  public void testBit() throws Exception
  {
    registerColumn(DBType.BIT, true);
    registerColumn(DBType.BIT, false);
    doTest();
  }

  public void testBlob() throws Exception
  {
    registerColumn(DBType.BLOB, new byte[0]);

    byte[] data = new byte[1000000];
    for (int i = 0; i < data.length; i++)
    {
      data[i] = (byte)(Math.random() * (Byte.MAX_VALUE - Byte.MIN_VALUE) + Byte.MIN_VALUE);
    }

    registerColumn(DBType.BLOB, data);
    doTest();
  }

  public void testBoolean() throws Exception
  {
    registerColumn(DBType.BOOLEAN, true);
    registerColumn(DBType.BOOLEAN, false);
    doTest();
  }

  public void testChar() throws Exception
  {
    registerColumn(DBType.CHAR, '\0');
    registerColumn(DBType.CHAR, 'a');
    registerColumn(DBType.CHAR, '\255');
    registerColumn(DBType.CHAR, '\u1234');
    doTest();
  }

  public void testClob() throws Exception
  {
    registerColumn(DBType.CLOB, "");
    registerColumn(DBType.CLOB, "Test");

    StringBuffer b = new StringBuffer();
    for (int i = 0; i < 1000000; i++)
    {
      b.append("x");
    }

    registerColumn(DBType.CLOB, b.toString());
    doTest();
  }

  // TODO and so on for all DBTypes....

  private void registerColumn(DBType type, Object value)
  {
    Pair<DBType, Object> column = new Pair<DBType, Object>(type, value);
    columns.add(column);
  }

  private void prepareTable()
  {
    IDBSchema schema = store.getDBSchema();
    IDBTable table = schema.addTable(TABLE_NAME);
    int c = 0;

    for (Pair<DBType, Object> column : columns)
    {
      table.addField(FIELD_NAME + c++, column.getElement1());
    }

    store.getDBAdapter().createTables(Arrays.asList(table), connection);
  }

  private void writeValues() throws Exception
  {
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    ExtendedDataOutputStream outs = new ExtendedDataOutputStream(output);

    boolean first = true;
    StringBuilder builder = new StringBuilder("INSERT INTO " + TABLE_NAME + " VALUES (");
    for (Pair<DBType, Object> column : columns)
    {
      writeTypeValue(outs, column.getElement1(), column.getElement2());
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
      column.getElement1().readValue(ins, stmt, c++);
    }

    stmt.executeUpdate();

    stmt.close();
    ins.close();
    input.close();
  }

  private void checkValues() throws Exception
  {
    Statement stmt = connection.createStatement();
    ResultSet resultSet = stmt.executeQuery("SELECT * FROM " + TABLE_NAME);
    assertTrue(resultSet.next());

    ByteArrayOutputStream output = new ByteArrayOutputStream();
    ExtendedDataOutputStream outs = new ExtendedDataOutputStream(output);

    int c = 1;
    for (Pair<DBType, Object> column : columns)
    {
      column.getElement1().writeValue(outs, resultSet, c++);
    }

    resultSet.close();
    stmt.close();

    outs.close();
    output.flush();
    byte[] buffer = output.toByteArray();
    output.close();

    ByteArrayInputStream input = new ByteArrayInputStream(buffer);
    ExtendedDataInputStream ins = new ExtendedDataInputStream(input);

    for (Pair<DBType, Object> column : columns)
    {
      Object actual = readTypeValue(ins, column.getElement1());
      Class<? extends Object> type = column.getElement2().getClass();
      if (type.isArray())
      {
        Class<?> componentType = type.getComponentType();
        if (componentType == byte.class)
        {
          assertEquals("Error with type " + column.getElement1(), true, Arrays.equals((byte[])column.getElement2(),
              (byte[])actual));
        }
        else if (componentType == char.class)
        {
          assertEquals("Error with type " + column.getElement1(), true, Arrays.equals((char[])column.getElement2(),
              (char[])actual));
        }
        else
        {
          throw new IllegalStateException("Unexpected component type: " + componentType);
        }
      }
      else
      {
        assertEquals("Error with type " + column.getElement1(), column.getElement2(), actual);
      }
    }
  }

  private void doTest() throws Exception
  {
    store = (DBStore)getRepository().getStore();
    connection = store.getDBConnectionProvider().getConnection();

    prepareTable();
    writeValues();
    checkValues();

    connection = null;
    store = null;
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
      outs.writeChar((Character)value);
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
    {
      long length = ((String)value).length();
      StringReader source = new StringReader((String)value);
      try
      {
        outs.writeLong(length);
        while (length-- > 0)
        {
          int c = source.read();
          outs.writeChar(c);
        }
      }
      finally
      {
        IOUtil.close(source);
      }

      return;
    }

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
    {
      long length = ((byte[])value).length;
      ByteArrayInputStream source = new ByteArrayInputStream((byte[])value);
      try
      {
        outs.writeLong(length);
        while (length-- > 0)
        {
          int b = source.read();
          outs.writeByte(b + Byte.MIN_VALUE);
        }
      }
      finally
      {
        IOUtil.close(source);
      }

      return;
    }

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
      return ins.readChar();
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
      return ins.readString();

    case CLOB:
    {
      StringWriter result = new StringWriter();
      try
      {
        long length = ins.readLong();
        while (length-- > 0)
        {
          char c = ins.readChar();
          result.append(c);
        }
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
        long length = ins.readLong();
        while (length-- > 0)
        {
          int b = ins.readByte();
          result.write(b - Byte.MIN_VALUE);
        }
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

}
