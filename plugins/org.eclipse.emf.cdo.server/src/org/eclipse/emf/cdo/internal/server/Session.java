/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
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

import org.eclipse.emf.cdo.internal.protocol.revision.InternalCDORevision;
import org.eclipse.emf.cdo.internal.server.bundle.OM;
import org.eclipse.emf.cdo.internal.server.protocol.CDOServerProtocol;
import org.eclipse.emf.cdo.internal.server.protocol.InvalidationNotification;
import org.eclipse.emf.cdo.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.protocol.CDOProtocolView.Type;
import org.eclipse.emf.cdo.protocol.id.CDOID;
import org.eclipse.emf.cdo.protocol.id.CDOIDObject;
import org.eclipse.emf.cdo.protocol.id.CDOIDProvider;
import org.eclipse.emf.cdo.protocol.model.CDOClass;
import org.eclipse.emf.cdo.protocol.model.CDOClassRef;
import org.eclipse.emf.cdo.protocol.model.CDOFeature;
import org.eclipse.emf.cdo.server.ISession;
import org.eclipse.emf.cdo.server.IView;
import org.eclipse.emf.cdo.server.SessionCreationException;
import org.eclipse.emf.cdo.server.StoreUtil;

import org.eclipse.net4j.internal.util.container.Container;
import org.eclipse.net4j.internal.util.lifecycle.LifecycleEventAdapter;
import org.eclipse.net4j.util.ImplementationError;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.lifecycle.ILifecycle;

import java.text.MessageFormat;
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

  private ConcurrentMap<Integer, IView> views = new ConcurrentHashMap<Integer, IView>();

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

  public IView getView(int viewID)
  {
    return views.get(viewID);
  }

  public void notifyViewsChanged(Session session, int viewID, byte kind)
  {
    if (kind == CDOProtocolConstants.VIEW_CLOSED)
    {
      IView view = views.remove(viewID);
      if (view != null)
      {
        fireElementRemovedEvent(view);
      }
    }
    else
    {
      IView view = createView(kind, viewID);
      views.put(viewID, view);
      fireElementAddedEvent(view);
    }
  }

  private IView createView(byte kind, int viewID)
  {
    switch (kind)
    {
    case CDOProtocolConstants.VIEW_TRANSACTION:
      return new Transaction(this, viewID);

    case CDOProtocolConstants.VIEW_READONLY:
      return new View(this, viewID, Type.READONLY);

    case CDOProtocolConstants.VIEW_AUDIT:
      return new View(this, viewID, Type.AUDIT);

    default:
      throw new ImplementationError("Invalid kind: " + kind);
    }
  }

  public void notifyInvalidation(long timeStamp, List<CDOID> dirtyIDs)
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

    CDOIDObject objectID = (CDOIDObject)id;
    if (objectID.getClassRef() == null)
    {
      CDOClassRef classRef = getClassRef(objectID);
      objectID = objectID.asLegacy(classRef);
    }

    return objectID;
  }

  public CDOClassRef getClassRef(CDOID id)
  {
    RevisionManager revisionManager = sessionManager.getRepository().getRevisionManager();
    CDOClass cdoClass = revisionManager.getObjectType(id);
    return cdoClass != null ? cdoClass.createClassRef() : StoreUtil.getReader().readObjectType(id);
  }

  /**
   * TODO I can't see how recursion is controlled/limited
   */
  public void collectContainedRevisions(InternalCDORevision revision, int referenceChunk, Set<CDOID> revisions,
      List<InternalCDORevision> additionalRevisions)
  {
    RevisionManager revisionManager = getSessionManager().getRepository().getRevisionManager();
    CDOClass cdoClass = revision.getCDOClass();
    CDOFeature[] features = cdoClass.getAllFeatures();
    for (int i = 0; i < features.length; i++)
    {
      CDOFeature feature = features[i];
      if (feature.isReference() && !feature.isMany() && feature.isContainment())
      {
        Object value = revision.getValue(feature);
        if (value instanceof CDOID)
        {
          CDOID id = (CDOID)value;
          if (!id.isNull() && !revisions.contains(id))
          {
            InternalCDORevision containedRevision = revisionManager.getRevision(id, referenceChunk);
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
