/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
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

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Eike Stepper
 */
public enum DBType
{
  BOOLEAN(16)
  {
    @Override
    public void writeValue(ExtendedDataOutput out, ResultSet resultSet, int column) throws SQLException, IOException
    {
      boolean value = resultSet.getBoolean(column);
      out.writeBoolean(value);
    }

    @Override
    public void readValue(ExtendedDataInput in, PreparedStatement statement, int column) throws SQLException,
        IOException
    {
      boolean value = in.readBoolean();
      statement.setBoolean(column, value);
    }
  },

  BIT(-7)
  {
    @Override
    public void writeValue(ExtendedDataOutput out, ResultSet resultSet, int column) throws SQLException, IOException
    {
      boolean value = resultSet.getBoolean(column);
      out.writeBoolean(value);
    }

    @Override
    public void readValue(ExtendedDataInput in, PreparedStatement statement, int column) throws SQLException,
        IOException
    {
      boolean value = in.readBoolean();
      statement.setBoolean(column, value);
    }
  },

  TINYINT(-6)
  {
    @Override
    public void writeValue(ExtendedDataOutput out, ResultSet resultSet, int column) throws SQLException, IOException
    {
      byte value = resultSet.getByte(column);
      out.writeByte(value);
    }

    @Override
    public void readValue(ExtendedDataInput in, PreparedStatement statement, int column) throws SQLException,
        IOException
    {
      byte value = in.readByte();
      statement.setByte(column, value);
    }
  },

  SMALLINT(5)
  {
    @Override
    public void writeValue(ExtendedDataOutput out, ResultSet resultSet, int column) throws SQLException, IOException
    {
      short value = resultSet.getShort(column);
      out.writeShort(value);
    }

    @Override
    public void readValue(ExtendedDataInput in, PreparedStatement statement, int column) throws SQLException,
        IOException
    {
      short value = in.readShort();
      statement.setShort(column, value);
    }
  },

  INTEGER(4)
  {
    @Override
    public void writeValue(ExtendedDataOutput out, ResultSet resultSet, int column) throws SQLException, IOException
    {
      int value = resultSet.getInt(column);
      out.writeInt(value);
    }

    @Override
    public void readValue(ExtendedDataInput in, PreparedStatement statement, int column) throws SQLException,
        IOException
    {
      int value = in.readInt();
      statement.setInt(column, value);
    }
  },

  BIGINT(-5)
  {
    @Override
    public void writeValue(ExtendedDataOutput out, ResultSet resultSet, int column) throws SQLException, IOException
    {
      long value = resultSet.getLong(column);
      out.writeLong(value);
    }

    @Override
    public void readValue(ExtendedDataInput in, PreparedStatement statement, int column) throws SQLException,
        IOException
    {
      long value = in.readLong();
      statement.setLong(column, value);
    }
  },

  FLOAT(6)
  {
    @Override
    public void writeValue(ExtendedDataOutput out, ResultSet resultSet, int column) throws SQLException, IOException
    {
      float value = resultSet.getFloat(column);
      out.writeFloat(value);
    }

    @Override
    public void readValue(ExtendedDataInput in, PreparedStatement statement, int column) throws SQLException,
        IOException
    {
      float value = in.readFloat();
      statement.setFloat(column, value);
    }
  },

  REAL(7)
  {
    @Override
    public void writeValue(ExtendedDataOutput out, ResultSet resultSet, int column) throws SQLException, IOException
    {
      float value = resultSet.getFloat(column);
      out.writeFloat(value);
    }

    @Override
    public void readValue(ExtendedDataInput in, PreparedStatement statement, int column) throws SQLException,
        IOException
    {
      float value = in.readFloat();
      statement.setFloat(column, value);
    }
  },

  DOUBLE(8)
  {
    @Override
    public void writeValue(ExtendedDataOutput out, ResultSet resultSet, int column) throws SQLException, IOException
    {
      double value = resultSet.getDouble(column);
      out.writeDouble(value);
    }

    @Override
    public void readValue(ExtendedDataInput in, PreparedStatement statement, int column) throws SQLException,
        IOException
    {
      double value = in.readDouble();
      statement.setDouble(column, value);
    }
  },

  NUMERIC(2)
  {
    @Override
    public void writeValue(ExtendedDataOutput out, ResultSet resultSet, int column) throws SQLException, IOException
    {
      // TODO: implement DBType.NUMERIC.writeValue()
      throw new UnsupportedOperationException();
    }

    @Override
    public void readValue(ExtendedDataInput in, PreparedStatement statement, int column) throws SQLException,
        IOException
    {
      // TODO: implement DBType.NUMERIC.readValue()
      throw new UnsupportedOperationException();
    }
  },

  DECIMAL(3)
  {
    @Override
    public void writeValue(ExtendedDataOutput out, ResultSet resultSet, int column) throws SQLException, IOException
    {
      // TODO: implement DBType.DECIMAL.writeValue()
      throw new UnsupportedOperationException();
    }

    @Override
    public void readValue(ExtendedDataInput in, PreparedStatement statement, int column) throws SQLException,
        IOException
    {
      // TODO: implement DBType.DECIMAL.readValue()
      throw new UnsupportedOperationException();
    }
  },

  CHAR(1)
  {
    @Override
    public void writeValue(ExtendedDataOutput out, ResultSet resultSet, int column) throws SQLException, IOException
    {
      String value = resultSet.getString(column);
      out.writeString(value);
    }

    @Override
    public void readValue(ExtendedDataInput in, PreparedStatement statement, int column) throws SQLException,
        IOException
    {
      String value = in.readString();
      statement.setString(column, value);
    }
  },

  VARCHAR(12)
  {
    @Override
    public void writeValue(ExtendedDataOutput out, ResultSet resultSet, int column) throws SQLException, IOException
    {
      String value = resultSet.getString(column);
      out.writeString(value);
    }

    @Override
    public void readValue(ExtendedDataInput in, PreparedStatement statement, int column) throws SQLException,
        IOException
    {
      String value = in.readString();
      statement.setString(column, value);
    }
  },

  LONGVARCHAR(-1, "LONG VARCHAR") //$NON-NLS-1$
  {
    @Override
    public void writeValue(ExtendedDataOutput out, ResultSet resultSet, int column) throws SQLException, IOException
    {
      String value = resultSet.getString(column);
      out.writeString(value);
    }

    @Override
    public void readValue(ExtendedDataInput in, PreparedStatement statement, int column) throws SQLException,
        IOException
    {
      String value = in.readString();
      statement.setString(column, value);
    }
  },

  CLOB(2005)
  {
    @Override
    public void writeValue(ExtendedDataOutput out, ResultSet resultSet, int column) throws SQLException, IOException
    {
      Clob value = resultSet.getClob(column);
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
    public void readValue(ExtendedDataInput in, PreparedStatement statement, int column) throws SQLException,
        IOException
    {
      // TODO: implement DBType.CLOB.readValue()
      throw new UnsupportedOperationException();
    }
  },

  DATE(91)
  {
    @Override
    public void writeValue(ExtendedDataOutput out, ResultSet resultSet, int column) throws SQLException, IOException
    {
      java.sql.Date value = resultSet.getDate(column);
      out.writeLong(value.getTime());
    }

    @Override
    public void readValue(ExtendedDataInput in, PreparedStatement statement, int column) throws SQLException,
        IOException
    {
      long value = in.readLong();
      statement.setDate(column, new java.sql.Date(value));
    }
  },

  TIME(92)
  {
    @Override
    public void writeValue(ExtendedDataOutput out, ResultSet resultSet, int column) throws SQLException, IOException
    {
      java.sql.Time value = resultSet.getTime(column);
      out.writeLong(value.getTime());
    }

    @Override
    public void readValue(ExtendedDataInput in, PreparedStatement statement, int column) throws SQLException,
        IOException
    {
      long value = in.readLong();
      statement.setTime(column, new java.sql.Time(value));
    }
  },

  TIMESTAMP(93)
  {
    @Override
    public void writeValue(ExtendedDataOutput out, ResultSet resultSet, int column) throws SQLException, IOException
    {
      java.sql.Timestamp value = resultSet.getTimestamp(column);
      out.writeLong(value.getTime());
      out.writeInt(value.getNanos());
    }

    @Override
    public void readValue(ExtendedDataInput in, PreparedStatement statement, int column) throws SQLException,
        IOException
    {
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
    public void writeValue(ExtendedDataOutput out, ResultSet resultSet, int column) throws SQLException, IOException
    {
      // TODO: implement DBType.BINARY.writeValue()
      throw new UnsupportedOperationException();
    }

    @Override
    public void readValue(ExtendedDataInput in, PreparedStatement statement, int column) throws SQLException,
        IOException
    {
      // TODO: implement DBType.BINARY.readValue()
      throw new UnsupportedOperationException();
    }
  },

  VARBINARY(-3)
  {
    @Override
    public void writeValue(ExtendedDataOutput out, ResultSet resultSet, int column) throws SQLException, IOException
    {
      // TODO: implement DBType.VARBINARY.writeValue()
      throw new UnsupportedOperationException();
    }

    @Override
    public void readValue(ExtendedDataInput in, PreparedStatement statement, int column) throws SQLException,
        IOException
    {
      // TODO: implement DBTypeVARBINARY.readValue()
      throw new UnsupportedOperationException();
    }
  },

  LONGVARBINARY(-4, "LONG VARBINARY") //$NON-NLS-1$
  {
    @Override
    public void writeValue(ExtendedDataOutput out, ResultSet resultSet, int column) throws SQLException, IOException
    {
      // TODO: implement DBType.LONGVARBINARY.writeValue()
      throw new UnsupportedOperationException();
    }

    @Override
    public void readValue(ExtendedDataInput in, PreparedStatement statement, int column) throws SQLException,
        IOException
    {
      // TODO: implement DBType.LONGVARBINARY.readValue()
      throw new UnsupportedOperationException();
    }
  },

  BLOB(2004)
  {
    @Override
    public void writeValue(ExtendedDataOutput out, ResultSet resultSet, int column) throws SQLException, IOException
    {
      Blob value = resultSet.getBlob(column);
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
    public void readValue(ExtendedDataInput in, PreparedStatement statement, int column) throws SQLException,
        IOException
    {
      // TODO: implement DBType.BLOB.readValue()
      throw new UnsupportedOperationException();
    }
  };

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

  /**
   * @since 3.0
   */
  public abstract void writeValue(ExtendedDataOutput out, ResultSet resultSet, int column) throws SQLException,
      IOException;

  /**
   * @since 3.0
   */
  public abstract void readValue(ExtendedDataInput in, PreparedStatement statement, int column) throws SQLException,
      IOException;

  @Override
  public String toString()
  {
    return getKeyword();
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
