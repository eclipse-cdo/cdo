package org.eclipse.emf.cdo.internal.ui.actions;

import org.eclipse.emf.cdo.internal.ui.views.CDOViewHistory;
import org.eclipse.emf.cdo.internal.ui.views.CDOViewHistory.Entry;

import org.eclipse.net4j.ui.actions.LongRunningAction;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IWorkbenchPage;

/**
 * @author Eike Stepper
 */
public abstract class EntryAction extends LongRunningAction
{
  private CDOViewHistory.Entry entry;

  public EntryAction(IWorkbenchPage page, String text, String toolTipText, ImageDescriptor image,
      CDOViewHistory.Entry entry)
  {
    super(page, text, toolTipText, image);
    this.entry = entry;
  }

  public CDOViewHistory.Entry getEntry()
  {
    return entry;
  }
}