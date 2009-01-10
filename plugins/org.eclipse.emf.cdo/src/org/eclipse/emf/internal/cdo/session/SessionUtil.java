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
package org.eclipse.emf.internal.cdo.session;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDMetaRange;
import org.eclipse.emf.cdo.common.id.CDOIDTemp;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.emf.internal.cdo.bundle.OM;
import org.eclipse.emf.internal.cdo.view.CDOViewSetImpl;

import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.spi.cdo.InternalCDOViewSet;

import java.util.Map;

/**
 * @author Eike Stepper
 */
public final class SessionUtil
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_SESSION, SessionUtil.class);

  private SessionUtil()
  {
  }

  public static CDOIDMetaRange registerEPackage(EPackage ePackage, int firstMetaID,
      Map<CDOID, InternalEObject> idToMetaInstances, Map<InternalEObject, CDOID> metaInstanceToIDs)
  {
    CDOIDTemp lowerBound = CDOIDUtil.createTempMeta(firstMetaID);
    CDOIDMetaRange range = CDOIDUtil.createMetaRange(lowerBound, 0);
    range = registerMetaInstance((InternalEObject)ePackage, range, idToMetaInstances, metaInstanceToIDs);
    return range;
  }

  public static CDOIDMetaRange registerMetaInstance(InternalEObject metaInstance, CDOIDMetaRange range,
      Map<CDOID, InternalEObject> idToMetaInstances, Map<InternalEObject, CDOID> metaInstanceToIDs)
  {
    range = range.increase();
    CDOID id = range.getUpperBound();
    if (TRACER.isEnabled())
    {
      TRACER.format("Registering meta instance: {0} <-> {1}", id, metaInstance);
    }

    if (idToMetaInstances != null)
    {
      if (idToMetaInstances.put(id, metaInstance) != null)
      {
        throw new IllegalStateException("Duplicate meta ID: " + id + " --> " + metaInstance);
      }
    }

    if (metaInstanceToIDs != null)
    {
      if (metaInstanceToIDs.put(metaInstance, id) != null)
      {
        throw new IllegalStateException("Duplicate metaInstance: " + metaInstance + " --> " + id);
      }
    }

    for (EObject content : metaInstance.eContents())
    {
      range = registerMetaInstance((InternalEObject)content, range, idToMetaInstances, metaInstanceToIDs);
    }

    return range;
  }

  /**
   * @since 2.0
   */
  public static InternalCDOViewSet prepareResourceSet(ResourceSet resourceSet)
  {
    InternalCDOViewSet viewSet = null;
    synchronized (resourceSet)
    {
      viewSet = (InternalCDOViewSet)CDOUtil.getViewSet(resourceSet);
      if (viewSet == null)
      {
        viewSet = new CDOViewSetImpl();
        resourceSet.eAdapters().add(viewSet);
      }
    }

    return viewSet;
  }

  public static CDOSessionImpl createSession()
  {
    return new CDOSessionImpl();
  }
}
