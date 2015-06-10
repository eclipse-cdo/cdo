/*
 * Copyright (c) 2011, 2012, 2015 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.releng.relativepaths;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.dialogs.FilteredResourcesSelectionDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Eike Stepper
 */
public class CreateRelativePathAction implements IObjectActionDelegate
{
  private static final String TITLE = "Create Relative Path";

  private Shell shell;

  private ISelection selection;

  public CreateRelativePathAction()
  {
  }

  public void setActivePart(IAction action, IWorkbenchPart targetPart)
  {
    shell = targetPart.getSite().getShell();
  }

  public void selectionChanged(IAction action, ISelection selection)
  {
    this.selection = selection;
  }

  public void run(IAction action)
  {
    IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
    IResource source = (IResource)((IStructuredSelection)selection).getFirstElement();

    FilteredResourcesSelectionDialog dialog = new FilteredResourcesSelectionDialog(shell, false, root, IResource.FILE);
    dialog.setTitle(TITLE);

    if (dialog.open() == Dialog.OK)
    {
      IResource target = (IResource)dialog.getResult()[0];
      String link = createLink(source, target);

      copyToClipboard(shell.getDisplay(), link);
      MessageDialog.openInformation(shell, TITLE,
          "The following relative path has been copied to the clipboard:\n" + link);
    }
  }

  public static String createLink(IResource source, IResource target)
  {
    return createLink(new File(source.getLocation().toString()), new File(target.getLocation().toString()));
  }

  public static String createLink(File source, File target)
  {
    List<String> sourceSegments = getSegments(source);
    List<String> targetSegments = getSegments(target);

    int minSize = Math.min(sourceSegments.size(), targetSegments.size());
    for (int i = 0; i < minSize; i++)
    {
      if (sourceSegments.get(0).equals(targetSegments.get(0)))
      {
        sourceSegments.remove(0);
        targetSegments.remove(0);
      }
      else
      {
        break;
      }
    }

    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < sourceSegments.size() - 1; i++)
    {
      builder.append("../");
    }

    boolean first = true;
    for (String segment : targetSegments)
    {
      if (first)
      {
        first = false;
      }
      else
      {
        builder.append("/");
      }

      builder.append(segment);
    }

    return builder.toString();
  }

  private static List<String> getSegments(File file)
  {
    List<String> result = new ArrayList<String>();
    getSegments(file, result);
    return result;
  }

  private static void getSegments(File file, List<String> result)
  {
    File parent = file.getParentFile();
    if (parent != null)
    {
      getSegments(parent, result);
    }

    result.add(file.getName());
  }

  public static void copyToClipboard(Display display, String text)
  {
    Clipboard clipboard = null;

    try
    {
      clipboard = new Clipboard(display);
      clipboard.setContents(new Object[] { text }, new Transfer[] { TextTransfer.getInstance() });
    }
    finally
    {
      if (clipboard != null)
      {
        clipboard.dispose();
      }
    }
  }
}
