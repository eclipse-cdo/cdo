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
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.db.IDBStoreAccessor;

import org.eclipse.net4j.util.collection.Pair;
import org.eclipse.net4j.util.om.monitor.OMMonitor;

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
  public void writeRevision(IDBStoreAccessor accessor, CDORevision revision, OMMonitor monitor)
  {
    try
    {
      monitor.begin(5);
      super.writeRevision(accessor, revision, monitor.fork(4));
      if (revision.getVersion() == 1)
      {
        CDOID id = revision.getID();
        CDOClass type = revision.getCDOClass();
        getMappingStrategy().getObjectTypeCache().putObjectType(accessor, id, type);
      }

      // TODO Better monitoring
      monitor.worked(1);
    }
    finally
    {
      monitor.done();
    }
  }

  @Override
  protected void checkDuplicateResources(IDBStoreAccessor accessor, CDORevision revision) throws IllegalStateException
  {
    IRepository repository = getMappingStrategy().getStore().getRepository();
    if (repository.isSupportingAudits())
    {
      IPackageManager packageManager = repository.getPackageManager();
      CDOResourceNodeClass resourceNodeClass = packageManager.getCDOResourcePackage().getCDOResourceNodeClass();
      CDOFeature resourceNameFeature = resourceNodeClass.getCDONameFeature();

      CDOID folderID = (CDOID)revision.data().getContainerID();
      String name = (String)revision.data().get(resourceNameFeature, 0);

      if (accessor.readResourceID(folderID, name, revision.getCreated()) != null)
      {
        throw new IllegalStateException("Duplicate resource or folder: " + name + " in folder " + folderID);
      }
    }
  }

  @Override
  public Object createReferenceMappingKey(CDOFeature cdoFeature)
  {
    return new Pair<CDOClass, CDOFeature>(getCDOClass(), cdoFeature);
  }

  public boolean hasFullRevisionInfo()
  {
    return true;
  }
}
