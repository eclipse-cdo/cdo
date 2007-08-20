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
package org.eclipse.emf.cdo.server.internal.db;

import org.eclipse.net4j.db.IDBField;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public final class DBFeatureInfo extends DBInfo
{
  private Map<Object, IDBField> fields;

  public DBFeatureInfo(int id)
  {
    super(id);
  }

  public IDBField getField(Object context)
  {
    return fields == null ? null : fields.get(context);
  }

  public void addField(Object context, IDBField field)
  {
    if (fields == null)
    {
      fields = new HashMap();
    }
    else
    {
      if (fields.containsKey(context))
      {
        throw new IllegalStateException("Field " + field + " is already added for context " + context);
      }
    }

    fields.put(context, field);
  }

  public IDBField removeField(Object context)
  {
    if (fields == null)
    {
      return null;
    }

    return fields.remove(context);
  }
}
