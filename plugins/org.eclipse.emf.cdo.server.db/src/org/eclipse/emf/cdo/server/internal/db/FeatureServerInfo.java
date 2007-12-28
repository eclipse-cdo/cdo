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
package org.eclipse.emf.cdo.server.internal.db;

import org.eclipse.emf.cdo.protocol.model.CDOFeature;

import org.eclipse.net4j.db.IDBField;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public final class FeatureServerInfo extends ServerInfo
{
  private Map<Object, IDBField> fields;

  private FeatureServerInfo(int id)
  {
    super(id);
  }

  public static FeatureServerInfo setDBID(CDOFeature cdoFeature, int id)
  {
    FeatureServerInfo serverInfo = new FeatureServerInfo(id);
    cdoFeature.setServerInfo(serverInfo);
    return serverInfo;
  }

  @Deprecated
  public IDBField getField(Object context)
  {
    return fields == null ? null : fields.get(context);
  }

  @Deprecated
  public void addField(Object context, IDBField field)
  {
    if (fields == null)
    {
      fields = new HashMap<Object, IDBField>();
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

  @Deprecated
  public IDBField removeField(Object context)
  {
    if (fields == null)
    {
      return null;
    }

    return fields.remove(context);
  }
}
