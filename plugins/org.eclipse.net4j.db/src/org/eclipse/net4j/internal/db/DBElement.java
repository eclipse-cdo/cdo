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
package org.eclipse.net4j.internal.db;

import org.eclipse.net4j.db.IDBElement;
import org.eclipse.net4j.util.event.Notifier;

import java.util.Properties;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public abstract class DBElement extends Notifier implements IDBElement
{
  private Properties properties;

  public DBElement()
  {
  }

  public synchronized final Properties getProperties()
  {
    if (properties == null)
    {
      properties = new Properties();
    }

    return properties;
  }
}
