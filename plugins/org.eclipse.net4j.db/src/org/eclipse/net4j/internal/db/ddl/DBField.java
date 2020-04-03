/*
 * Copyright (c) 2008-2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.internal.db.ddl;

import org.eclipse.net4j.db.DBType;
import org.eclipse.net4j.db.ddl.IDBField;
import org.eclipse.net4j.db.ddl.IDBIndex;
import org.eclipse.net4j.db.ddl.IDBSchema;
import org.eclipse.net4j.db.ddl.IDBSchemaElement;
import org.eclipse.net4j.db.ddl.IDBSchemaVisitor;
import org.eclipse.net4j.db.ddl.IDBTable;
import org.eclipse.net4j.spi.db.ddl.InternalDBField;
import org.eclipse.net4j.spi.db.ddl.InternalDBSchema;
import org.eclipse.net4j.spi.db.ddl.InternalDBTable;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Eike Stepper
 */
public class DBField extends DBSchemaElement implements InternalDBField
{
  public static final int DEFAULT_BOOLEAN_PRECISION = 1;

  public static final int DEFAULT_INTEGER_PRECISION = 10;

  public static final int DEFAULT_DECIMAL_PRECISION = 5;

  public static final int DEFAULT_PRECISION = 0;

  public static final int DEFAULT_SCALE = 0;

  public static final int DEFAULT_CHAR_LENGTH = 1;

  public static final int DEFAULT_VARCHAR_LENGTH = 255;

  private static final ThreadLocal<Boolean> TRACK_CONSTRUCTION = new InheritableThreadLocal<Boolean>()
  {
    @Override
    protected Boolean initialValue()
    {
      return false;
    }
  };

  private static final long serialVersionUID = 1L;

  private IDBTable table;

  private DBType type;

  private int precision;

  private int scale;

  private boolean notNull;

  private int position;

  /**
   * Tracks the construction stack trace to provide better debug infos in IDBTable.addIndex().
   */
  private transient Exception constructionStackTrace;

  public DBField(IDBTable table, String name, DBType type, int precision, int scale, boolean notNull, int position)
  {
    super(name);
    this.table = table;
    this.type = type;
    this.precision = precision;
    this.scale = scale;
    this.notNull = notNull;
    this.position = position;

    if (TRACK_CONSTRUCTION.get() == Boolean.TRUE)
    {
      try
      {
        throw new Exception("The field " + this + " has been constructed here:");
      }
      catch (Exception ex)
      {
        constructionStackTrace = ex;
      }
    }
  }

  /**
   * Constructor for deserialization.
   */
  protected DBField()
  {
  }

  @Override
  public IDBField getWrapper()
  {
    return (IDBField)super.getWrapper();
  }

  @Override
  public SchemaElementType getSchemaElementType()
  {
    return SchemaElementType.FIELD;
  }

  @Override
  public IDBSchema getSchema()
  {
    return table.getSchema();
  }

  @Override
  public IDBTable getTable()
  {
    return table;
  }

  @Override
  public IDBTable getParent()
  {
    return getTable();
  }

  @Override
  public DBType getType()
  {
    return type;
  }

  @Override
  public void setType(DBType type)
  {
    assertUnlocked();
    this.type = type;
  }

  @Override
  public int getPrecision()
  {
    if (precision == DEFAULT)
    {
      switch (type)
      {
      case BOOLEAN:
        return DEFAULT_BOOLEAN_PRECISION;

      case INTEGER:
        return DEFAULT_INTEGER_PRECISION;

      case CHAR:
        return DEFAULT_CHAR_LENGTH;

      case VARCHAR:
      case VARBINARY:
        return DEFAULT_VARCHAR_LENGTH;

      case DECIMAL:
      case NUMERIC:
        return DEFAULT_DECIMAL_PRECISION;

      default:
        return DEFAULT_PRECISION;
      }
    }

    return precision;
  }

  @Override
  public void setPrecision(int precision)
  {
    assertUnlocked();
    this.precision = precision;
  }

  @Override
  public int getScale()
  {
    if (scale == DEFAULT)
    {
      return DEFAULT_SCALE;
    }

    return scale;
  }

  @Override
  public void setScale(int scale)
  {
    assertUnlocked();
    this.scale = scale;
  }

  @Override
  public boolean isNotNull()
  {
    return notNull;
  }

  @Override
  public void setNotNull(boolean notNull)
  {
    if (DBIndex.FIX_NULLABLE_INDEX_COLUMNS.get() != Boolean.TRUE)
    {
      assertUnlocked();
    }

    this.notNull = notNull;
  }

  @Override
  public boolean isIndexed()
  {
    String name = getName();

    for (IDBIndex index : table.getIndices())
    {
      if (index.getField(name) != null)
      {
        return true;
      }
    }

    return false;
  }

  @Override
  public IDBIndex[] getIndices()
  {
    List<IDBIndex> indices = new ArrayList<>();
    String name = getName();

    for (IDBIndex index : table.getIndices())
    {
      if (index.getField(name) != null)
      {
        indices.add(index);
      }
    }

    return indices.toArray(new IDBIndex[indices.size()]);
  }

  @Override
  public int getPosition()
  {
    return position;
  }

  @Override
  public void setPosition(int position)
  {
    assertUnlocked();
    this.position = position;
  }

  @Override
  public String getFullName()
  {
    return table.getName() + "." + getName(); //$NON-NLS-1$
  }

  @Override
  public void remove()
  {
    ((InternalDBTable)table).removeField(this);
  }

  @Override
  public String formatPrecision()
  {
    int precision = getPrecision();
    if (precision > 0)
    {
      return "(" + precision + ")"; //$NON-NLS-1$ //$NON-NLS-2$
    }

    return ""; //$NON-NLS-1$
  }

  @Override
  public String formatPrecisionAndScale()
  {
    if (scale == DEFAULT)
    {
      return "(" + getPrecision() + ")"; //$NON-NLS-1$ //$NON-NLS-2$
    }

    return "(" + getPrecision() + ", " + getScale() + ")"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
  }

  @Override
  public Exception getConstructionStackTrace()
  {
    return constructionStackTrace;
  }

  @Override
  protected void collectElements(List<IDBSchemaElement> elements)
  {
    // Do nothing
  }

  @Override
  protected void doAccept(IDBSchemaVisitor visitor)
  {
    visitor.visit(this);
  }

  @Override
  protected void dumpAdditionalProperties(Writer writer) throws IOException
  {
    writer.append(", type=");
    writer.append(getType().toString());
    writer.append(", precision=");
    writer.append(String.valueOf(getPrecision()));
    writer.append(", scale=");
    writer.append(String.valueOf(getScale()));
    writer.append(", notNull=");
    writer.append(String.valueOf(isNotNull()));
  }

  private void assertUnlocked()
  {
    ((InternalDBSchema)table.getSchema()).assertUnlocked();
  }

  public static void trackConstruction(boolean on)
  {
    if (on)
    {
      TRACK_CONSTRUCTION.set(true);
    }
    else
    {
      TRACK_CONSTRUCTION.remove();
    }
  }

  public static boolean isTrackConstruction()
  {
    return TRACK_CONSTRUCTION.get();
  }
}
