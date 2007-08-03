/***************************************************************************
 * Copyright (c) 2004 - 2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.internal.cdo;

import org.eclipse.emf.cdo.CDOState;
import org.eclipse.emf.cdo.protocol.CDOProtocolConstants;

import org.eclipse.net4j.internal.util.om.trace.ContextTracer;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.internal.cdo.bundle.OM;

/**
 * @author Eike Stepper
 */
public class CDOAdapterImpl extends CDOLegacyImpl implements Adapter.Internal
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_OBJECT, CDOAdapterImpl.class);

  public CDOAdapterImpl()
  {
  }

  public boolean isAdapterForType(Object type)
  {
    return type == CDOAdapterImpl.class;
  }

  public InternalEObject getTarget()
  {
    return instance;
  }

  public void setTarget(Notifier newTarget)
  {
    if (newTarget instanceof InternalEObject)
    {
      instance = (InternalEObject)newTarget;
    }
    else
    {
      throw new IllegalArgumentException("Not an InternalEObject: " + newTarget.getClass().getName());
    }
  }

  public void unsetTarget(Notifier oldTarget)
  {
    if (oldTarget instanceof InternalEObject)
    {
      if (instance == oldTarget)
      {
        instance = null;
      }
    }
    else
    {
      throw new IllegalArgumentException("Not an InternalEObject: " + oldTarget.getClass().getName());
    }
  }

  public void notifyChanged(Notification msg)
  {
    switch (msg.getEventType())
    {
    case Notification.ADD:
    case Notification.ADD_MANY:
    case Notification.REMOVE:
    case Notification.REMOVE_MANY:
    case Notification.MOVE:
    case Notification.SET:
    case Notification.UNSET:
      InternalEObject notifier = (InternalEObject)msg.getNotifier();
      if (notifier == instance && !notifier.eIsProxy())
      {
        CDOStateMachine.INSTANCE.write(this);
      }
    }
  }

  @Override
  public CDOState cdoInternalSetState(CDOState state)
  {
    CDOState oldState = super.cdoInternalSetState(state);
    if (oldState != state)
    {
      // TODO Check if EMF proxy resolution is needed at all
      if (state == CDOState.PROXY)
      {
        if (!instance.eIsProxy())
        {
          URI uri = URI.createURI(CDOProtocolConstants.PROTOCOL_NAME + ":proxy#" + id);
          if (TRACER.isEnabled())
          {
            TRACER.format("Setting proxyURI {0} for {1}", uri, instance);
          }

          instance.eSetProxyURI(uri);
        }
      }
      else
      {
        if (instance.eIsProxy())
        {
          if (TRACER.isEnabled())
          {
            TRACER.format("Unsetting proxyURI for {1}", instance);
          }

          instance.eSetProxyURI(null);
        }
      }
    }

    return oldState;
  }

  // /**
  // * This implementation simply asks the view to convert the ID which can
  // result
  // * in a LoadObjectRequest being sent. Basically this leads to loading the
  // * whole subtree (i.e. resource).
  // * <p>
  // * TODO Investigate the use of proxies here
  // *
  // * @see CDOCallbackImpl
  // */
  // @Override
  // protected Object convertID(CDOViewImpl view, CDOID id)
  // {
  // return view.convertIDToObject(id);
  // }
}
