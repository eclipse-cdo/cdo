/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - http://bugs.eclipse.org/201266
 *    Simon McDuff - http://bugs.eclipse.org/230832
 *    Simon McDuff - http://bugs.eclipse.org/233490
 *    Simon McDuff - http://bugs.eclipse.org/213402
 *    Caspar De Groot - https://bugs.eclipse.org/333260
 */
package org.eclipse.emf.cdo.internal.server;

import org.eclipse.emf.cdo.common.CDOCommonSession;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDAndVersion;
import org.eclipse.emf.cdo.common.id.CDOIDProvider;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.model.CDOModelUtil;
import org.eclipse.emf.cdo.common.model.CDOPackageUnit;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.internal.server.bundle.OM;
import org.eclipse.emf.cdo.internal.server.protocol.CDOServerProtocol;
import org.eclipse.emf.cdo.internal.server.protocol.CommitNotificationRequest;
import org.eclipse.emf.cdo.internal.server.protocol.RemoteSessionNotificationRequest;
import org.eclipse.emf.cdo.server.IAudit;
import org.eclipse.emf.cdo.server.ISession;
import org.eclipse.emf.cdo.server.ITransaction;
import org.eclipse.emf.cdo.server.IView;
import org.eclipse.emf.cdo.server.SessionCreationException;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;

import org.eclipse.net4j.channel.IChannel;
import org.eclipse.net4j.util.ReflectUtil.ExcludeFromDump;
import org.eclipse.net4j.util.container.Container;
import org.eclipse.net4j.util.event.EventUtil;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.lifecycle.ILifecycle;
import org.eclipse.net4j.util.lifecycle.LifecycleEventAdapter;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.log.OMLogger;
import org.eclipse.net4j.util.options.IOptionsContainer;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author Eike Stepper
 */
public class Session extends Container<IView> implements ISession, CDOIDProvider, CDOCommonSession.Options
{
  private SessionManager sessionManager;

  private CDOServerProtocol protocol;

  private int sessionID;

  private String userID;

  private boolean passiveUpdateEnabled = true;

  private ConcurrentMap<Integer, IView> views = new ConcurrentHashMap<Integer, IView>();

  @ExcludeFromDump
  private IListener protocolListener = new LifecycleEventAdapter()
  {
    @Override
    protected void onDeactivated(ILifecycle lifecycle)
    {
      deactivate();
    }
  };

  private boolean subscribed;

  /**
   * @since 2.0
   */
  public Session(SessionManager sessionManager, CDOServerProtocol protocol, int sessionID, String userID)
      throws SessionCreationException
  {
    this.sessionManager = sessionManager;
    this.protocol = protocol;
    this.sessionID = sessionID;
    this.userID = userID;
    EventUtil.addListener(protocol, protocolListener);

    try
    {
      activate();
    }
    catch (Exception ex)
    {
      throw new SessionCreationException(ex);
    }
  }

  /**
   * @since 2.0
   */
  public Options options()
  {
    return this;
  }

  /**
   * @since 2.0
   */
  public IOptionsContainer getContainer()
  {
    return this;
  }

  public SessionManager getSessionManager()
  {
    return sessionManager;
  }

  public int getSessionID()
  {
    return sessionID;
  }

  /**
   * @since 2.0
   */
  public String getUserID()
  {
    return userID;
  }

  /**
   * @since 2.0
   */
  public boolean isSubscribed()
  {
    return subscribed;
  }

  /**
   * @since 2.0
   */
  public void setSubscribed(boolean subscribed)
  {
    checkActive();
    if (this.subscribed != subscribed)
    {
      this.subscribed = subscribed;
      byte opcode = subscribed ? CDOProtocolConstants.REMOTE_SESSION_SUBSCRIBED
          : CDOProtocolConstants.REMOTE_SESSION_UNSUBSCRIBED;
      sessionManager.handleRemoteSessionNotification(opcode, this);
    }
  }

  /**
   * @since 2.0
   */
  public boolean isPassiveUpdateEnabled()
  {
    return passiveUpdateEnabled;
  }

  /**
   * @since 2.0
   */
  public void setPassiveUpdateEnabled(boolean passiveUpdateEnabled)
  {
    checkActive();
    this.passiveUpdateEnabled = passiveUpdateEnabled;
  }

  public View[] getElements()
  {
    checkActive();
    return getViews();
  }

  @Override
  public boolean isEmpty()
  {
    checkActive();
    return views.isEmpty();
  }

  public View[] getViews()
  {
    checkActive();
    return getViewsArray();
  }

  private View[] getViewsArray()
  {
    return views.values().toArray(new View[views.size()]);
  }

  public IView getView(int viewID)
  {
    checkActive();
    return views.get(viewID);
  }

  /**
   * @since 2.0
   */
  public IView openView(int viewID)
  {
    checkActive();
    IView view = new View(this, viewID);
    addView(view);
    return view;
  }

  /**
   * @since 2.0
   */
  public IAudit openAudit(int viewID, long timeStamp)
  {
    checkActive();
    IAudit audit = new Audit(this, viewID, timeStamp);
    addView(audit);
    return audit;
  }

  /**
   * @since 2.0
   */
  public ITransaction openTransaction(int viewID)
  {
    checkActive();
    ITransaction transaction = new Transaction(this, viewID);
    addView(transaction);
    return transaction;
  }

  private void addView(IView view)
  {
    checkActive();
    views.put(view.getViewID(), view);
    fireElementAddedEvent(view);
  }

  /**
   * @since 2.0
   */
  public void viewClosed(View view)
  {
    if (views.remove(view.getViewID()) == view)
    {
      view.doClose();
      fireElementRemovedEvent(view);
    }
  }

  /**
   * @since 2.0
   */
  public void handleCommitNotification(long timeStamp, CDOPackageUnit[] packageUnits, List<CDOIDAndVersion> dirtyIDs,
      List<CDOID> detachedObjects, List<CDORevisionDelta> deltas)
  {
    if (!isPassiveUpdateEnabled())
    {
      dirtyIDs = Collections.emptyList();
    }

    // Look if someone needs to know something about modified objects
    List<CDORevisionDelta> newDeltas = new ArrayList<CDORevisionDelta>();
    for (CDORevisionDelta delta : deltas)
    {
      CDOID lookupID = delta.getID();
      for (View view : getViews())
      {
        if (view.hasSubscription(lookupID))
        {
          newDeltas.add(delta);
          break;
        }
      }
    }

    if (!isPassiveUpdateEnabled())
    {
      List<CDOID> subDetached = new ArrayList<CDOID>();
      for (CDOID id : detachedObjects)
      {
        for (View view : getViews())
        {
          if (view.hasSubscription(id))
          {
            subDetached.add(id);
            break;
          }
        }
      }

      detachedObjects = subDetached;
    }

    try
    {
      if (!dirtyIDs.isEmpty() || !newDeltas.isEmpty() || !detachedObjects.isEmpty() || packageUnits.length > 0)
      {
        IChannel channel = protocol.getChannel();
        if (LifecycleUtil.isActive(channel))
        {
          new CommitNotificationRequest(channel, timeStamp, packageUnits, dirtyIDs, detachedObjects, newDeltas)
              .sendAsync();
        }
        else
        {
          OM.LOG.warn("Session channel is inactive: " + this); //$NON-NLS-1$
        }
      }
    }
    catch (Exception ex)
    {
      OM.LOG.error(ex);
    }
  }

  /**
   * @since 2.0
   */
  public void handleRemoteSessionNotification(byte opcode, ISession session)
  {
    try
    {
      IChannel channel = protocol.getChannel();
      if (LifecycleUtil.isActive(channel))
      {
        new RemoteSessionNotificationRequest(channel, opcode, session).sendAsync();
      }
      else
      {
        OM.LOG.warn("Session channel is inactive: " + this); //$NON-NLS-1$
      }
    }
    catch (Exception ex)
    {
      OM.LOG.error(ex);
    }
  }

  public CDOID provideCDOID(Object idObject)
  {
    return (CDOID)idObject;
  }

  /**
   * TODO I can't see how recursion is controlled/limited
   * 
   * @since 2.0
   */
  public void collectContainedRevisions(InternalCDORevision revision, int referenceChunk, Set<CDOID> revisions,
      List<CDORevision> additionalRevisions)
  {
    RevisionManager revisionManager = (RevisionManager)getSessionManager().getRepository().getRevisionManager();
    EClass eClass = revision.getEClass();
    EStructuralFeature[] features = CDOModelUtil.getAllPersistentFeatures(eClass);
    for (int i = 0; i < features.length; i++)
    {
      EStructuralFeature feature = features[i];
      // TODO Clarify feature maps
      if (feature instanceof EReference && !feature.isMany() && ((EReference)feature).isContainment())
      {
        Object value = revision.getValue(feature);
        if (value instanceof CDOID)
        {
          CDOID id = (CDOID)value;
          if (!CDOIDUtil.isNull(id) && !revisions.contains(id))
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
    return MessageFormat.format("Session[{0}]", sessionID); //$NON-NLS-1$
  }

  /**
   * @since 2.0
   */
  public void close()
  {
    LifecycleUtil.deactivate(this, OMLogger.Level.DEBUG);
  }

  /**
   * @since 2.0
   */
  public boolean isClosed()
  {
    return !isActive();
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    protocol.removeListener(protocolListener);
    protocolListener = null;

    LifecycleUtil.deactivate(protocol, OMLogger.Level.DEBUG);
    protocol = null;

    for (IView view : getViewsArray())
    {
      view.close();
    }

    views = null;
    sessionManager.sessionClosed(this);
    sessionManager = null;
    super.doDeactivate();
  }
}
