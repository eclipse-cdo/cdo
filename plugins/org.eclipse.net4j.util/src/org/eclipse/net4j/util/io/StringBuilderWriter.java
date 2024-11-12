package org.eclipse.net4j.util.io;

import java.io.Writer;

/**
 * @author Eike Stepper
 * @since 3.26
 */
public class StringBuilderWriter extends Writer
{
  private final StringBuilder builder;

  public StringBuilderWriter()
  {
    this(16);
  }

  public StringBuilderWriter(int initialSize)
  {
    this(new StringBuilder(initialSize));
  }

  public StringBuilderWriter(StringBuilder builder)
  {
    this.builder = builder;
    lock = builder;
  }

  public StringBuilder getBuilder()
  {
    return builder;
  }

  @Override
  public void write(int c)
  {
    builder.append((char)c);
  }

  @Override
  public void write(char cbuf[], int off, int len)
  {
    if (off < 0 || off > cbuf.length || len < 0 || off + len > cbuf.length || off + len < 0)
    {
      throw new IndexOutOfBoundsException();
    }

    if (len == 0)
    {
      return;
    }

    builder.append(cbuf, off, len);
  }

  @Override
  public void write(String str)
  {
    builder.append(str);
  }

  @Override
  public void write(String str, int off, int len)
  {
    builder.append(str, off, off + len);
  }

  @Override
  public StringBuilderWriter append(CharSequence csq)
  {
    write(String.valueOf(csq));
    return this;
  }

  @Override
  public StringBuilderWriter append(CharSequence csq, int start, int end)
  {
    if (csq == null)
    {
      csq = "null";
    }

    return append(csq.subSequence(start, end));
  }

  @Override
  public StringBuilderWriter append(char c)
  {
    write(c);
    return this;
  }

  @Override
  public void flush()
  {
    // Do nothing.
  }

  @Override
  public void close()
  {
    // Do nothing.
  }

  @Override
  public String toString()
  {
    return builder.toString();
  }
}
