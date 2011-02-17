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

import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;

import org.eclipse.net4j.util.concurrent.QueueWorker;

import org.eclipse.emf.ecore.EClass;

import com.mongodb.DB;
import com.mongodb.DBCollection;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public class Mapper extends QueueWorker<CDOCommitInfo>
{
  private Map<EClass, DBCollection> collections = new HashMap<EClass, DBCollection>();

  private MongoDBStore store;

  public Mapper(MongoDBStore store)
  {
    this.store = store;
  }

  public synchronized DBCollection getCollection(EClass eClass)
  {
    DBCollection collection = collections.get(eClass);
    if (collection == null)
    {
      collection = mapClass(store.getDB(), eClass);
      collections.put(eClass, collection);
    }

    return collection;
  }

  protected DBCollection mapClass(DB db, EClass eClass)
  {
    String name = eClass.getEPackage().getName() + "." + eClass.getName();
    return db.getCollection(name);
  }

  @Override
  protected void work(WorkContext context, CDOCommitInfo commitInfo)
  {
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    super.doDeactivate();
  }
}
