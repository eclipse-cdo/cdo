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
package org.eclipse.emf.cdo.server.mongodbdb;

import org.eclipse.emf.cdo.server.ISession;
import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.server.ITransaction;

import com.mongodb.DB;

/**
 * @author Eike Stepper
 */
public interface IMongoDBStore extends IStore
{
  public IsolationLevel getIsolationLevel();

  public EmbeddingStrategy getEmbeddingStrategy();

  public IDHandler getIDHandler();

  public DB getDB();

  public IMongoDBStoreAccessor getReader(ISession session);

  public IMongoDBStoreAccessor getWriter(ITransaction transaction);

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
  public interface IDHandler
  {
  }
}
