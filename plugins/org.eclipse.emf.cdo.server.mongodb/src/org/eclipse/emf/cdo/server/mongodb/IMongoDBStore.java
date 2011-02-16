/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Stefan Winkler - 271444: [DB] Multiple refactorings
 *    Stefan Winkler - 249610: [DB] Support external references (Implementation)
 */
package org.eclipse.emf.cdo.server.mongodb;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOID.ObjectType;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.server.ISession;
import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.server.ITransaction;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoURI;

import java.util.Comparator;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public interface IMongoDBStore extends IStore
{
  public MongoURI getMongoURI();

  public String getDBName();

  public IsolationLevel getIsolationLevel();

  public EmbeddingStrategy getEmbeddingStrategy();

  public IDHandler getIDHandler();

  public IMongoDBStoreAccessor getReader(ISession session);

  public IMongoDBStoreAccessor getWriter(ITransaction transaction);

  public DB getDB();

  public DBCollection getPropertiesCollection();

  public DBCollection getPackageUnitsCollection();

  public DBCollection getCommitInfosCollection();

  /**
   * @author Eike Stepper
   */
  public interface IsolationLevel
  {
  }

  /**
   * @author Eike Stepper
   */
  public interface EmbeddingStrategy
  {
  }

  /**
   * @author Eike Stepper
   */
  public interface IDHandler extends Comparator<CDOID>
  {
    public IMongoDBStore getStore();

    public Set<ObjectType> getObjectIDTypes();

    public CDOID getMinCDOID();

    public CDOID getMaxCDOID();

    public boolean isLocalCDOID(CDOID id);

    public CDOID getNextCDOID(CDORevision revision);

    public CDOID createCDOID(String val);

    public CDOID getNextLocalObjectID();

    public void setNextLocalObjectID(CDOID nextLocalObjectID);

    public CDOID getLastObjectID();

    public void setLastObjectID(CDOID lastObjectID);

    public void write(DBObject doc, String key, CDOID id);
  }
}
