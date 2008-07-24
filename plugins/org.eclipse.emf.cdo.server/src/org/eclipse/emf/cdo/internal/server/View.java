/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - https://bugs.eclipse.org/233490
 **************************************************************************/
package org.eclipse.emf.cdo.internal.server;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.server.IView;

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public class View implements IView
{
  private Session session;

  private int viewID;

  private Type viewType;

  private Set<CDOID> changeSubscriptionIDs = new HashSet<CDOID>();

  public View(Session session, int viewID, Type viewType)
  {
    this.session = session;
    this.viewID = viewID;
    this.viewType = viewType;
  }

  public Session getSession()
  {
    return session;
  }

  public int getViewID()
  {
    return viewID;
  }

  public Type getViewType()
  {
    return viewType;
  }

  synchronized public void subscribe(CDOID id)
  {
    changeSubscriptionIDs.add(id);
  }

  synchronized public void unsubscribe(CDOID id)
  {
    changeSubscriptionIDs.remove(id);
  }

  synchronized public boolean hasSubscription(CDOID id)
  {
    return changeSubscriptionIDs.contains(id);
  }

  synchronized public void clearChangeSubscription()
  {
    changeSubscriptionIDs.clear();
  }

  @Override
  public String toString()
  {
    return MessageFormat.format("View({0}, {1})", viewID, viewType);
  }
}
