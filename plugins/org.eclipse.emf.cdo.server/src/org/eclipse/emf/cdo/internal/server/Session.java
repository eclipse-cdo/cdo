/***************************************************************************
 * Copyright (c) 2004 - 2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - https://bugs.eclipse.org/bugs/show_bug.cgi?id=201266
 **************************************************************************/
package org.eclipse.emf.cdo.internal.server;

import org.eclipse.emf.cdo.internal.protocol.CDOIDImpl;
import org.eclipse.emf.cdo.internal.protocol.model.CDOClassImpl;
import org.eclipse.emf.cdo.internal.protocol.model.CDOFeatureImpl;
import org.eclipse.emf.cdo.internal.protocol.revision.CDOIDProvider;
import org.eclipse.emf.cdo.internal.protocol.revision.CDORevisionImpl;
import org.eclipse.emf.cdo.internal.server.bundle.OM;
import org.eclipse.emf.cdo.internal.server.protocol.CDOServerProtocol;
import org.eclipse.emf.cdo.internal.server.protocol.InvalidationNotification;
import org.eclipse.emf.cdo.protocol.CDOID;
import org.eclipse.emf.cdo.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.protocol.model.CDOClassRef;
import org.eclipse.emf.cdo.server.ISession;
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
import java.util.List;
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

  private boolean disableLegacyObjects;

  private ConcurrentMap<Integer, View> views = new ConcurrentHashMap<Integer, View>();

  private Set<CDOID> knownTypes = new HashSet<CDOID>();

  private IListener protocolListener = new LifecycleEventAdapter()
  {
    @Override
    protected void onDeactivated(ILifecycle lifecycle)
    {
      deactivate();
    }
  };

  public Session(SessionManager sessionManager, CDOServerProtocol protocol, int sessionID, boolean disableLegacyObjects)
      throws SessionCreationException
  {
    this.sessionManager = sessionManager;
    this.protocol = protocol;
    this.sessionID = sessionID;
    this.disableLegacyObjects = disableLegacyObjects;
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

  public boolean isDisableLegacyObjects()
  {
    return disableLegacyObjects;
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

  public void notifyInvalidation(long timeStamp, CDOID[] dirtyIDs)
  {
    try
    {
      new InvalidationNotification(protocol.getChannel(), timeStamp, dirtyIDs).send();
    }
    catch (Exception ex)
    {
      OM.LOG.error(ex);
    }
  }

  public CDOID provideCDOID(Object idObject)
  {
    CDOID id = (CDOID)idObject;
    if (disableLegacyObjects || id.isNull() || id.isMeta())
    {
      return id;
    }

    Repository repository = sessionManager.getRepository();
    if (repository.isRememberingKnownTypes())
    {
      if (knownTypes.contains(id))
      {
        return id;
      }

      knownTypes.add(id);
    }

    CDOClassRef type = repository.getTypeManager().getObjectType(StoreUtil.getReader(), id);
    return CDOIDImpl.create(id.getValue(), type);
  }

  /**
   * TODO I can't see how recursion is controlled/limited
   */
  public void collectContainedRevisions(CDORevisionImpl revision, int referenceChunk, Set<CDOID> revisions,
      List<CDORevisionImpl> additionalRevisions)
  {
    RevisionManager revisionManager = getSessionManager().getRepository().getRevisionManager();
    CDOClassImpl cdoClass = revision.getCDOClass();
    CDOFeatureImpl[] features = cdoClass.getAllFeatures();
    for (int i = 0; i < features.length; i++)
    {
      CDOFeatureImpl feature = features[i];
      if (feature.isReference() && !feature.isMany() && feature.isContainment())
      {
        Object value = revision.getValue(feature);
        if (value instanceof CDOID)
        {
          CDOID id = (CDOID)value;
          if (!id.isNull() && !revisions.contains(id))
          {
            CDORevisionImpl containedRevision = revisionManager.getRevision(id, referenceChunk);
            revisions.add(id);
            additionalRevisions.add(containedRevision);

            collectContainedRevisions(containedRevision, referenceChunk, revisions, additionalRevisions);
          }
        }
      }
    }
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
