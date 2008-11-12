/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
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
 **************************************************************************/
package org.eclipse.emf.cdo.internal.server;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDAndVersion;
import org.eclipse.emf.cdo.common.id.CDOIDProvider;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.model.CDOClass;
import org.eclipse.emf.cdo.common.model.CDOFeature;
import org.eclipse.emf.cdo.common.model.CDOPackageURICompressor;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.internal.server.bundle.OM;
import org.eclipse.emf.cdo.internal.server.protocol.CDOServerProtocol;
import org.eclipse.emf.cdo.internal.server.protocol.CommitNotificationRequest;
import org.eclipse.emf.cdo.server.IAudit;
import org.eclipse.emf.cdo.server.ISession;
import org.eclipse.emf.cdo.server.ITransaction;
import org.eclipse.emf.cdo.server.IView;
import org.eclipse.emf.cdo.server.SessionCreationException;
import org.eclipse.emf.cdo.spi.common.InternalCDORevision;

import org.eclipse.net4j.channel.IChannel;
import org.eclipse.net4j.util.ReflectUtil.ExcludeFromDump;
import org.eclipse.net4j.util.container.Container;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.io.ExtendedDataInput;
import org.eclipse.net4j.util.io.ExtendedDataOutput;
import org.eclipse.net4j.util.io.StringCompressor;
import org.eclipse.net4j.util.lifecycle.ILifecycle;
import org.eclipse.net4j.util.lifecycle.LifecycleEventAdapter;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;

import java.io.IOException;
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
public class Session extends Container<IView> implements ISession, CDOIDProvider, CDOPackageURICompressor
{
  private SessionManager sessionManager;

  private CDOServerProtocol protocol;

  private int sessionID;

  private boolean passiveUpdateEnabled = true;

  private ConcurrentMap<Integer, IView> views = new ConcurrentHashMap<Integer, IView>();

  @ExcludeFromDump
  private transient StringCompressor packageURICompressor = new StringCompressor(false);

  @ExcludeFromDump
  private IListener protocolListener = new LifecycleEventAdapter()
  {
    @Override
    protected void onDeactivated(ILifecycle lifecycle)
    {
      deactivate();
    }
  };

  /**
   * @since 2.0
   */
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

  /**
   * @since 2.0
   */
  public String getUserID()
  {
    return protocol.getChannel().getUserID();
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
  public void handleCommitNotification(long timeStamp, List<CDOIDAndVersion> dirtyIDs, List<CDOID> detachedObjects,
      List<CDORevisionDelta> deltas)
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
      if (!dirtyIDs.isEmpty() || !newDeltas.isEmpty() || !detachedObjects.isEmpty())
      {
        IChannel channel = protocol.getChannel();
        if (LifecycleUtil.isActive(channel))
        {
          new CommitNotificationRequest(channel, timeStamp, dirtyIDs, detachedObjects, newDeltas).sendAsync();
        }
        else
        {
          OM.LOG.warn("Session channel is inactive: " + this);
        }
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
   */
  public void collectContainedRevisions(InternalCDORevision revision, int referenceChunk, Set<CDOID> revisions,
      List<CDORevision> additionalRevisions)
  {
    RevisionManager revisionManager = (RevisionManager)getSessionManager().getRepository().getRevisionManager();
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

  /**
   * @since 2.0
   */
  public void writePackageURI(ExtendedDataOutput out, String uri) throws IOException
  {
    packageURICompressor.write(out, uri);
  }

  /**
   * @since 2.0
   */
  public String readPackageURI(ExtendedDataInput in) throws IOException
  {
    return packageURICompressor.read(in);
  }

  @Override
  public String toString()
  {
    return MessageFormat.format("Session[{0}]", sessionID);
  }

  /**
   * @since 2.0
   */
  public void close()
  {
    deactivate();
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
    protocol = null;
    protocolListener = null;

    for (IView view : getViewsArray())
    {
      view.close();
    }

    views = null;
    sessionManager.sessionClosed(this);
    sessionManager = null;
    packageURICompressor = null;
    super.doDeactivate();
  }
}
