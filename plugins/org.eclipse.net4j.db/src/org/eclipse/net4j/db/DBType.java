package org.eclipse.net4j.db;

/**
 * @author Eike Stepper
 */
public enum DBType
{
  BOOLEAN(16), //
  BIT(-7), //
  TINYINT(-6), //
  SMALLINT(5), //
  INTEGER(4), //
  BIGINT(-5), //
  FLOAT(6), //
  REAL(7), //
  DOUBLE(8), //
  NUMERIC(2), //
  DECIMAL(3), //
  CHAR(1)
  {
    @Override
    public void appendValue(StringBuilder builder, Object value)
    {
      builder.append("'");
      builder.append(value);
      builder.append("'");
    }
  }, //
  VARCHAR(12)
  {
    @Override
    public void appendValue(StringBuilder builder, Object value)
    {
      builder.append("'");
      builder.append(value);
      builder.append("'");
    }
  }, //
  LONGVARCHAR(-1, "LONG VARCHAR")
  {
    @Override
    public void appendValue(StringBuilder builder, Object value)
    {
      builder.append("'");
      builder.append(value);
      builder.append("'");
    }
  }, //
  DATE(91), //
  TIME(92), //
  TIMESTAMP(93), //
  BINARY(-2)
  {
    @Override
    public void appendValue(StringBuilder builder, Object value)
    {
      throw new UnsupportedOperationException();
    }
  }, //
  VARBINARY(-3)
  {
    @Override
    public void appendValue(StringBuilder builder, Object value)
    {
      throw new UnsupportedOperationException();
    }
  }, //
  LONGVARBINARY(-4, "LONG VARBINARY")
  {
    @Override
    public void appendValue(StringBuilder builder, Object value)
    {
      throw new UnsupportedOperationException();
    }
  }, //
  BLOB(2004)
  {
    @Override
    public void appendValue(StringBuilder builder, Object value)
    {
      throw new UnsupportedOperationException();
    }
  }, //
  CLOB(2005)
  {
    @Override
    public void appendValue(StringBuilder builder, Object value)
    {
      throw new UnsupportedOperationException();
    }
  }; //

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

  public void appendValue(StringBuilder builder, Object value)
  {
    if (value == null)
    {
      builder.append("NULL");
    }
    else
    {
      builder.append(value);
    }
  }
}