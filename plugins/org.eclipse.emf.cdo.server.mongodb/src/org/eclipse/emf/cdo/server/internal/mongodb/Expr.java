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
package org.eclipse.emf.cdo.server.internal.mongodb;

import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

/**
 * @author Eike Stepper
 */
public abstract class Expr
{
  public void evaluate(DBCollection collection, ResultHandler handler)
  {
    DBCursor cursor = findDocuments(collection);

    try
    {
      while (cursor.hasNext())
      {
        DBObject doc = cursor.next();
        handleDocument(doc, handler);
      }
    }
    finally
    {
      cursor.close();
    }
  }

  protected void handleDocument(DBObject doc, ResultHandler handler)
  {
  }

  protected abstract DBCursor findDocuments(DBCollection collection);

  /**
   * @author Eike Stepper
   */
  public interface ResultHandler
  {

  }
}
