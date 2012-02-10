/**
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.releng.wingit;

import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.mylyn.tasks.core.ITaskAttachment;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.actions.BaseSelectionListenerAction;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * @author Eike Stepper
 */
public class ApplyPatchToGitAction extends BaseSelectionListenerAction implements IViewActionDelegate, IMenuCreator
{
  private IViewPart view;

  private ISelection currentSelection;

  private Menu dropDownMenu;

  public ApplyPatchToGitAction()
  {
    super("Adjust and Apply Patch");
  }

  protected ApplyPatchToGitAction(String text)
  {
    super(text);
  }

  public void init(IViewPart view)
  {
    this.view = view;
  }

  public void run(IAction action)
  {
    action.setMenuCreator(this);
  }

  public void selectionChanged(IAction action, ISelection selection)
  {
    action.setMenuCreator(this);
    currentSelection = selection;
  }

  private ITaskAttachment getSelectedAttachment()
  {
    if (currentSelection instanceof StructuredSelection)
    {
      Object object = ((StructuredSelection)currentSelection).getFirstElement();
      if (object instanceof ITaskAttachment)
      {
        return (ITaskAttachment)object;
      }
    }

    return null;
  }

  public void dispose()
  {
    if (dropDownMenu != null)
    {
      dropDownMenu.dispose();
      dropDownMenu = null;
    }
  }

  public Menu getMenu(Control parent)
  {
    dispose();
    dropDownMenu = new Menu(parent);
    addActionsToMenu();
    return dropDownMenu;
  }

  public Menu getMenu(Menu parent)
  {
    dispose();
    dropDownMenu = new Menu(parent);
    addActionsToMenu();
    return dropDownMenu;
  }

  private void addActionsToMenu()
  {
    ITaskAttachment attachment = getSelectedAttachment();
    if (attachment == null)
    {
      return;
    }

    Repository[] repositories = org.eclipse.egit.core.Activator.getDefault().getRepositoryCache().getAllRepositories();
    for (Repository repository : repositories)
    {
      RepositorySelectionAction action = new RepositorySelectionAction(attachment, repository);
      ActionContributionItem item = new ActionContributionItem(action);
      item.fill(dropDownMenu, -1);
    }
  }

  /**
   * @author Eike Stepper
   */
  private class RepositorySelectionAction extends Action
  {
    private final ITaskAttachment attachment;

    private final Repository repository;

    public RepositorySelectionAction(ITaskAttachment attachment, Repository repository)
    {
      this.attachment = attachment;
      this.repository = repository;
      setText(repository.getWorkTree().getName());
      setImageDescriptor(Activator.getImageDescriptor("icons/repository.gif"));
    }

    @Override
    @SuppressWarnings({ "restriction", "deprecation" })
    public void run()
    {
      try
      {
        IStorage storage = org.eclipse.mylyn.internal.tasks.ui.editors.TaskAttachmentStorage.create(attachment);
        File file = savePatch(storage);

        applyPatch(file);
      }
      catch (Exception ex)
      {
        ex.printStackTrace();
      }
    }

    @SuppressWarnings("resource")
    private File savePatch(IStorage storage) throws CoreException
    {
      InputStream in = null;
      BufferedWriter writer = null;

      try
      {
        File tempFile = File.createTempFile("~attachment-", ".patch");
        writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tempFile)));

        in = storage.getContents();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));

        String line;
        while ((line = reader.readLine()) != null)
        {
          writer.write(line);
          writer.write("\n");
        }

        return tempFile;
      }
      catch (CoreException ex)
      {
        throw ex;
      }
      catch (Exception ex)
      {
        throw new CoreException(new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Problem while saving patch", ex));
      }
      finally
      {
        if (in != null)
        {
          try
          {
            in.close();
          }
          catch (Exception ex)
          {
          }
        }

        if (writer != null)
        {
          try
          {
            writer.close();
          }
          catch (Exception ex)
          {
          }
        }
      }
    }

    protected void applyPatch(File file)
    {
      if (repository != null)
      {
        Shell shell = view.getSite().getShell();
        File workTree = repository.getWorkTree();
        String command = "apply \"" + file.getAbsolutePath() + "\"";

        GitBash.executeCommand(shell, workTree, command);
      }
    }
  }
}
