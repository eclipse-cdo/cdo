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
package org.eclipse.net4j.db;

import org.eclipse.net4j.util.collection.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Eike Stepper
 */
public abstract class DBSelect implements IDBRowHandler
{
  private List<IDBField> fields = new ArrayList(0);

  private List<Pair<IDBField, Boolean>> orders = new ArrayList(0);

  private String where;

  public DBSelect(IDBField... fields)
  {
    this.fields.addAll(Arrays.asList(fields));
  }

  public DBSelect field(IDBField field)
  {
    fields.add(field);
    return this;
  }

  public DBSelect order(IDBField field, boolean asc)
  {
    orders.add(new Pair(field, asc));
    return this;
  }

  public DBSelect orderAsc(IDBField field)
  {
    return order(field, true);
  }

  public DBSelect orderDesc(IDBField field)
  {
    return order(field, false);
  }

  public DBSelect where()
  {
    where = "";
    return this;
  }

  public DBSelect whereEq(IDBField field, boolean value)
  {
    this.where += where;
    return this;
  }
}
