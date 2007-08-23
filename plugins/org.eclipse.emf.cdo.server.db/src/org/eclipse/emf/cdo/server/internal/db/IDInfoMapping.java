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

import org.eclipse.emf.cdo.internal.protocol.revision.CDORevisionImpl;
import org.eclipse.emf.cdo.protocol.model.CDOClass;
import org.eclipse.emf.cdo.server.db.IDBStoreAccessor;
import org.eclipse.emf.cdo.server.db.IMappingStrategy;

/**
 * @author Eike Stepper
 */
public abstract class IDInfoMapping extends Mapping
{
  public IDInfoMapping(IMappingStrategy mappingStrategy, CDOClass cdoClass)
  {
    super(mappingStrategy, cdoClass);
  }

  public void writeRevision(IDBStoreAccessor storeAccessor, CDORevisionImpl revision)
  {
    writeIDInfo(storeAccessor, revision);
  }

  protected void writeIDInfo(IDBStoreAccessor storeAccessor, CDORevisionImpl revision)
  {
  }
}
