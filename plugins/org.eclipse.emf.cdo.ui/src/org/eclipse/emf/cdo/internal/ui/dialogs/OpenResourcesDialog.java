package org.eclipse.emf.cdo.internal.ui.dialogs;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.FilteredResourcesSelectionDialog;

public class OpenResourcesDialog extends FilteredResourcesSelectionDialog
{
  public OpenResourcesDialog(Shell parentShell)
  {
    super(parentShell, true, ResourcesPlugin.getWorkspace().getRoot(), IResource.FILE);
    setTitle("Open Resources");
    setInitialPattern("*.ecore");
  }
}
