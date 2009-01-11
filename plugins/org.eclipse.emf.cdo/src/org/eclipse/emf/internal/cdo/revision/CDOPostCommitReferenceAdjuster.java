/***************************************************************************
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 **************************************************************************/
package org.eclipse.emf.internal.cdo.revision;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDProvider;
import org.eclipse.emf.cdo.common.revision.CDOReferenceAdjuster;
import org.eclipse.emf.cdo.internal.common.revision.CDOIDMapper;

import org.eclipse.emf.ecore.InternalEObject;

/**
 * @author Simon McDuff
 */
public class CDOPostCommitReferenceAdjuster implements CDOReferenceAdjuster
{
  private CDOIDProvider idProvider;

  private CDOIDMapper idMapper;

  public CDOPostCommitReferenceAdjuster(CDOIDProvider idProvider, CDOIDMapper idMapper)
  {
    this.idProvider = idProvider;
    this.idMapper = idMapper;
  }

  public CDOIDProvider getIDProvider()
  {
    return idProvider;
  }

  public CDOIDMapper getIDMapper()
  {
    return idMapper;
  }

  public Object adjustReference(Object id)
  {
    if (id == null)
    {
      return null;
    }

    if (idProvider != null && (id instanceof CDOID || id instanceof InternalEObject))
    {
      id = idProvider.provideCDOID(id);
    }

    return idMapper.adjustReference(id);
  }
}
