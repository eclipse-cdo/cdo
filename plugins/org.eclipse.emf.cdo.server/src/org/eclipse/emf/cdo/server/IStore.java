/***************************************************************************
 * Copyright (c) 2004, 2005, 2006 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.server;

import org.eclipse.emf.cdo.internal.protocol.revision.CDORevisionImpl;
import org.eclipse.emf.cdo.internal.server.RevisionManager;
import org.eclipse.emf.cdo.protocol.CDOID;
import org.eclipse.emf.cdo.protocol.model.CDOClassRef;

import org.eclipse.net4j.util.transaction.ITransaction;

import java.util.Map;

/**
 * @author Eike Stepper
 */
public interface IStore
{
  public String getType();

  public IRepository getRepository();

  public ITransaction createTransaction();

  public void registerResource(CDOID id, String path, Map<CDOID, String> idToPathMap, Map<String, CDOID> pathToIDMap);

  public CDOID loadResourceID(String path);

  public String loadResourcePath(CDOID id);

  public void addRevision(RevisionManager revisionManager, CDORevisionImpl revision);

  public CDORevisionImpl loadRevision(CDOID id);

  public CDORevisionImpl loadHistoricalRevision(CDOID id, long timeStamp);

  public CDOClassRef queryObjectType(CDOID id);
}
