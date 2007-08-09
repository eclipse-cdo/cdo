/***************************************************************************
 * Copyright (c) 2004-2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.internal.server;

import org.eclipse.emf.cdo.internal.protocol.CDOIDImpl;
import org.eclipse.emf.cdo.internal.protocol.revision.CDOIDProvider;
import org.eclipse.emf.cdo.internal.protocol.revision.CDORevisionImpl;
import org.eclipse.emf.cdo.internal.server.bundle.OM;
import org.eclipse.emf.cdo.internal.server.protocol.CDOServerProtocol;
import org.eclipse.emf.cdo.internal.server.protocol.InvalidationRequest;
import org.eclipse.emf.cdo.protocol.CDOID;
import org.eclipse.emf.cdo.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.protocol.model.CDOClassRef;
import org.eclipse.emf.cdo.server.ISession;
import org.eclipse.emf.cdo.server.ISessionViewsEvent;
import org.eclipse.emf.cdo.server.IView;

import org.eclipse.net4j.internal.util.container.SingleDeltaContainerEvent;
import org.eclipse.net4j.internal.util.event.Notifier;
import org.eclipse.net4j.util.container.IContainerDelta;
import org.eclipse.net4j.util.container.IContainerDelta.Kind;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author Eike Stepper
 */
public class Session extends Notifier implements ISession, CDOIDProvider
{
  private SessionManager sessionManager;

  private CDOServerProtocol protocol;

  private int sessionID;

  private ConcurrentMap<Integer, View> views = new ConcurrentHashMap();

  private Set<CDOID> knownObjects = new HashSet();

  public Session(SessionManager sessionManager, CDOServerProtocol protocol, int sessionID)
  {
    this.sessionManager = sessionManager;
    this.protocol = protocol;
    this.sessionID = sessionID;
  }

  public SessionManager getSessionManager()
  {
    return sessionManager;
  }

  public int getSessionID()
  {
    return sessionID;
  }

  public CDOServerProtocol getProtocol()
  {
    return protocol;
  }

  public View[] getElements()
  {
    return getViews();
  }

  public boolean isEmpty()
  {
    return views.isEmpty();
  }

  public View[] getViews()
  {
    return views.values().toArray(new View[views.size()]);
  }

  public void notifyViewsChanged(Session session, int viewID, byte kind)
  {
    switch (kind)
    {
    case CDOProtocolConstants.VIEW_ADDED:
    {
      View view = new View(this, viewID);
      views.put(viewID, view);
      fireEvent(new ViewsEvent(view, IContainerDelta.Kind.ADDED));
      break;
    }

    case CDOProtocolConstants.VIEW_REMOVED:
    {
      View view = views.remove(viewID);
      if (view != null)
      {
        fireEvent(new ViewsEvent(view, IContainerDelta.Kind.REMOVED));
      }

      break;
    }
    }
  }

  public void notifyInvalidation(long timeStamp, CDORevisionImpl[] dirtyObjects)
  {
    try
    {
      new InvalidationRequest(protocol.getChannel(), timeStamp, dirtyObjects).send();
    }
    catch (Exception ex)
    {
      OM.LOG.error(ex);
    }
  }

  public CDOID provideCDOID(Object idObject)
  {
    CDOID id = (CDOID)idObject;
    if (id.isNull() || id.isMeta())
    {
      return id;
    }

    if (knownObjects.contains(id))
    {
      // TODO On client-side add a check if the id is really known!
      return id;
    }

    knownObjects.add(id);
    CDOClassRef type = sessionManager.getRepository().getObjectType(id);
    return CDOIDImpl.create(id.getValue(), type);
  }

  private final class ViewsEvent extends SingleDeltaContainerEvent<IView> implements ISessionViewsEvent
  {
    private static final long serialVersionUID = 1L;

    private ViewsEvent(IView view, Kind kind)
    {
      super(Session.this, view, kind);
    }

    public IView getView()
    {
      return getDeltaElement();
    }
  }
}
