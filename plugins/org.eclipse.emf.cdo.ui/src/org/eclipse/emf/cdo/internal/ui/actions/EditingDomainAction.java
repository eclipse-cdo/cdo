package org.eclipse.emf.cdo.internal.ui.actions;

import org.eclipse.net4j.ui.actions.LongRunningAction;

import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.edit.domain.IEditingDomainProvider;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IWorkbenchPart;

/**
 * @author Eike Stepper
 * @ADDED
 */
public abstract class EditingDomainAction extends LongRunningAction
{
  protected EditingDomain editingDomain;

  public EditingDomainAction(String text, ImageDescriptor image)
  {
    super(text, image);
  }

  public EditingDomainAction(String text, int style)
  {
    super(text, style);
  }

  public EditingDomainAction(String text, String toolTipText, ImageDescriptor image)
  {
    super(text, toolTipText, image);
  }

  public EditingDomainAction(String text, String toolTipText)
  {
    super(text, toolTipText);
  }

  public EditingDomainAction(String text)
  {
    super(text);
  }

  public EditingDomain getEditingDomain()
  {
    return editingDomain;
  }

  public void setEditingDomain(EditingDomain editingDomain)
  {
    this.editingDomain = editingDomain;
  }

  public void setActiveWorkbenchPart(IWorkbenchPart workbenchPart)
  {
    setPage(workbenchPart == null ? null : workbenchPart.getSite().getPage());
    setEditingDomain(workbenchPart instanceof IEditingDomainProvider ? ((IEditingDomainProvider)workbenchPart)
        .getEditingDomain() : null);
  }

  public void update()
  {
    setEnabled(editingDomain != null);
  }
}