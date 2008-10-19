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

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.model.CDOClass;
import org.eclipse.emf.cdo.common.model.CDOFeature;
import org.eclipse.emf.cdo.common.model.resource.CDOResourceNodeClass;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.server.IPackageManager;
import org.eclipse.emf.cdo.server.db.IDBStoreReader;
import org.eclipse.emf.cdo.server.db.IDBStoreWriter;
import org.eclipse.emf.cdo.server.db.IMappingStrategy;

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
  public void writeRevision(IDBStoreWriter storeWriter, CDORevision revision)
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
  protected void checkDuplicateResources(IDBStoreReader storeReader, CDORevision revision) throws IllegalStateException
  {
    // If auditing is not supported this is checked by a table index (see constructor)
    IMappingStrategy mappingStrategy = getMappingStrategy();
    if (mappingStrategy.getStore().getRepository().isSupportingAudits())
    {
      IPackageManager packageManager = mappingStrategy.getStore().getRepository().getPackageManager();
      CDOResourceNodeClass resourceNodeClass = packageManager.getCDOResourcePackage().getCDOResourceNodeClass();
      CDOFeature resourceNameFeature = resourceNodeClass.getCDONameFeature();

      CDOID folderID = (CDOID)revision.getData().getContainerID();
      String name = (String)revision.getData().get(resourceNameFeature, 0);

      if (mappingStrategy.readResourceID(storeReader, folderID, name, revision.getCreated()) != null)
      {
        throw new IllegalStateException("Duplicate resource or folder: " + name + " in folder " + folderID);
      }
    }
  }

  @Override
  protected boolean hasFullRevisionInfo()
  {
    return true;
  }
}
