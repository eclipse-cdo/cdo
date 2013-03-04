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
package org.eclipse.net4j.spi.db;

import org.eclipse.net4j.db.ddl.IDBSchemaElement;

/**
 * @author Eike Stepper
 * @since 4.2
 * @noextend This class is not intended to be subclassed by clients.
 */
public abstract class DBSchemaElement extends DBNamedElement implements IDBSchemaElement
{
  private static final long serialVersionUID = 1L;

  public DBSchemaElement(String name)
  {
    super(name);
  }

  /**
   * Constructor for deserialization.
   */
  protected DBSchemaElement()
  {
  }
}
