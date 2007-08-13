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
import org.eclipse.emf.cdo.server.IStoreReader;
import org.eclipse.emf.cdo.server.IView;
import org.eclipse.emf.cdo.server.SessionCreationException;
import org.eclipse.emf.cdo.server.IView.Type;

import org.eclipse.net4j.internal.util.container.Container;
import org.eclipse.net4j.internal.util.lifecycle.LifecycleEventAdapter;
import org.eclipse.net4j.util.ImplementationError;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.lifecycle.ILifecycle;

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author Eike Stepper
 */
public class Session extends Container<IView> implements ISession, CDOIDProvider
{
  private SessionManager sessionManager;

  private CDOServerProtocol protocol;

  private int sessionID;

  private ConcurrentMap<Integer, View> views = new ConcurrentHashMap();

  private Set<CDOID> knownObjects = new HashSet();

  private IListener protocolListener = new LifecycleEventAdapter()
  {
    @Override
    protected void onDeactivated(ILifecycle lifecycle)
    {
      deactivate();
    }
  };

  public Session(SessionManager sessionManager, CDOServerProtocol protocol, int sessionID)
      throws SessionCreationException
  {
    this.sessionManager = sessionManager;
    this.protocol = protocol;
    this.sessionID = sessionID;

    protocol.addListener(protocolListener);
    try
    {
      activate();
    }
    catch (Exception ex)
    {
      throw new SessionCreationException(ex);
    }
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

  @Override
  public boolean isEmpty()
  {
    return views.isEmpty();
  }

  public View[] getViews()
  {
    return views.values().toArray(new View[views.size()]);
  }

  public View getView(int viewID)
  {
    return views.get(viewID);
  }

  public void notifyViewsChanged(Session session, int viewID, byte kind)
  {
    if (kind == CDOProtocolConstants.VIEW_CLOSED)
    {
      View view = views.remove(viewID);
      if (view != null)
      {
        fireElementRemovedEvent(view);
      }
    }
    else
    {
      IView.Type viewType = getViewType(kind);
      View view = new View(this, viewID, viewType);
      views.put(viewID, view);
      fireElementAddedEvent(view);
    }
  }

  private Type getViewType(byte kind)
  {
    switch (kind)
    {
    case CDOProtocolConstants.VIEW_TRANSACTION:
      return Type.TRANSACTION;
    case CDOProtocolConstants.VIEW_READONLY:
      return Type.READONLY;
    case CDOProtocolConstants.VIEW_AUDIT:
      return Type.AUDIT;
    }

    throw new ImplementationError("Invalid kind: " + kind);
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

    IStoreReader storeReader = StoreUtil.getReader();
    CDOClassRef type = storeReader.readObjectType(id);

    knownObjects.add(id);
    return CDOIDImpl.create(id.getValue(), type);
  }

  @Override
  public String toString()
  {
    return MessageFormat.format("Session[{0}, {1}]", sessionID, protocol.getChannel());
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    protocol.removeListener(protocolListener);
    sessionManager.sessionClosed(this);
    super.doDeactivate();
  }
}
