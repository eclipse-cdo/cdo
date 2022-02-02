/*
 * Copyright (c) 2018, 2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.migrator.tasks;

import org.eclipse.net4j.util.ObjectUtil;

import org.eclipse.ant.core.AntCorePlugin;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.CharArrayReader;
import java.io.CharArrayWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Collection;

/**
 * @author Eike Stepper
 */
public abstract class CDOTask extends Task
{
  public static final String FALSE = Boolean.FALSE.toString();

  public static final String TRUE = Boolean.TRUE.toString();

  public static final String NL = System.getProperty("line.separator");

  public static final int EOF = -1;

  protected final IWorkspace workspace = ResourcesPlugin.getWorkspace();

  protected final IWorkspaceRoot root = workspace.getRoot();

  protected boolean verbose = true;

  public void setVerbose(boolean verbose)
  {
    this.verbose = verbose;
  }

  @Override
  public final void execute() throws BuildException
  {
    checkAttributes();

    try
    {

      doExecute();
    }
    catch (Exception ex)
    {
      ex.printStackTrace();

      if (ex instanceof BuildException)
      {
        throw (BuildException)ex;
      }

      throw new BuildException(ex);
    }
  }

  /**
   * Subclasses may override.
   */
  protected void checkAttributes() throws BuildException
  {
    // Do nothing.
  }

  protected abstract void doExecute() throws Exception;

  protected final void verbose(Object object)
  {
    if (verbose)
    {
      log(String.valueOf(object));
    }
  }

  protected final IProgressMonitor getProgressMonitor()
  {
    try
    {
      if (getProject() != null)
      {
        IProgressMonitor progressMonitor = (IProgressMonitor)getProject().getReferences().get(AntCorePlugin.ECLIPSE_PROGRESS_MONITOR);
        if (progressMonitor != null)
        {
          return progressMonitor;
        }
      }
    }
    catch (Exception ex)
    {
      // Do nothing.
    }

    return new NullProgressMonitor();
  }

  public static boolean isSet(Object obj)
  {
    return obj != null;
  }

  public static boolean isSet(String str)
  {
    return !ObjectUtil.isEmpty(str);
  }

  public static boolean isSet(Collection<?> collection)
  {
    return !ObjectUtil.isEmpty(collection);
  }

  public static void assertTrue(String message, boolean expression) throws BuildException
  {
    if (!expression)
    {
      throw new BuildException(message);
    }
  }

  public static String readTextFile(File file) throws IOException
  {
    Reader input = new FileReader(file);

    try
    {
      CharArrayWriter output = new CharArrayWriter();
      copyCharacter(input, output);
      return output.toString();
    }
    finally
    {
      closeSilent(input);
    }
  }

  public static void writeTextFile(File file, String content) throws IOException
  {
    Writer output = new FileWriter(file);

    try
    {
      CharArrayReader input = new CharArrayReader(content.toCharArray());
      copyCharacter(input, output);
    }
    finally
    {
      closeSilent(output);
    }
  }

  private static Exception closeSilent(Closeable closeable)
  {
    try
    {
      if (closeable != null)
      {
        closeable.close();
      }

      return null;
    }
    catch (Exception ex)
    {
      return ex;
    }
  }

  private static long copyCharacter(Reader reader, Writer writer) throws IOException
  {
    if (!(reader instanceof BufferedReader) && !(reader instanceof CharArrayReader))
    {
      reader = new BufferedReader(reader);
    }

    if (!(writer instanceof BufferedWriter) && !(writer instanceof CharArrayWriter))
    {
      writer = new BufferedWriter(writer);
    }

    long size = 0;
    int c;
    while ((c = reader.read()) != EOF)
    {
      writer.write(c);
      ++size;
    }

    writer.flush();
    return size;
  }
}
