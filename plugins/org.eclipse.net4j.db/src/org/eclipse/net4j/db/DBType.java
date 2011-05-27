/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Kai Schlamp - bug 282976: [DB] Influence Mappings through EAnnotations
 */
package org.eclipse.net4j.db;

import org.eclipse.net4j.util.io.ExtendedDataInput;
import org.eclipse.net4j.util.io.ExtendedDataOutput;
import org.eclipse.net4j.util.io.IOUtil;

import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Enumerates the SQL data types that are compatible with the DB framework.
 * 
 * @author Eike Stepper
 */
public enum DBType
{
  BOOLEAN(16)
  {
    @Override
    public void writeValue(ExtendedDataOutput out, ResultSet resultSet, int column, boolean canBeNull)
        throws SQLException, IOException
    {
      writeValueBoolean(out, resultSet, column, canBeNull);
    }

    @Override
    public void readValue(ExtendedDataInput in, PreparedStatement statement, int column, boolean canBeNull)
        throws SQLException, IOException
    {
      readValueBoolean(in, statement, column, canBeNull, getCode());
    }
  },

  BIT(-7)
  {
    @Override
    public void writeValue(ExtendedDataOutput out, ResultSet resultSet, int column, boolean canBeNull)
        throws SQLException, IOException
    {
      writeValueBoolean(out, resultSet, column, canBeNull);
    }

    @Override
    public void readValue(ExtendedDataInput in, PreparedStatement statement, int column, boolean canBeNull)
        throws SQLException, IOException
    {
      readValueBoolean(in, statement, column, canBeNull, getCode());
    }
  },

  TINYINT(-6)
  {
    @Override
    public void writeValue(ExtendedDataOutput out, ResultSet resultSet, int column, boolean canBeNull)
        throws SQLException, IOException
    {
      byte value = resultSet.getByte(column);
      if (canBeNull)
      {
        if (resultSet.wasNull())
        {
          out.writeBoolean(false);
          return;
        }

        out.writeBoolean(true);
      }

      out.writeByte(value);
    }

    @Override
    public void readValue(ExtendedDataInput in, PreparedStatement statement, int column, boolean canBeNull)
        throws SQLException, IOException
    {
      if (canBeNull && !in.readBoolean())
      {
        statement.setNull(column, getCode());
        return;
      }

      byte value = in.readByte();
      statement.setByte(column, value);
    }
  },

  SMALLINT(5)
  {
    @Override
    public void writeValue(ExtendedDataOutput out, ResultSet resultSet, int column, boolean canBeNull)
        throws SQLException, IOException
    {
      short value = resultSet.getShort(column);
      if (canBeNull)
      {
        if (resultSet.wasNull())
        {
          out.writeBoolean(false);
          return;
        }

        out.writeBoolean(true);
      }

      out.writeShort(value);
    }

    @Override
    public void readValue(ExtendedDataInput in, PreparedStatement statement, int column, boolean canBeNull)
        throws SQLException, IOException
    {
      if (canBeNull && !in.readBoolean())
      {
        statement.setNull(column, getCode());
        return;
      }

      short value = in.readShort();
      statement.setShort(column, value);
    }
  },

  INTEGER(4)
  {
    @Override
    public void writeValue(ExtendedDataOutput out, ResultSet resultSet, int column, boolean canBeNull)
        throws SQLException, IOException
    {
      int value = resultSet.getInt(column);
      if (canBeNull)
      {
        if (resultSet.wasNull())
        {
          out.writeBoolean(false);
          return;
        }

        out.writeBoolean(true);
      }

      out.writeInt(value);
    }

    @Override
    public void readValue(ExtendedDataInput in, PreparedStatement statement, int column, boolean canBeNull)
        throws SQLException, IOException
    {
      if (canBeNull && !in.readBoolean())
      {
        statement.setNull(column, getCode());
        return;
      }

      int value = in.readInt();
      statement.setInt(column, value);
    }
  },

  BIGINT(-5)
  {
    @Override
    public void writeValue(ExtendedDataOutput out, ResultSet resultSet, int column, boolean canBeNull)
        throws SQLException, IOException
    {
      long value = resultSet.getLong(column);
      if (canBeNull)
      {
        if (resultSet.wasNull())
        {
          out.writeBoolean(false);
          return;
        }

        out.writeBoolean(true);
      }

      out.writeLong(value);
    }

    @Override
    public void readValue(ExtendedDataInput in, PreparedStatement statement, int column, boolean canBeNull)
        throws SQLException, IOException
    {
      if (canBeNull && !in.readBoolean())
      {
        statement.setNull(column, getCode());
        return;
      }

      long value = in.readLong();
      statement.setLong(column, value);
    }
  },

  FLOAT(6)
  {
    @Override
    public void writeValue(ExtendedDataOutput out, ResultSet resultSet, int column, boolean canBeNull)
        throws SQLException, IOException
    {
      float value = resultSet.getFloat(column);
      if (canBeNull)
      {
        if (resultSet.wasNull())
        {
          out.writeBoolean(false);
          return;
        }

        out.writeBoolean(true);
      }

      out.writeFloat(value);
    }

    @Override
    public void readValue(ExtendedDataInput in, PreparedStatement statement, int column, boolean canBeNull)
        throws SQLException, IOException
    {
      if (canBeNull && !in.readBoolean())
      {
        statement.setNull(column, getCode());
        return;
      }

      float value = in.readFloat();
      statement.setFloat(column, value);
    }
  },

  REAL(7)
  {
    @Override
    public void writeValue(ExtendedDataOutput out, ResultSet resultSet, int column, boolean canBeNull)
        throws SQLException, IOException
    {
      float value = resultSet.getFloat(column);
      if (canBeNull)
      {
        if (resultSet.wasNull())
        {
          out.writeBoolean(false);
          return;
        }

        out.writeBoolean(true);
      }

      out.writeFloat(value);
    }

    @Override
    public void readValue(ExtendedDataInput in, PreparedStatement statement, int column, boolean canBeNull)
        throws SQLException, IOException
    {
      if (canBeNull && !in.readBoolean())
      {
        statement.setNull(column, getCode());
        return;
      }

      float value = in.readFloat();
      statement.setFloat(column, value);
    }
  },

  DOUBLE(8)
  {
    @Override
    public void writeValue(ExtendedDataOutput out, ResultSet resultSet, int column, boolean canBeNull)
        throws SQLException, IOException
    {
      double value = resultSet.getDouble(column);
      if (canBeNull)
      {
        if (resultSet.wasNull())
        {
          out.writeBoolean(false);
          return;
        }

        out.writeBoolean(true);
      }

      out.writeDouble(value);
    }

    @Override
    public void readValue(ExtendedDataInput in, PreparedStatement statement, int column, boolean canBeNull)
        throws SQLException, IOException
    {
      if (canBeNull && !in.readBoolean())
      {
        statement.setNull(column, getCode());
        return;
      }

      double value = in.readDouble();
      statement.setDouble(column, value);
    }
  },

  NUMERIC(2)
  {
    @Override
    public void writeValue(ExtendedDataOutput out, ResultSet resultSet, int column, boolean canBeNull)
        throws SQLException, IOException
    {
      throw new UnsupportedOperationException("SQL NULL has to be considered");
      // BigDecimal value = resultSet.getBigDecimal(column);
      // BigInteger valueUnscaled = value.unscaledValue();
      //
      // byte[] byteArray = valueUnscaled.toByteArray();
      // out.writeInt(byteArray.length);
      // out.write(byteArray);
      // out.writeInt(value.scale());
    }

    @Override
    public void readValue(ExtendedDataInput in, PreparedStatement statement, int column, boolean canBeNull)
        throws SQLException, IOException
    {
      throw new UnsupportedOperationException("SQL NULL has to be considered");
      // byte[] bytes = in.readByteArray();
      // int scale = in.readInt();
      // BigInteger valueUnscaled = new BigInteger(bytes);
      // BigDecimal value = new BigDecimal(valueUnscaled, scale);
      //
      // // TODO: Read out the precision, scale information and bring the big decimal to the correct form.
      // statement.setBigDecimal(column, value);
    }
  },

  DECIMAL(3)
  {
    @Override
    public void writeValue(ExtendedDataOutput out, ResultSet resultSet, int column, boolean canBeNull)
        throws SQLException, IOException
    {
      throw new UnsupportedOperationException("SQL NULL has to be considered");
      // BigDecimal value = resultSet.getBigDecimal(column);
      // BigInteger valueUnscaled = value.unscaledValue();
      //
      // byte[] byteArray = valueUnscaled.toByteArray();
      // out.writeInt(byteArray.length);
      // out.write(byteArray);
      // out.writeInt(value.scale());
    }

    @Override
    public void readValue(ExtendedDataInput in, PreparedStatement statement, int column, boolean canBeNull)
        throws SQLException, IOException
    {
      throw new UnsupportedOperationException("SQL NULL has to be considered");
      // byte[] bytes = in.readByteArray();
      // int scale = in.readInt();
      //
      // BigInteger valueUnscaled = new BigInteger(bytes);
      // BigDecimal value = new BigDecimal(valueUnscaled, scale);
      // statement.setBigDecimal(column, value);
    }
  },

  CHAR(1)
  {
    @Override
    public void writeValue(ExtendedDataOutput out, ResultSet resultSet, int column, boolean canBeNull)
        throws SQLException, IOException
    {
      String value = resultSet.getString(column);
      out.writeString(value);
    }

    @Override
    public void readValue(ExtendedDataInput in, PreparedStatement statement, int column, boolean canBeNull)
        throws SQLException, IOException
    {
      String value = in.readString();
      statement.setString(column, value);
    }
  },

  VARCHAR(12)
  {
    @Override
    public void writeValue(ExtendedDataOutput out, ResultSet resultSet, int column, boolean canBeNull)
        throws SQLException, IOException
    {
      String value = resultSet.getString(column);
      out.writeString(value);
    }

    @Override
    public void readValue(ExtendedDataInput in, PreparedStatement statement, int column, boolean canBeNull)
        throws SQLException, IOException
    {
      String value = in.readString();
      statement.setString(column, value);
    }
  },

  LONGVARCHAR(-1, "LONG VARCHAR") //$NON-NLS-1$
  {
    @Override
    public void writeValue(ExtendedDataOutput out, ResultSet resultSet, int column, boolean canBeNull)
        throws SQLException, IOException
    {
      String value = resultSet.getString(column);
      out.writeString(value);
    }

    @Override
    public void readValue(ExtendedDataInput in, PreparedStatement statement, int column, boolean canBeNull)
        throws SQLException, IOException
    {
      String value = in.readString();
      statement.setString(column, value);
    }
  },

  CLOB(2005)
  {
    @Override
    public void writeValue(ExtendedDataOutput out, ResultSet resultSet, int column, boolean canBeNull)
        throws SQLException, IOException
    {
      Clob value = resultSet.getClob(column);
      if (canBeNull)
      {
        if (resultSet.wasNull())
        {
          out.writeBoolean(false);
          return;
        }

        out.writeBoolean(true);
      }

      long length = value.length();
      Reader reader = value.getCharacterStream();

      try
      {
        out.writeLong(length);
        while (length-- > 0)
        {
          int c = reader.read();
          out.writeChar(c);
        }
      }
      finally
      {
        IOUtil.close(reader);
      }
    }

    @Override
    public void readValue(final ExtendedDataInput in, PreparedStatement statement, int column, boolean canBeNull)
        throws SQLException, IOException
    {
      if (canBeNull && !in.readBoolean())
      {
        statement.setNull(column, getCode());
        return;
      }

      Reader reader;

      long length = in.readLong();
      if (length > 0)
      {
        reader = new Reader()
        {
          @Override
          public int read(char[] cbuf, int off, int len) throws IOException
          {
            int read = 0;

            try
            {
              while (read < len)
              {
                cbuf[off++] = in.readChar();
                read++;
              }
            }
            catch (EOFException ex)
            {
              read = -1;
            }

            return read;
          }

          @Override
          public void close() throws IOException
          {
          }
        };
      }
      else
      {
        reader = new Reader()
        {
          @Override
          public int read(char[] cbuf, int off, int len) throws IOException
          {
            return -1;
          }

          @Override
          public void close() throws IOException
          {
          }
        };
      }

      statement.setCharacterStream(column, reader, (int)length);
      reader.close();
    }
  },

  DATE(91)
  {
    @Override
    public void writeValue(ExtendedDataOutput out, ResultSet resultSet, int column, boolean canBeNull)
        throws SQLException, IOException
    {
      java.sql.Date value = resultSet.getDate(column);
      if (canBeNull)
      {
        if (resultSet.wasNull())
        {
          out.writeBoolean(false);
          return;
        }

        out.writeBoolean(true);
      }

      out.writeLong(value.getTime());
    }

    @Override
    public void readValue(ExtendedDataInput in, PreparedStatement statement, int column, boolean canBeNull)
        throws SQLException, IOException
    {
      if (canBeNull && !in.readBoolean())
      {
        statement.setNull(column, getCode());
        return;
      }

      long value = in.readLong();
      statement.setDate(column, new java.sql.Date(value));
    }
  },

  TIME(92)
  {
    @Override
    public void writeValue(ExtendedDataOutput out, ResultSet resultSet, int column, boolean canBeNull)
        throws SQLException, IOException
    {
      java.sql.Time value = resultSet.getTime(column);
      if (canBeNull)
      {
        if (resultSet.wasNull())
        {
          out.writeBoolean(false);
          return;
        }

        out.writeBoolean(true);
      }

      out.writeLong(value.getTime());
    }

    @Override
    public void readValue(ExtendedDataInput in, PreparedStatement statement, int column, boolean canBeNull)
        throws SQLException, IOException
    {
      if (canBeNull && !in.readBoolean())
      {
        statement.setNull(column, getCode());
        return;
      }

      long value = in.readLong();
      statement.setTime(column, new java.sql.Time(value));
    }
  },

  TIMESTAMP(93)
  {
    @Override
    public void writeValue(ExtendedDataOutput out, ResultSet resultSet, int column, boolean canBeNull)
        throws SQLException, IOException
    {
      java.sql.Timestamp value = resultSet.getTimestamp(column);
      if (canBeNull)
      {
        if (resultSet.wasNull())
        {
          out.writeBoolean(false);
          return;
        }

        out.writeBoolean(true);
      }

      out.writeLong(value.getTime());
      out.writeInt(value.getNanos());
    }

    @Override
    public void readValue(ExtendedDataInput in, PreparedStatement statement, int column, boolean canBeNull)
        throws SQLException, IOException
    {
      if (canBeNull && !in.readBoolean())
      {
        statement.setNull(column, getCode());
        return;
      }

      long value = in.readLong();
      int nanos = in.readInt();
      java.sql.Timestamp timeStamp = new java.sql.Timestamp(value);
      timeStamp.setNanos(nanos);
      statement.setTimestamp(column, timeStamp);
    }
  },

  BINARY(-2)
  {
    @Override
    public void writeValue(ExtendedDataOutput out, ResultSet resultSet, int column, boolean canBeNull)
        throws SQLException, IOException
    {
      byte[] value = resultSet.getBytes(column);
      if (canBeNull)
      {
        if (resultSet.wasNull())
        {
          out.writeBoolean(false);
          return;
        }

        out.writeBoolean(true);
      }

      out.writeInt(value.length);
      out.write(value);
    }

    @Override
    public void readValue(ExtendedDataInput in, PreparedStatement statement, int column, boolean canBeNull)
        throws SQLException, IOException
    {
      if (canBeNull && !in.readBoolean())
      {
        statement.setNull(column, getCode());
        return;
      }

      byte[] value = in.readByteArray();
      statement.setBytes(column, value);
    }
  },

  VARBINARY(-3)
  {
    @Override
    public void writeValue(ExtendedDataOutput out, ResultSet resultSet, int column, boolean canBeNull)
        throws SQLException, IOException
    {
      byte[] value = resultSet.getBytes(column);
      if (canBeNull)
      {
        if (resultSet.wasNull())
        {
          out.writeBoolean(false);
          return;
        }

        out.writeBoolean(true);
      }

      out.writeInt(value.length);
      out.write(value);
    }

    @Override
    public void readValue(ExtendedDataInput in, PreparedStatement statement, int column, boolean canBeNull)
        throws SQLException, IOException
    {
      if (canBeNull && !in.readBoolean())
      {
        statement.setNull(column, getCode());
        return;
      }

      byte[] value = in.readByteArray();
      statement.setBytes(column, value);
    }
  },

  LONGVARBINARY(-4, "LONG VARBINARY") //$NON-NLS-1$
  {
    @Override
    public void writeValue(ExtendedDataOutput out, ResultSet resultSet, int column, boolean canBeNull)
        throws SQLException, IOException
    {
      byte[] value = resultSet.getBytes(column);
      if (canBeNull)
      {
        if (resultSet.wasNull())
        {
          out.writeBoolean(false);
          return;
        }

        out.writeBoolean(true);
      }

      out.writeInt(value.length);
      out.write(value);
    }

    @Override
    public void readValue(ExtendedDataInput in, PreparedStatement statement, int column, boolean canBeNull)
        throws SQLException, IOException
    {
      if (canBeNull && !in.readBoolean())
      {
        statement.setNull(column, getCode());
        return;
      }

      byte[] value = in.readByteArray();
      statement.setBytes(column, value);
    }
  },

  BLOB(2004)
  {
    @Override
    public void writeValue(ExtendedDataOutput out, ResultSet resultSet, int column, boolean canBeNull)
        throws SQLException, IOException
    {
      Blob value = resultSet.getBlob(column);
      if (canBeNull)
      {
        if (resultSet.wasNull())
        {
          out.writeBoolean(false);
          return;
        }

        out.writeBoolean(true);
      }

      long length = value.length();
      InputStream stream = value.getBinaryStream();

      try
      {
        out.writeLong(length);
        while (length-- > 0)
        {
          int b = stream.read();
          out.writeByte(b + Byte.MIN_VALUE);
        }
      }
      finally
      {
        IOUtil.close(stream);
      }
    }

    @Override
    public void readValue(final ExtendedDataInput in, PreparedStatement statement, int column, boolean canBeNull)
        throws SQLException, IOException
    {
      if (canBeNull && !in.readBoolean())
      {
        statement.setNull(column, getCode());
        return;
      }

      long length = in.readLong();
      InputStream value = null;

      try
      {
        if (length > 0)
        {
          value = new InputStream()
          {
            @Override
            public int read() throws IOException
            {
              return in.readByte() - Byte.MIN_VALUE;
            }
          };
        }
        else
        {
          value = new ByteArrayInputStream(new byte[0]);
        }

        statement.setBinaryStream(column, value, (int)length);
      }
      finally
      {
        IOUtil.close(value);
      }
    }
  };

  private static final int BOOLEAN_NULL = -1;

  private static final int BOOLEAN_FALSE = 0;

  private static final int BOOLEAN_TRUE = 1;

  private int code;

  private String keyword;

  private DBType(int code, String keyword)
  {
    this.code = code;
    this.keyword = keyword;
  }

  private DBType(int code)
  {
    this(code, null);
  }

  public int getCode()
  {
    return code;
  }

  public String getKeyword()
  {
    return keyword == null ? super.toString() : keyword;
  }

  @Override
  public String toString()
  {
    return getKeyword();
  }

  /**
   * @since 3.0
   */
  public abstract void writeValue(ExtendedDataOutput out, ResultSet resultSet, int column, boolean canBeNull)
      throws SQLException, IOException;

  /**
   * @since 3.0
   */
  public abstract void readValue(ExtendedDataInput in, PreparedStatement statement, int column, boolean canBeNull)
      throws SQLException, IOException;

  private static void writeValueBoolean(ExtendedDataOutput out, ResultSet resultSet, int column, boolean canBeNull)
      throws SQLException, IOException
  {
    boolean value = resultSet.getBoolean(column);
    if (canBeNull)
    {
      if (resultSet.wasNull())
      {
        out.writeByte(BOOLEAN_NULL);
      }
      else
      {
        out.writeByte(value ? BOOLEAN_TRUE : BOOLEAN_FALSE);
      }
    }
    else
    {
      out.writeBoolean(value);
    }
  }

  private static void readValueBoolean(ExtendedDataInput in, PreparedStatement statement, int column,
      boolean canBeNull, int sqlType) throws IOException, SQLException
  {
    if (canBeNull)
    {
      byte opcode = in.readByte();
      switch (opcode)
      {
      case BOOLEAN_NULL:
        statement.setNull(column, sqlType);
        break;

      case BOOLEAN_FALSE:
        statement.setBoolean(column, false);
        break;

      case BOOLEAN_TRUE:
        statement.setBoolean(column, true);
        break;

      default:
        throw new IOException("Invalid boolean opcode: " + opcode);
      }
    }
    else
    {
      boolean value = in.readBoolean();
      statement.setBoolean(column, value);
    }
  }

  /**
   * @since 3.0
   */
  public static DBType getTypeByKeyword(String keyword)
  {
    DBType[] values = DBType.values();
    for (int i = 0; i < values.length; i++)
    {
      DBType dbType = values[i];
      if (dbType.getKeyword().equalsIgnoreCase(keyword))
      {
        return dbType;
      }
    }

    return null;
  }
}
