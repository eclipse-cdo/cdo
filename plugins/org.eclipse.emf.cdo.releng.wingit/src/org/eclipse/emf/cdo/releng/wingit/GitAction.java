/*
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.releng.wingit;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class GitAction implements IObjectActionDelegate
{
  private static final String GIT_BASH = "C:\\Program Files (x86)\\Git\\bin\\sh.exe";

  private IWorkbenchPart targetPart;

  private String gitBash;

  private Repository repository;

  public GitAction()
  {
  }

  public void setActivePart(IAction action, IWorkbenchPart targetPart)
  {
    this.targetPart = targetPart;
  }

  public void selectionChanged(IAction action, ISelection selection)
  {
    repository = null;
    if (selection instanceof IStructuredSelection)
    {
      IStructuredSelection ssel = (IStructuredSelection)selection;
      Object element = ssel.getFirstElement();
      if (element instanceof IAdaptable)
      {
        IAdaptable adaptable = (IAdaptable)element;
        repository = (Repository)adaptable.getAdapter(Repository.class);
      }
    }
  }

  public void run(IAction action)
  {
    if (repository != null)
    {
      initGitBash();

      try
      {
        File workTree = repository.getWorkTree();
        Runtime.getRuntime().exec(
            "cmd /c cd \"" + workTree.getAbsolutePath() + "\" && start cmd.exe /c \"" + GIT_BASH + "\" --login -i");
      }
      catch (Exception ex)
      {
        ex.printStackTrace();
      }
    }
  }

  protected void initGitBash()
  {
    if (gitBash == null)
    {
      File stateFile = Activator.getDefault().getStateLocation().append("git-bash.txt").toFile();
      if (stateFile.isFile())
      {
        gitBash = loadFile(stateFile);
      }

      if (gitBash == null)
      {
        gitBash = getPath(GIT_BASH);
      }

      if (!new File(gitBash).isFile())
      {
        gitBash = getPath(gitBash);
      }

      saveFile(stateFile, gitBash);
    }
  }

  private String getPath(String initial)
  {
    InputDialog dialog = new InputDialog(targetPart.getSite().getShell(), "Git Bash", "Location:", initial,
        new IInputValidator()
        {
          public String isValid(String newText)
          {
            return new File(newText).isFile() ? null : "Not a file!";
          }
        });

    if (dialog.open() != InputDialog.OK)
    {
      throw new IllegalStateException("Git bash not found at " + gitBash);
    }

    return dialog.getValue();
  }

  private String loadFile(File file)
  {
    FileReader in = null;

    try
    {
      in = new FileReader(file);
      return new BufferedReader(in).readLine();
    }
    catch (IOException ex)
    {
      return null;
    }
    finally
    {
      if (in != null)
      {
        try
        {
          in.close();
        }
        catch (IOException ex)
        {
          // Ignore
        }
      }
    }
  }

  private void saveFile(File file, String content)
  {
    FileWriter out = null;

    try
    {
      out = new FileWriter(file);
      out.write(content);
    }
    catch (IOException ex)
    {
      throw new IllegalStateException("Could not write to " + file, ex);
    }
    finally
    {
      if (out != null)
      {
        try
        {
          out.close();
        }
        catch (IOException ex)
        {
          // Ignore
        }
      }
    }
  }
}
