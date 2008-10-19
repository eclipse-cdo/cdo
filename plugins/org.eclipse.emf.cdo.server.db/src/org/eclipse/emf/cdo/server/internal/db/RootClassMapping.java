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

import org.eclipse.emf.cdo.common.model.core.CDOObjectClass;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.server.db.IDBStoreReader;

/**
 * @author Eike Stepper
 */
public class RootClassMapping extends ClassMapping
{
  public RootClassMapping(VerticalMappingStrategy mappingStrategy)
  {
    super(mappingStrategy, getRootClass(mappingStrategy), null);
    initTable(getTable(), true);
  }

  @Override
  public VerticalMappingStrategy getMappingStrategy()
  {
    return (VerticalMappingStrategy)super.getMappingStrategy();
  }

  @Override
  protected boolean hasFullRevisionInfo()
  {
    return true;
  }

  @Override
  protected void checkDuplicateResources(IDBStoreReader storeReader, CDORevision revision) throws IllegalStateException
  {
    // TODO: implement RootClassMapping.checkDuplicateResources(storeReader, revision)
    throw new UnsupportedOperationException();
  }

  private static CDOObjectClass getRootClass(VerticalMappingStrategy mappingStrategy)
  {
    return mappingStrategy.getStore().getRepository().getPackageManager().getCDOCorePackage().getCDOObjectClass();
  }
}
