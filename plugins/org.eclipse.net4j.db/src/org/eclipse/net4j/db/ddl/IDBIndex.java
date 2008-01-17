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
package org.eclipse.net4j.db.ddl;

/**
 * @author Eike Stepper
 */
public interface IDBIndex extends IDBSchemaElement
{
  public IDBTable getTable();

  public Type getType();

  public IDBField getField(int index);

  public int getFieldCount();

  public IDBField[] getFields();

  public int getPosition();

  /**
   * @author Eike Stepper
   */
  public enum Type
  {
    PRIMARY_KEY, UNIQUE, NON_UNIQUE
  }
}
