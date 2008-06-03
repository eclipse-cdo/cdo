/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.internal.common.model;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDObjectFactory;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.model.CDOType;

import org.eclipse.net4j.util.io.ExtendedDataInput;
import org.eclipse.net4j.util.io.ExtendedDataOutput;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public abstract class CDOTypeImpl implements CDOType
{
  public static Map<Integer, CDOTypeImpl> ids = new HashMap<Integer, CDOTypeImpl>();

  private static final byte BOOLEAN_DEFAULT_PRIMITIVE = 0;

  private static final char CHARACTER_DEFAULT_PRIMITIVE = 0;

  private static final short SHORT_DEFAULT_PRIMITIVE = 0;

  public static final Boolean BOOLEAN_DEFAULT = new Boolean(false);

  public static final Byte BYTE_DEFAULT = new Byte(BOOLEAN_DEFAULT_PRIMITIVE);

  public static final Character CHARACTER_DEFAULT = new Character(CHARACTER_DEFAULT_PRIMITIVE);

  public static final Double DOUBLE_DEFAULT = new Double(0.0);

  public static final Float FLOAT_DEFAULT = new Float(0.0);

  public static final Integer INTEGER_DEFAULT = new Integer(0);

  public static final Long LONG_DEFAULT = new Long(0L);

  public static final Short SHORT_DEFAULT = new Short(SHORT_DEFAULT_PRIMITIVE);

  public static final CDOType BOOLEAN = new CDOTypeImpl("BOOLEAN", 22, false, BOOLEAN_DEFAULT)
  {
    public void writeValue(ExtendedDataOutput out, Object value) throws IOException
    {
      boolean v = (Boolean)(value == null ? getDefaultValue() : value);
      out.writeBoolean(v);
    }

    public Object readValue(ExtendedDataInput in, CDOIDObjectFactory factory) throws IOException
    {
      boolean v = in.readBoolean();
      return new Boolean(v);
    }
  };

  public static final CDOType BYTE = new CDOTypeImpl("BYTE", 24, false, BYTE_DEFAULT)
  {
    public void writeValue(ExtendedDataOutput out, Object value) throws IOException
    {
      out.writeByte((Byte)(value == null ? getDefaultValue() : value));
    }

    public Object readValue(ExtendedDataInput in, CDOIDObjectFactory factory) throws IOException
    {
      return new Byte(in.readByte());
    }
  };

  public static final CDOType CHAR = new CDOTypeImpl("CHAR", 27, false, CHARACTER_DEFAULT)
  {
    public void writeValue(ExtendedDataOutput out, Object value) throws IOException
    {
      out.writeChar(((Character)(value == null ? getDefaultValue() : value)).charValue());
    }

    public Object readValue(ExtendedDataInput in, CDOIDObjectFactory factory) throws IOException
    {
      return new Character(in.readChar());
    }
  };

  public static final CDOType DOUBLE = new CDOTypeImpl("DOUBLE", 31, false, DOUBLE_DEFAULT)
  {
    public void writeValue(ExtendedDataOutput out, Object value) throws IOException
    {
      out.writeDouble((Double)(value == null ? getDefaultValue() : value));
    }

    public Object readValue(ExtendedDataInput in, CDOIDObjectFactory factory) throws IOException
    {
      return new Double(in.readDouble());
    }
  };

  public static final CDOType FLOAT = new CDOTypeImpl("FLOAT", 37, false, FLOAT_DEFAULT)
  {
    public void writeValue(ExtendedDataOutput out, Object value) throws IOException
    {
      out.writeFloat((Float)(value == null ? getDefaultValue() : value));
    }

    public Object readValue(ExtendedDataInput in, CDOIDObjectFactory factory) throws IOException
    {
      return new Float(in.readFloat());
    }
  };

  public static final CDOType INT = new CDOTypeImpl("INT", 39, false, INTEGER_DEFAULT)
  {
    public void writeValue(ExtendedDataOutput out, Object value) throws IOException
    {
      out.writeInt((Integer)(value == null ? getDefaultValue() : value));
    }

    public Object readValue(ExtendedDataInput in, CDOIDObjectFactory factory) throws IOException
    {
      return new Integer(in.readInt());
    }
  };

  public static final CDOType LONG = new CDOTypeImpl("LONG", 43, false, LONG_DEFAULT)
  {
    public void writeValue(ExtendedDataOutput out, Object value) throws IOException
    {
      out.writeLong((Long)(value == null ? getDefaultValue() : value));
    }

    public Object readValue(ExtendedDataInput in, CDOIDObjectFactory factory) throws IOException
    {
      return new Long(in.readLong());
    }
  };

  public static final CDOType SHORT = new CDOTypeImpl("SHORT", 48, false, SHORT_DEFAULT)
  {
    public void writeValue(ExtendedDataOutput out, Object value) throws IOException
    {
      out.writeShort((Short)(value == null ? getDefaultValue() : value));
    }

    public Object readValue(ExtendedDataInput in, CDOIDObjectFactory factory) throws IOException
    {
      return new Short(in.readShort());
    }
  };

  public static final CDOType OBJECT = new CDOTypeImpl("OBJECT", 10, true, CDOID.NULL)
  {
    public void writeValue(ExtendedDataOutput out, Object value) throws IOException
    {
      CDOIDUtil.write(out, (CDOID)value);
    }

    public Object readValue(ExtendedDataInput in, CDOIDObjectFactory factory) throws IOException
    {
      return CDOIDUtil.read(in, factory);
    }
  };

  public static final CDOType BOOLEAN_OBJECT = new ObjectType("BOOLEAN_OBJECT", 23)
  {
    @Override
    protected void doWriteValue(ExtendedDataOutput out, Object value) throws IOException
    {
      out.writeBoolean((Boolean)value);
    }

    @Override
    protected Object doReadValue(ExtendedDataInput in) throws IOException
    {
      return in.readBoolean();
    }
  };

  public static final CDOType BYTE_OBJECT = new ObjectType("BYTE_OBJECT", 26)
  {
    @Override
    protected void doWriteValue(ExtendedDataOutput out, Object value) throws IOException
    {
      out.writeByte((Byte)value);
    }

    @Override
    protected Object doReadValue(ExtendedDataInput in) throws IOException
    {
      return in.readByte();
    }
  };

  public static final CDOType CHARACTER_OBJECT = new ObjectType("CHARACTER_OBJECT", 28)
  {
    @Override
    protected void doWriteValue(ExtendedDataOutput out, Object value) throws IOException
    {
      out.writeChar((Character)value);
    }

    @Override
    protected Object doReadValue(ExtendedDataInput in) throws IOException
    {
      return in.readChar();
    }
  };

  public static final CDOType DATE = new ObjectType("DATE", 29)
  {
    @Override
    protected void doWriteValue(ExtendedDataOutput out, Object value) throws IOException
    {
      out.writeLong(((Date)value).getTime());
    }

    @Override
    protected Object doReadValue(ExtendedDataInput in) throws IOException
    {
      return new Date(in.readLong());
    }
  };

  public static final CDOType DOUBLE_OBJECT = new ObjectType("DOUBLE_OBJECT", 32)
  {
    @Override
    protected void doWriteValue(ExtendedDataOutput out, Object value) throws IOException
    {
      out.writeDouble((Double)value);
    }

    @Override
    protected Object doReadValue(ExtendedDataInput in) throws IOException
    {
      return in.readDouble();
    }
  };

  public static final CDOType FLOAT_OBJECT = new ObjectType("FLOAT_OBJECT", 38)
  {
    @Override
    protected void doWriteValue(ExtendedDataOutput out, Object value) throws IOException
    {
      out.writeFloat((Float)value);
    }

    @Override
    protected Object doReadValue(ExtendedDataInput in) throws IOException
    {
      return in.readFloat();
    }
  };

  public static final CDOType INTEGER_OBJECT = new ObjectType("INTEGER_OBJECT", 40)
  {
    @Override
    protected void doWriteValue(ExtendedDataOutput out, Object value) throws IOException
    {
      out.writeInt((Integer)value);
    }

    @Override
    protected Object doReadValue(ExtendedDataInput in) throws IOException
    {
      return in.readInt();
    }
  };

  public static final CDOType LONG_OBJECT = new ObjectType("LONG_OBJECT", 44)
  {
    @Override
    protected void doWriteValue(ExtendedDataOutput out, Object value) throws IOException
    {
      out.writeLong((Long)value);
    }

    @Override
    protected Object doReadValue(ExtendedDataInput in) throws IOException
    {
      return in.readLong();
    }
  };

  public static final CDOType SHORT_OBJECT = new ObjectType("SHORT_OBJECT", 49)
  {
    @Override
    protected void doWriteValue(ExtendedDataOutput out, Object value) throws IOException
    {
      out.writeShort((Short)value);
    }

    @Override
    protected Object doReadValue(ExtendedDataInput in) throws IOException
    {
      return in.readShort();
    }
  };

  public static final CDOType STRING = new CDOTypeImpl("STRING", 50, true)
  {
    @SuppressWarnings("cast")
    @Override
    public Object copyValue(Object value)
    {
      return (String)value;
    }

    public void writeValue(ExtendedDataOutput out, Object value) throws IOException
    {
      out.writeString((String)value);
    }

    public Object readValue(ExtendedDataInput in, CDOIDObjectFactory factory) throws IOException
    {
      return in.readString();
    }
  };

  public static final CDOType BYTE_ARRAY = new CDOTypeImpl("BYTE_ARRAY", 25, true)
  {
    @Override
    public Object copyValue(Object value)
    {
      if (value == null)
      {
        return null;
      }

      byte[] array = (byte[])value;
      byte[] result = new byte[array.length];
      System.arraycopy(value, 0, result, 0, array.length);
      return result;
    }

    public void writeValue(ExtendedDataOutput out, Object value) throws IOException
    {
      out.writeByteArray((byte[])value);
    }

    public Object readValue(ExtendedDataInput in, CDOIDObjectFactory factory) throws IOException
    {
      return in.readByteArray();
    }
  };

  public static final CDOType CUSTOM = new CDOTypeImpl("CUSTOM", 999, true)
  {
    @SuppressWarnings("cast")
    @Override
    public Object copyValue(Object value)
    {
      return (String)value;
    }

    public void writeValue(ExtendedDataOutput out, Object value) throws IOException
    {
      out.writeString((String)value);
    }

    public Object readValue(ExtendedDataInput in, CDOIDObjectFactory factory) throws IOException
    {
      return in.readString();
    }
  };

  private String name;

  private int typeID;

  private boolean canBeNull;

  private Object defaultValue;

  private CDOTypeImpl(String name, int typeID, boolean canBeNull, Object defaultValue)
  {
    this.name = name;
    this.typeID = typeID;
    this.canBeNull = canBeNull;
    this.defaultValue = defaultValue;
    ids.put(typeID, this);
  }

  private CDOTypeImpl(String name, int typeID, boolean canBeNull)
  {
    this(name, typeID, canBeNull, null);
  }

  public String getName()
  {
    return name;
  }

  public int getTypeID()
  {
    return typeID;
  }

  public boolean canBeNull()
  {
    return canBeNull;
  }

  public Object getDefaultValue()
  {
    return defaultValue;
  }

  @Override
  public String toString()
  {
    return name;
  }

  public Object copyValue(Object value)
  {
    return value == null ? getDefaultValue() : value;
  }

  public void write(ExtendedDataOutput out) throws IOException
  {
    out.writeInt(typeID);
  }

  /**
   * @author Eike Stepper
   */
  private static abstract class ObjectType extends CDOTypeImpl
  {
    public ObjectType(String name, int typeID)
    {
      super(name, typeID, true);
    }

    public final void writeValue(ExtendedDataOutput out, Object value) throws IOException
    {
      if (value == null)
      {
        out.writeBoolean(false);
      }
      else
      {
        out.writeBoolean(true);
        doWriteValue(out, value);
      }
    }

    protected abstract void doWriteValue(ExtendedDataOutput out, Object value) throws IOException;

    public final Object readValue(ExtendedDataInput in, CDOIDObjectFactory factory) throws IOException
    {
      boolean notNull = in.readBoolean();
      if (notNull)
      {
        return doReadValue(in);
      }

      return null;
    }

    protected abstract Object doReadValue(ExtendedDataInput in) throws IOException;
  }
}
