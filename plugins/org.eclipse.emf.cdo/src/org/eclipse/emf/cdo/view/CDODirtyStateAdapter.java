/*
 * Copyright (c) 2012, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.view;

import org.eclipse.emf.cdo.transaction.CDOTransactionFinishedEvent;
import org.eclipse.emf.cdo.transaction.CDOTransactionStartedEvent;

import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;

/**
 * A {@link IListener listener} that calls the {@link #onDirtyStateChanged(boolean)} method when the {@link CDOView#isDirty() dirty} state
 * of the {@link CDOView view} this listener is {@link CDOView#addListener(IListener) registered} with has changed.
 *
 * @author Eike Stepper
 * @since 4.2
 */
public abstract class CDODirtyStateAdapter implements IListener
{
  @Override
  public void notifyEvent(IEvent event)
  {
    if (event instanceof CDOTransactionStartedEvent)
    {
      onDirtyStateChanged(true);
    }
    else if (event instanceof CDOTransactionFinishedEvent)
    {
      onDirtyStateChanged(false);
    }
    else
    {
      notifyOtherEvent(event);
    }
  }

  protected void notifyOtherEvent(IEvent event)
  {
  }

  protected abstract void onDirtyStateChanged(boolean dirty);
}
