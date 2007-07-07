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

/**
 * @author Eike Stepper
 */
public interface IDBIndex
{
  public IDBTable geTable();

  public Type geType();

  public IDBField getField(int index);

  public int getFieldCount();

  public IDBField[] getFields();

  public int getPosition();

  public String getName();

  /**
   * @author Eike Stepper
   */
  public enum Type
  {
    PRIMARY_KEY, UNIQUE, NON_UNIQUE
  }
}
