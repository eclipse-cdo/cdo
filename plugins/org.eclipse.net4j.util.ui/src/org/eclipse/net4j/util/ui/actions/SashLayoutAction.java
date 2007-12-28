/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.util.ui.actions;

import org.eclipse.net4j.util.ui.widgets.SashComposite;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.MenuManager;

/**
 * @author Eike Stepper
 */
public abstract class SashLayoutAction extends SafeAction
{
  private SashComposite sashComposite;

  public SashLayoutAction(SashComposite sashComposite, String text)
  {
    super(text, IAction.AS_RADIO_BUTTON);
    this.sashComposite = sashComposite;
  }

  public SashComposite getSashComposite()
  {
    return sashComposite;
  }

  /**
   * @author Eike Stepper
   */
  public static class Vertical extends SashLayoutAction
  {
    public Vertical(SashComposite sashComposite)
    {
      super(sashComposite, "Vertical");
    }

    @Override
    protected void safeRun() throws Exception
    {
      getSashComposite().setVertical(true);
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class Horizontal extends SashLayoutAction
  {
    public Horizontal(SashComposite sashComposite)
    {
      super(sashComposite, "Horizontal");
    }

    @Override
    protected void safeRun() throws Exception
    {
      getSashComposite().setVertical(false);
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
      this(sashComposite, true);
    }

    public LayoutMenu(SashComposite sashComposite, boolean defaultVertical)
    {
      super("Layout");
      verticalAction = new SashLayoutAction.Vertical(sashComposite);
      verticalAction.setChecked(defaultVertical);
      add(verticalAction);

      horizontalAction = new SashLayoutAction.Horizontal(sashComposite);
      horizontalAction.setChecked(!defaultVertical);
      add(horizontalAction);
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