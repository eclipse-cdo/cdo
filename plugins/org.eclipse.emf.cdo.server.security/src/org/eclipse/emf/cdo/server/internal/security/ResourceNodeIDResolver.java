/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.emf.cdo.server.internal.security;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionProvider;
import org.eclipse.emf.cdo.eresource.EresourcePackage;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;

import org.eclipse.emf.ecore.EClass;

/**
 * Resolves the direct resource node for a server-side revision.
 *
 * @author Eike Stepper
 */
public final class ResourceNodeIDResolver
{
  private ResourceNodeIDResolver()
  {
  }

  public static CDOID resolve(CDORevision revision, CDORevisionProvider provider)
  {
    if (isResourceNode(revision.getEClass()))
    {
      return revision.getID();
    }

    InternalCDORevision internalRevision = (InternalCDORevision)revision;

    CDOID resourceID = internalRevision.getResourceID();
    if (resourceID != null && !CDOID.NULL.equals(resourceID))
    {
      return resourceID;
    }

    CDOID parentID = (CDOID)internalRevision.getContainerID();

    while (parentID != null && !CDOID.NULL.equals(parentID))
    {
      InternalCDORevision parent = (InternalCDORevision)provider.getRevision(parentID);
      if (parent == null)
      {
        return null;
      }

      if (isResourceNode(parent.getEClass()))
      {
        return parent.getID();
      }

      if (parent.getResourceID() != null && !CDOID.NULL.equals(parent.getResourceID()))
      {
        return parent.getResourceID();
      }

      parentID = (CDOID)parent.getContainerID();
    }

    return null;
  }

  public static boolean isResourceNode(EClass eClass)
  {
    return eClass == EresourcePackage.Literals.CDO_RESOURCE_NODE //
        || eClass.getEAllSuperTypes().contains(EresourcePackage.Literals.CDO_RESOURCE_NODE);
  }
}
