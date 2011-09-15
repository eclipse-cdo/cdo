/*
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.db.ddl;

/**
 * An index specification in a {@link IDBTable DB table}.
 * 
 * @author Eike Stepper
 * @noimplement This interface is not intended to be implemented by clients.
 * @noextend This interface is not intended to be extended by clients.
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
   * The type of an {@link IDBIndex index} specification in a {@link IDBTable DB table}.
   * 
   * @author Eike Stepper
   * @noextend This interface is not intended to be extended by clients.
   */
  public enum Type
  {
    PRIMARY_KEY, UNIQUE, NON_UNIQUE
  }
}
