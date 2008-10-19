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
package org.eclipse.emf.cdo.server;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.internal.server.MEMStore;
import org.eclipse.emf.cdo.server.IStoreReader.QueryResourcesContext;

/**
 * @author Eike Stepper
 */
public final class StoreUtil
{
  private StoreUtil()
  {
  }

  /**
   * @since 2.0
   */
  public static IMEMStore createMEMStore()
  {
    return new MEMStore();
  }

  /**
   * @since 2.0
   */
  public static QueryResourcesContext.ExactMatch createExactMatchContext(final CDOID folderID, final String name,
      final long timeStamp)
  {
    return new QueryResourcesContext.ExactMatch()
    {
      private CDOID resourceID;

      public CDOID getResourceID()
      {
        return resourceID;
      }

      public long getTimeStamp()
      {
        return timeStamp;
      }

      public CDOID getFolderID()
      {
        return folderID;
      }

      public String getName()
      {
        return name;
      }

      public boolean exactMatch()
      {
        return true;
      }

      public int getMaxResults()
      {
        return 1;
      }

      public boolean addResource(CDOID resourceID)
      {
        this.resourceID = resourceID;
        return false;
      }
    };
  }
}
