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
package org.eclipse.emf.cdo.internal.server;

import org.eclipse.emf.cdo.server.IView;

/**
 * @author Eike Stepper
 */
public class View implements IView
{
  private Session session;

  private int viewID;

  private Type viewType;

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
}
