package org.eclipse.net4j.ui.actions;

import org.eclipse.net4j.internal.ui.bundle.Net4jUI;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;

/**
 * @author Eike Stepper
 */
public abstract class SafeAction extends Action
{
  public SafeAction()
  {
  }

  public SafeAction(String text, String toolTipText, ImageDescriptor image)
  {
    super(text, image);
    setToolTipText(toolTipText);
  }

  public SafeAction(String text, int style)
  {
    super(text, style);
  }

  public SafeAction(String text)
  {
    super(text);
  }

  public final void run()
  {
    try
    {
      doRun();
    }
    catch (Exception ex)
    {
      Net4jUI.LOG.error(ex);
    }
  }

  protected abstract void doRun() throws Exception;
}