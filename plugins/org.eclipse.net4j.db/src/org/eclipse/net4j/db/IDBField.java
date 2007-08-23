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
public interface IDBField extends IDBElement
{
  public static final int DEFAULT = -1;

  public IDBTable getTable();

  public DBType getType();

  public int getPrecision();

  public int getScale();

  public boolean isNotNull();

  public int getPosition();

  public String getFullName();

  public void appendValue(StringBuilder builder, Object value);
}
