/*
 * Copyright (c) 2011, 2012, 2015, 2019, 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.mongodb;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public class Props extends Coll
{
  public static final String NAME = "props";

  public static final String ID = "_id";

  public static final String VALUE = "value";

  public static final String REPOSITORY_CREATED = "org.eclipse.emf.cdo.server.mongodb.repositoryCreated"; //$NON-NLS-1$

  public static final String REPOSITORY_STOPPED = "org.eclipse.emf.cdo.server.mongodb.repositoryStopped"; //$NON-NLS-1$

  public static final String NEXT_LOCAL_CDOID = "org.eclipse.emf.cdo.server.mongodb.nextLocalCDOID"; //$NON-NLS-1$

  public static final String LAST_CDOID = "org.eclipse.emf.cdo.server.mongodb.lastCDOID"; //$NON-NLS-1$

  public static final String LAST_CLASSIFIERID = "org.eclipse.emf.cdo.server.mongodb.lastClassiferID"; //$NON-NLS-1$

  public static final String LAST_BRANCHID = "org.eclipse.emf.cdo.server.mongodb.lastBranchID"; //$NON-NLS-1$

  public static final String LAST_LOCAL_BRANCHID = "org.eclipse.emf.cdo.server.mongodb.lastLocalBranchID"; //$NON-NLS-1$

  public static final String LAST_COMMITTIME = "org.eclipse.emf.cdo.server.mongodb.lastCommitTime"; //$NON-NLS-1$

  public static final String LAST_NONLOCAL_COMMITTIME = "org.eclipse.emf.cdo.server.mongodb.lastNonLocalCommitTime"; //$NON-NLS-1$

  public static final String GRACEFULLY_SHUT_DOWN = "org.eclipse.emf.cdo.server.mongodb.gracefullyShutDown"; //$NON-NLS-1$

  public Props(MongoDBStore store)
  {
    super(store, NAME);
  }

  public Map<String, String> get(Set<String> names)
  {
    Map<String, String> result = new HashMap<>();
    for (String name : names)
    {
      DBObject query = new BasicDBObject(ID, name);
      DBCursor cursor = collection.find(query);

      try
      {
        if (cursor.hasNext())
        {
          DBObject doc = cursor.next();
          result.put(name, (String)doc.get(VALUE));
        }
      }
      finally
      {
        cursor.close();
      }
    }

    return result;
  }

  public void set(Map<String, String> properties)
  {
    for (Map.Entry<String, String> property : properties.entrySet())
    {
      DBObject doc = new BasicDBObject();
      doc.put(ID, property.getKey());
      doc.put(VALUE, property.getValue());

      collection.save(doc);

    }
  }

  public void remove(Set<String> names)
  {
    BasicDBList list = new BasicDBList();
    for (String name : names)
    {
      list.add(name);
    }

    DBObject ref = new BasicDBObject();
    ref.put(ID, new BasicDBObject("$in", list));

    collection.remove(ref);
  }
}
