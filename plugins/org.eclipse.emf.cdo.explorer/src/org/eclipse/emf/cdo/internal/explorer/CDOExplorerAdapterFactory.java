/*
 * Copyright (c) 2011-2013 Eike Stepper (Berlin, Germany) and others.
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
import org.eclipse.emf.cdo.explorer.CDOCheckoutSource;
import org.eclipse.emf.cdo.explorer.CDOExplorerUtil;
import org.eclipse.emf.cdo.explorer.CDORepository;
import org.eclipse.emf.cdo.session.CDORepositoryInfo;
import org.eclipse.emf.cdo.session.CDOSession;

import org.eclipse.core.runtime.IAdapterFactory;

/**
 * @since 4.4
 */
@SuppressWarnings("rawtypes")
public class CDOExplorerAdapterFactory implements IAdapterFactory
{
  private static final Class[] CLASSES = { CDOCheckoutSource.class };

  public CDOExplorerAdapterFactory()
  {
  }

  public Class[] getAdapterList()
  {
    return CLASSES;
  }

  public Object getAdapter(Object adaptableObject, Class adapterType)
  {
    if (adapterType == CLASSES[0])
    {
      if (adaptableObject instanceof CDOBranch)
      {
        final CDOBranch branch = (CDOBranch)adaptableObject;

        CDOCommonRepository commonRepository = branch.getBranchManager().getRepository();
        if (commonRepository instanceof CDORepositoryInfo)
        {
          final CDORepositoryInfo repositoryInfo = (CDORepositoryInfo)commonRepository;
          final CDOID rootID = repositoryInfo.getRootResourceID();
          CDOSession session = repositoryInfo.getSession();

          CDORepositoryManagerImpl repositoryManager = (CDORepositoryManagerImpl)CDOExplorerUtil.getRepositoryManager();
          final CDORepository repository = repositoryManager.getRepository(session);

          if (repository != null)
          {
            return new CDOCheckoutSource()
            {
              public CDORepository getRepository()
              {
                return repository;
              }

              public String getBranchPath()
              {
                return branch.getPathName();
              }

              public long getTimeStamp()
              {
                return CDOBranchPoint.UNSPECIFIED_DATE;
              }

              public CDOID getRootID()
              {
                return rootID;
              }
            };
          }
        }
      }
    }

    return null;
  }
}
