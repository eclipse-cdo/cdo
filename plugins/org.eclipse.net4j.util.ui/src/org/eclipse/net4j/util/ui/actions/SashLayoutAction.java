/*
 * Copyright (c) 2007-2009, 2011, 2012, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.ui.actions;

import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.internal.ui.messages.Messages;
import org.eclipse.net4j.util.ui.widgets.SashComposite;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.MenuManager;

/**
 * @author Eike Stepper
 */
public abstract class SashLayoutAction extends SafeAction implements IListener
{
  private SashComposite sashComposite;

  public SashLayoutAction(SashComposite sashComposite, String text)
  {
    super(text, IAction.AS_RADIO_BUTTON);
    this.sashComposite = sashComposite;
    notifyEvent(null);
    sashComposite.addListener(this);
  }

  public SashComposite getSashComposite()
  {
    return sashComposite;
  }

  /**
   * @since 2.0
   */
  public void dispose()
  {
    sashComposite.removeListener(this);
  }

  /**
   * @author Eike Stepper
   */
  public static class Vertical extends SashLayoutAction
  {
    public Vertical(SashComposite sashComposite)
    {
      super(sashComposite, Messages.getString("SashLayoutAction_0")); //$NON-NLS-1$
    }

    @Override
    protected void safeRun() throws Exception
    {
      getSashComposite().setVertical(true);
    }

    /**
     * @since 2.0
     */
    @Override
    public void notifyEvent(IEvent event)
    {
      setChecked(getSashComposite().isVertical());
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class Horizontal extends SashLayoutAction
  {
    public Horizontal(SashComposite sashComposite)
    {
      super(sashComposite, Messages.getString("SashLayoutAction_1")); //$NON-NLS-1$
    }

    @Override
    protected void safeRun() throws Exception
    {
      getSashComposite().setVertical(false);
    }

    /**
     * @since 2.0
     */
    @Override
    public void notifyEvent(IEvent event)
    {
      setChecked(!getSashComposite().isVertical());
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class LayoutMenu extends MenuManager
  {
    private SafeAction verticalAction;

    private SafeAction horizontalAction;

    public LayoutMenu(SashComposite sashComposite)
    {
      super(Messages.getString("SashLayoutAction_2")); //$NON-NLS-1$
      add(verticalAction = new SashLayoutAction.Vertical(sashComposite));
      add(horizontalAction = new SashLayoutAction.Horizontal(sashComposite));
    }

    public SafeAction getVerticalAction()
    {
      return verticalAction;
    }

    public SafeAction getHorizontalAction()
    {
      return horizontalAction;
    }
  }
}
