/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - http://bugs.eclipse.org/233490
 **************************************************************************/
package org.eclipse.emf.cdo.internal.server;

import org.eclipse.emf.cdo.common.CDOProtocolView;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.IView;

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public class View implements IView
{
  private IRepository repository;

  private Session session;

  private int viewID;

  private Set<CDOID> changeSubscriptionIDs = new HashSet<CDOID>();

  /**
   * @since 2.0
   */
  public View(Session session, int viewID)
  {
    repository = session.getSessionManager().getRepository();
    this.session = session;
    this.viewID = viewID;
  }

  /**
   * @since 2.0
   */
  public IRepository getRepository()
  {
    return repository;
  }

  public Session getSession()
  {
    return session;
  }

  public int getViewID()
  {
    return viewID;
  }

  public CDOProtocolView.Type getViewType()
  {
    return CDOProtocolView.Type.READONLY;
  }

  /**
   * @since 2.0
   */
  public synchronized void subscribe(CDOID id)
  {
    changeSubscriptionIDs.add(id);
  }

  /**
   * @since 2.0
   */
  public synchronized void unsubscribe(CDOID id)
  {
    changeSubscriptionIDs.remove(id);
  }

  /**
   * @since 2.0
   */
  public synchronized boolean hasSubscription(CDOID id)
  {
    return changeSubscriptionIDs.contains(id);
  }

  /**
   * @since 2.0
   */
  public synchronized void clearChangeSubscription()
  {
    changeSubscriptionIDs.clear();
  }

  @Override
  public String toString()
  {
    return MessageFormat.format("View[{0}]", viewID);
  }
}
