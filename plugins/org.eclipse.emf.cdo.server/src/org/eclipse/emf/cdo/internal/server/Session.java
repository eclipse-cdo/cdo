/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - bug 201266
 *    Simon McDuff - bug 230832
 *    Simon McDuff - bug 233490
 *    Simon McDuff - bug 213402
 */
package org.eclipse.emf.cdo.internal.server;

import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDAndVersion;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.model.CDOModelUtil;
import org.eclipse.emf.cdo.common.model.CDOPackageUnit;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.server.IView;
import org.eclipse.emf.cdo.server.SessionCreationException;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranch;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionManager;
import org.eclipse.emf.cdo.spi.server.ISessionProtocol;
import org.eclipse.emf.cdo.spi.server.InternalSession;
import org.eclipse.emf.cdo.spi.server.InternalSessionManager;
import org.eclipse.emf.cdo.spi.server.InternalTransaction;
import org.eclipse.emf.cdo.spi.server.InternalView;

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
public class Session extends Container<IView> implements InternalSession
{
  private InternalSessionManager manager;

  private ISessionProtocol protocol;

  private int sessionID;

  private String userID;

  private boolean passiveUpdateEnabled = true;

  private ConcurrentMap<Integer, InternalView> views = new ConcurrentHashMap<Integer, InternalView>();

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
  public Session(InternalSessionManager manager, ISessionProtocol protocol, int sessionID, String userID)
      throws SessionCreationException
  {
    this.manager = manager;
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

  public InternalSessionManager getManager()
  {
    return manager;
  }

  public ISessionProtocol getProtocol()
  {
    return protocol;
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
      manager.handleRemoteSessionNotification(opcode, this);
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

  public InternalView[] getElements()
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

  public InternalView[] getViews()
  {
    checkActive();
    return getViewsArray();
  }

  private InternalView[] getViewsArray()
  {
    return views.values().toArray(new InternalView[views.size()]);
  }

  public InternalView getView(int viewID)
  {
    checkActive();
    return views.get(viewID);
  }

  /**
   * @since 2.0
   */
  public InternalView openView(int viewID, CDOBranchPoint branchPoint)
  {
    checkActive();
    InternalView view = new View(this, viewID, branchPoint);
    addView(view);
    return view;
  }

  /**
   * @since 2.0
   */
  public InternalTransaction openTransaction(int viewID, CDOBranchPoint branchPoint)
  {
    checkActive();
    InternalTransaction transaction = new Transaction(this, viewID, branchPoint);
    addView(transaction);
    return transaction;
  }

  private void addView(InternalView view)
  {
    checkActive();
    views.put(view.getViewID(), view);
    fireElementAddedEvent(view);
  }

  /**
   * @since 2.0
   */
  public void viewClosed(InternalView view)
  {
    if (views.remove(view.getViewID()) == view)
    {
      view.doClose();
      fireElementRemovedEvent(view);
    }
  }

  public void handleBranchNotification(InternalCDOBranch branch)
  {
    protocol.sendBranchNotification(branch);
  }

  /**
   * @since 2.0
   */
  public void handleCommitNotification(CDOBranchPoint branchPoint, CDOPackageUnit[] packageUnits,
      List<CDOIDAndVersion> dirtyIDs, List<CDOID> detachedObjects, List<CDORevisionDelta> deltas)
  {
    if (!isPassiveUpdateEnabled())
    {
      dirtyIDs = Collections.emptyList();
    }

    InternalView[] views = getViews();

    // Look if someone needs to know something about modified objects
    List<CDORevisionDelta> newDeltas = new ArrayList<CDORevisionDelta>();
    for (CDORevisionDelta delta : deltas)
    {
      CDOID lookupID = delta.getID();
      for (InternalView view : views)
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
        for (InternalView view : views)
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

    protocol.sendCommitNotification(branchPoint, packageUnits, dirtyIDs, detachedObjects, newDeltas);
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
  public void collectContainedRevisions(InternalCDORevision revision, CDOBranchPoint branchPoint, int referenceChunk,
      Set<CDOID> revisions, List<CDORevision> additionalRevisions)
  {
    InternalCDORevisionManager revisionManager = getManager().getRepository().getRevisionManager();
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
            InternalCDORevision containedRevision = (InternalCDORevision)revisionManager.getRevision(id, branchPoint,
                referenceChunk, CDORevision.DEPTH_NONE, true);
            revisions.add(id);
            additionalRevisions.add(containedRevision);

            // Recurse
            collectContainedRevisions(containedRevision, branchPoint, referenceChunk, revisions, additionalRevisions);
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
    EventUtil.removeListener(protocol, protocolListener);
    protocolListener = null;

    LifecycleUtil.deactivate(protocol, OMLogger.Level.DEBUG);
    protocol = null;

    for (IView view : getViewsArray())
    {
      view.close();
    }

    views = null;
    manager.sessionClosed(this);
    manager = null;
    super.doDeactivate();
  }
}
