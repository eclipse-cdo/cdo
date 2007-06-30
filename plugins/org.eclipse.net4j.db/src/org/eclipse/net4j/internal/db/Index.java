/***************************************************************************
 * Copyright (c) 2004 - 2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.internal.db;

import org.eclipse.net4j.db.IField;
import org.eclipse.net4j.db.IIndex;

/**
 * @author Eike Stepper
 */
public class Index implements IIndex
{
  private Table table;

  private Type type;

  private IField[] fields;

  public int position;

  public Index(Table table, Type type, IField[] fields, int position)
  {
    this.table = table;
    this.type = type;
    this.fields = fields;
    this.position = position;
  }

  public Table geTable()
  {
    return table;
  }

  public Type geType()
  {
    return type;
  }

  public IField getField(int index)
  {
    return fields[index];
  }

  public int getFieldCount()
  {
    return fields.length;
  }

  public IField[] getFields()
  {
    return fields;
  }

  public int getPosition()
  {
    return position;
  }

  public String getName()
  {
    return "idx_" + table.getName() + "_" + position;
  }

  @Override
  public String toString()
  {
    return getName();
  }
}
