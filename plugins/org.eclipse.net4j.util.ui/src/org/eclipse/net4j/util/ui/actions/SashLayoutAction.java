package org.eclipse.net4j.util.ui.actions;

import org.eclipse.net4j.util.ui.widgets.SashComposite;

import org.eclipse.jface.action.IAction;

/**
 * @author Eike Stepper
 */
public abstract class SashLayoutAction extends SafeAction
{
  private SashComposite sashComposite;

  public SashLayoutAction(SashComposite sashComposite, String text)
  {
    super(text, IAction.AS_RADIO_BUTTON);
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
}