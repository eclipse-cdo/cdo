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
  CHAR(1), //
  VARCHAR(12), //
  LONGVARCHAR(-1, "LONG VARCHAR"), //
  DATE(91), //
  TIME(92), //
  TIMESTAMP(93), //
  BINARY(-2), //
  VARBINARY(-3), //
  LONGVARBINARY(-4, "LONG VARBINARY"), //
  BLOB(2004), //
  CLOB(2005); //

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
}