/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.internal.db.ddl;

import org.eclipse.net4j.db.ddl.IDBIndexField;
import org.eclipse.net4j.db.ddl.IDBSchema;
import org.eclipse.net4j.spi.db.DBSchemaElement;

/**
 * @author Eike Stepper
 */
public class DBIndexField extends DBSchemaElement implements IDBIndexField
{
  private static final long serialVersionUID = 1L;

  private DBIndex index;

  private DBField field;

  private int position;

  public DBIndexField(DBIndex index, DBField field, int position)
  {
    super(field.getName());
    this.index = index;
    this.field = field;
    this.position = position;
  }

  /**
   * Constructor for deserialization.
   */
  protected DBIndexField()
  {
  }

  public DBIndex getIndex()
  {
    return index;
  }

  public DBField getField()
  {
    return field;
  }

  public int getPosition()
  {
    return position;
  }

  public void setPosition(int position)
  {
    index.getTable().getSchema().assertUnlocked();
    this.position = position;
  }

  public IDBSchema getSchema()
  {
    return field.getSchema();
  }

  public String getFullName()
  {
    return field.getFullName();
  }

  public void remove()
  {
    index.removeIndexField(this);
  }
}
