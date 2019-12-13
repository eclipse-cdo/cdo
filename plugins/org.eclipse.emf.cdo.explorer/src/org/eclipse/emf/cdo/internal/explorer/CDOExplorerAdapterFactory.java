/*
 * Copyright (c) 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.explorer;

import org.eclipse.emf.cdo.common.CDOCommonRepository;
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.explorer.CDOExplorerUtil;
import org.eclipse.emf.cdo.explorer.repositories.CDORepository;
import org.eclipse.emf.cdo.explorer.repositories.CDORepositoryElement;
import org.eclipse.emf.cdo.internal.explorer.repositories.CDORepositoryManagerImpl;
import org.eclipse.emf.cdo.session.CDORepositoryInfo;

import org.eclipse.core.runtime.IAdapterFactory;

/**
 * @since 4.4
 */
public class CDOExplorerAdapterFactory implements IAdapterFactory
{
  private static final Class<CDORepositoryElement> CLASS_CDOREPOSITORYELEMENT = CDORepositoryElement.class;

  private static final Class<?>[] CLASSES = { CLASS_CDOREPOSITORYELEMENT };

  public CDOExplorerAdapterFactory()
  {
  }

  @Override
  public Class<?>[] getAdapterList()
  {
    return CLASSES;
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T> T getAdapter(Object adaptableObject, Class<T> adapterType)
  {
    if (adapterType == CLASS_CDOREPOSITORYELEMENT)
    {
      if (adaptableObject instanceof CDOBranch)
      {
        adaptableObject = ((CDOBranch)adaptableObject).getHead();
      }

      if (adaptableObject instanceof CDOBranchPoint)
      {
        final CDOBranchPoint branchPoint = (CDOBranchPoint)adaptableObject;
        final CDOBranch branch = branchPoint.getBranch();
        final long timeStamp = branchPoint.getTimeStamp();

        CDOCommonRepository commonRepository = branch.getBranchManager().getRepository();
        if (commonRepository instanceof CDORepositoryInfo)
        {
          final CDORepositoryInfo repositoryInfo = (CDORepositoryInfo)commonRepository;
          final CDOID objectID = repositoryInfo.getRootResourceID();

          CDORepositoryManagerImpl repositoryManager = (CDORepositoryManagerImpl)CDOExplorerUtil.getRepositoryManager();
          final CDORepository repository = repositoryManager.getRepository(repositoryInfo.getSession());
          if (repository != null)
          {
            return (T)new CDORepositoryElement()
            {
              @Override
              public CDORepository getRepository()
              {
                return repository;
              }

              @Override
              public int getBranchID()
              {
                return branch.getID();
              }

              @Override
              public long getTimeStamp()
              {
                return timeStamp;
              }

              @Override
              public CDOID getObjectID()
              {
                return objectID;
              }
            };
          }
        }
      }
    }

    return null;
  }
}
