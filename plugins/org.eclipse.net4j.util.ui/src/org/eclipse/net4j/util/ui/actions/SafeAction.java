package org.eclipse.net4j.util.ui.actions;

import org.eclipse.net4j.util.internal.ui.bundle.OM;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
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

  public SafeAction(String text, String toolTipText)
  {
    super(text, null);
    setToolTipText(toolTipText);
  }

  public SafeAction(String text, ImageDescriptor image)
  {
    super(text, image);
  }

  public SafeAction(String text, int style)
  {
    super(text, style);
  }

  public SafeAction(String text)
  {
    super(text);
  }

  @Override
  public final void run()
  {
    try
    {
      safeRun();
    }
    catch (Exception ex)
    {
      OM.LOG.error(ex);
      MessageDialog.openError(null, getText(), ex.getMessage() + "\nSee the Error log for details.");
    }
  }

  protected abstract void safeRun() throws Exception;
}