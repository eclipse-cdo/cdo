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

import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.widgets.Shell;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class WinGit
{
  private static final String GIT_BASH = "C:\\Program Files (x86)\\Git\\bin\\sh.exe";

  private static String gitBash;

  public WinGit()
  {
  }

  public static synchronized String getGitBash(Shell shell)
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
        gitBash = getPath(GIT_BASH, shell);
      }

      if (!new File(gitBash).isFile())
      {
        gitBash = getPath(gitBash, shell);
      }

      saveFile(stateFile, gitBash);
    }

    return gitBash;
  }

  private static String getPath(String initial, Shell shell)
  {
    InputDialog dialog = new InputDialog(shell, "Git Bash", "Location:", initial, new IInputValidator()
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

  private static String loadFile(File file)
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

  private static void saveFile(File file, String content)
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
