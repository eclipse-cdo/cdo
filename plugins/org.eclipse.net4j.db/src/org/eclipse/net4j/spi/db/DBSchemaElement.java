/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
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

import java.util.Properties;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public abstract class DBSchemaElement implements IDBSchemaElement
{
  private Properties properties;

  public DBSchemaElement()
  {
  }

  public Properties getProperties()
  {
    if (properties == null)
    {
      properties = new Properties();
    }

    return properties;
  }

  @Override
  public String toString()
  {
    return getName();
  }
}
