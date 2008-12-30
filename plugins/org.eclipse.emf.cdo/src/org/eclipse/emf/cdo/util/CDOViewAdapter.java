/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Victor Roldan Betancort - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.util;

import org.eclipse.emf.cdo.CDOView;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;

/**
 * Adapter meant to carry a <code>CDOView</code> associated with (and needed by) certain <code>CDOResource</code>
 * 
 * @author Victor Roldan Betancort
 * @since 2.0
 */
public class CDOViewAdapter implements Adapter
{
  private CDOView view;

  public CDOViewAdapter(CDOView view)
  {
    this.view = view;
  }

  public CDOView getView()
  {
    return view;
  }

  public Notifier getTarget()
  {
    return null;
  }

  public void setTarget(Notifier newTarget)
  {
  }

  public boolean isAdapterForType(Object type)
  {
    return false;
  }

  public void notifyChanged(Notification notification)
  {
  }
}
