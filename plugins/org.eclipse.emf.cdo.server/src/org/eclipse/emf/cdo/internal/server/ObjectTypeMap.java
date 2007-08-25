package org.eclipse.emf.cdo.internal.server;

import org.eclipse.net4j.util.io.ExtendedIOUtil;
import org.eclipse.net4j.util.io.SortedFileMap;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.File;
import java.io.IOException;

/**
 * @author Eike Stepper
 */
public final class ObjectTypeMap extends SortedFileMap<Integer, String>
{
  public ObjectTypeMap(File file)
  {
    super(file, "rw");
  }

  @Override
  public int getKeySize()
  {
    return 4;
  }

  @Override
  protected Integer readKey(DataInput in) throws IOException
  {
    return in.readInt();
  }

  @Override
  protected void writeKey(DataOutput out, Integer key) throws IOException
  {
    out.writeInt(key);
  }

  @Override
  public int getValueSize()
  {
    return 20;
  }

  @Override
  protected String readValue(DataInput in) throws IOException
  {
    return ExtendedIOUtil.readString(in);
  }

  @Override
  protected void writeValue(DataOutput out, String value) throws IOException
  {
    byte[] bytes = value.getBytes();
    if (bytes.length + 4 > getValueSize())
    {
      throw new IllegalArgumentException("Value size of " + getValueSize() + " exceeded: " + value);
    }

    ExtendedIOUtil.writeByteArray(out, bytes);
  }
}
