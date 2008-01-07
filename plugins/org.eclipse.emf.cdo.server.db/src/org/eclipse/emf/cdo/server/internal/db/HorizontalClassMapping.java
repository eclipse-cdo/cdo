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

import org.eclipse.emf.cdo.internal.protocol.revision.CDORevisionImpl;
import org.eclipse.emf.cdo.protocol.CDOID;
import org.eclipse.emf.cdo.protocol.model.CDOClass;
import org.eclipse.emf.cdo.server.db.IDBStoreWriter;

/**
 * @author Eike Stepper
 */
public class HorizontalClassMapping extends ClassMapping
{
  public HorizontalClassMapping(HorizontalMappingStrategy mappingStrategy, CDOClass cdoClass)
  {
    super(mappingStrategy, cdoClass, cdoClass.getAllFeatures());
  }

  @Override
  public HorizontalMappingStrategy getMappingStrategy()
  {
    return (HorizontalMappingStrategy)super.getMappingStrategy();
  }

  @Override
  public void writeRevision(IDBStoreWriter storeWriter, CDORevisionImpl revision)
  {
    super.writeRevision(storeWriter, revision);
    if (revision.getVersion() == 1)
    {
      CDOID id = revision.getID();
      CDOClass type = revision.getCDOClass();
      getMappingStrategy().getObjectTypeCache().putObjectType(storeWriter, id, type);
    }
  }

  @Override
  protected boolean hasFullRevisionInfo()
  {
    return true;
  }
}
