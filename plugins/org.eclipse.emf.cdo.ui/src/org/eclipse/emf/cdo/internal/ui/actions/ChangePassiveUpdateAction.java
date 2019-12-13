/*
 * Copyright (c) 2009, 2011, 2012, 2015, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Victor Roldan Betancort - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.ui.actions;

import org.eclipse.emf.cdo.internal.ui.messages.Messages;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.session.CDOSession.Options;

import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * Changes the state of the {@link Options#isPassiveUpdateEnabled() passive update} option for a given
 * {@link CDOSession session}.
 *
 * @author Victor Roldan Betancort
 * @deprecated As of 4.6 no longer supported.
 */
@Deprecated
public class ChangePassiveUpdateAction extends EditingDomainAction
{
  public static final String ID = "change-passiveupdate"; //$NON-NLS-1$

  private static final String TITLE = Messages.getString("ChangePassiveUpdateAction.1"); //$NON-NLS-1$

  private CDOSession session;

  private IListener passiveUpdateListener = new IListener()
  {
    @Override
    public void notifyEvent(IEvent event)
    {
      if (session != null)
      {
        if (event instanceof CDOSession.Options.PassiveUpdateEvent)
        {
          setChecked(session.options().isPassiveUpdateEnabled());
        }
      }
    }
  };

  public ChangePassiveUpdateAction()
  {
    super(TITLE);
    setId(ID);
  }

  @Override
  protected void doRun(IProgressMonitor progressMonitor) throws Exception
  {
    if (session != null)
    {
      session.options().setPassiveUpdateEnabled(!session.options().isPassiveUpdateEnabled());
    }

    update();
  }

  @Override
  public void update()
  {
    super.update();
    setChecked(session != null ? session.options().isPassiveUpdateEnabled() : false);
  }

  /**
   * Sets the {@link CDOSession session} to be associated with this action.
   */
  public void setSession(CDOSession session)
  {
    if (this.session != null)
    {
      session.options().removeListener(passiveUpdateListener);
    }

    this.session = session;
    if (this.session != null)
    {
      session.options().addListener(passiveUpdateListener);
    }
  }
}
