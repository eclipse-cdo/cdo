/*
 * Copyright (c) 2013 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.releng.internal.setup;

import org.eclipse.emf.cdo.releng.internal.setup.ui.ErrorDialog;
import org.eclipse.emf.cdo.releng.internal.setup.util.UpdateUtil.UpdatingException;
import org.eclipse.emf.cdo.releng.setup.SetupConstants;

import org.eclipse.net4j.util.io.IOUtil;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.dynamichelpers.IExtensionTracker;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import org.osgi.framework.BundleContext;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;

/**
 * @author Eike Stepper
 */
public class Activator extends AbstractUIPlugin
{
  public static final String PLUGIN_ID = "org.eclipse.emf.cdo.releng.setup";

  // public static final String CDO_URL = "http://download.eclipse.org/modeling/emf/cdo/updates/integration";

  public static final String CDO_URL = "http://hudson.eclipse.org/cdo/job/emf-cdo-integration/lastSuccessfulBuild/artifact";

  public static final File OLD_PREFERENCES = new File(SetupConstants.USER_HOME, SetupConstants.PREFERENCES_NAME);

  private static Activator plugin;

  private static BundleContext bundleContext;

  public Activator()
  {
  }

  public boolean isSkipStartupTasks()
  {
    return getPreferenceStore().getBoolean(SetupConstants.PREF_SKIP_STARTUP_TASKS);
  }

  public void setSkipStartupTasks(boolean value)
  {
    getPreferenceStore().setValue(SetupConstants.PREF_SKIP_STARTUP_TASKS, value);
  }

  public boolean isLogUnneededTasks()
  {
    return getPreferenceStore().getBoolean(SetupConstants.PREF_LOG_UNNEEDED_TASKS);
  }

  public void setLogUnneededTasks(boolean value)
  {
    getPreferenceStore().setValue(SetupConstants.PREF_LOG_UNNEEDED_TASKS, value);
  }

  @Override
  public void start(BundleContext context) throws Exception
  {
    super.start(context);
    bundleContext = context;
    plugin = this;

    if (OLD_PREFERENCES.isFile() && !SetupConstants.PREFERENCES_FILE.exists())
    {
      SetupConstants.PREFERENCES_FOLDER.mkdirs();
      OLD_PREFERENCES.renameTo(SetupConstants.PREFERENCES_FILE);
    }

    if (!SetupConstants.GIT_IGNORE_FILE.exists())
    {
      SetupConstants.PREFERENCES_FOLDER.mkdirs();
      PrintStream stream = null;

      try
      {
        stream = new PrintStream(SetupConstants.GIT_IGNORE_FILE);
        stream.println(SetupConstants.CACHE_NAME + "/");
      }
      finally
      {
        IOUtil.closeSilent(stream);
      }
    }

    if (SetupConstants.SETUP_IDE && !SetupConstants.SETUP_SKIP && !isSkipStartupTasks())
    {
      final Display display = Display.getDefault();
      display.asyncExec(new Runnable()
      {
        public void run()
        {
          IWorkbench workbench = PlatformUI.getWorkbench();

          IExtensionTracker extensionTracker = workbench.getExtensionTracker();
          if (extensionTracker == null)
          {
            display.asyncExec(this);
          }
          else
          {
            try
            {
              SetupTaskPerformer performer = new SetupTaskPerformer(false);
              performer.perform();
            }
            catch (UpdatingException ex)
            {
              workbench.restart();
            }
            catch (Throwable ex)
            {
              log(ex);
              ErrorDialog.open(ex);
            }
          }
        }
      });
    }
  }

  @Override
  public void stop(BundleContext context) throws Exception
  {
    plugin = null;
    super.stop(context);
  }

  private static void log(String message, int severity)
  {
    log(new Status(severity, PLUGIN_ID, message));
  }

  public static void log(String message)
  {
    log(message, IStatus.INFO);
  }

  public static void log(IStatus status)
  {
    plugin.getLog().log(status);
  }

  public static String log(Throwable t)
  {
    IStatus status = getStatus(t);
    log(status);
    return status.getMessage();
  }

  public static IStatus getStatus(Object obj)
  {
    if (obj instanceof CoreException)
    {
      CoreException coreException = (CoreException)obj;
      return coreException.getStatus();
    }

    if (obj instanceof Throwable)
    {
      Throwable t = (Throwable)obj;
      String msg = t.getLocalizedMessage();
      if (msg == null || msg.length() == 0)
      {
        msg = t.getClass().getName();
      }

      return new Status(IStatus.ERROR, PLUGIN_ID, msg, t);
    }

    return new Status(IStatus.INFO, PLUGIN_ID, obj.toString(), null);
  }

  public static void coreException(Throwable t) throws CoreException
  {
    if (t instanceof CoreException)
    {
      CoreException ex = (CoreException)t;
      IStatus status = ex.getStatus();
      if (status != null && status.getSeverity() == IStatus.CANCEL)
      {
        throw new OperationCanceledException();
      }

      throw ex;
    }

    if (t instanceof OperationCanceledException)
    {
      throw (OperationCanceledException)t;
    }

    if (t instanceof Error)
    {
      throw (Error)t;
    }

    IStatus status = getStatus(t);
    throw new CoreException(status);
  }

  public static String toString(Throwable t)
  {
    return print(t);
  }

  public static String toString(IStatus status)
  {
    return print(status);
  }

  private static String print(Object object)
  {
    try
    {
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      PrintStream printStream = new PrintStream(out, false, "UTF-8");

      print(object, null, printStream, 0, 0);

      printStream.close();
      return new String(out.toByteArray(), "UTF-8");
    }
    catch (UnsupportedEncodingException ex)
    {
      return object.toString();
    }
  }

  private static void print(Object object, StackTraceElement[] extra, PrintStream stream, int level, int more)
  {
    if (object instanceof IStatus)
    {
      IStatus status = (IStatus)object;
      indent(stream, level);

      int severity = status.getSeverity();
      switch (severity)
      {
      case IStatus.OK:
        stream.print("OK");
        break;

      case IStatus.ERROR:
        stream.print("ERROR");
        break;

      case IStatus.WARNING:
        stream.print("WARNING");
        break;

      case IStatus.INFO:
        stream.print("INFO");
        break;

      case IStatus.CANCEL:
        stream.print("CANCEL");
        break;

      default:
        stream.print("severity=");
        stream.print(severity);
      }

      stream.print(": ");
      stream.print(status.getPlugin());

      stream.print(" code=");
      stream.print(status.getCode());

      stream.print(' ');
      stream.println(status.getMessage());

      Throwable t = status.getException();
      if (t != null)
      {
        print(t, null, stream, level, more);
      }
      else if (extra != null)
      {
        print(extra, null, stream, level, more);
      }

      for (IStatus child : status.getChildren())
      {
        print(child, null, stream, level + 1, more);
      }
    }
    else if (object instanceof CoreException)
    {
      CoreException ex = (CoreException)object;

      IStatus status = ex.getStatus();
      if (status.getException() == null)
      {
        extra = ex.getStackTrace();
      }

      print(status, extra, stream, level, more);
    }
    else if (object instanceof Throwable)
    {
      Throwable t = (Throwable)object;

      indent(stream, level);
      if (more != 0)
      {
        stream.print("Caused by: ");
      }

      stream.print(t.getClass().getName());

      String msg = t.getLocalizedMessage();
      if (msg != null && msg.length() != 0)
      {
        stream.print(": ");
        stream.print(msg);
      }

      stream.println();

      print(t.getStackTrace(), null, stream, level, more);

      Throwable cause = t.getCause();
      if (cause != null)
      {
        print(cause, null, stream, level, more + 1);
      }

      if (t instanceof InvocationTargetException)
      {
        Throwable targetException = ((InvocationTargetException)t).getTargetException();
        print(targetException, null, stream, level + 1, more);
      }
    }
    else if (object instanceof StackTraceElement[])
    {
      StackTraceElement[] stackTrace = (StackTraceElement[])object;
      for (int i = 0; i < stackTrace.length - more; i++)
      {
        indent(stream, level + 1);
        stream.print("at ");
        stream.println(stackTrace[i].toString());
      }

      if (more != 0)
      {
        indent(stream, level + 1);
        stream.print("... ");
        stream.print(more);
        stream.println(" more");
      }
    }
    else if (extra != null)
    {
      print(extra, null, stream, level, more);
    }
  }

  private static void indent(PrintStream stream, int level)
  {
    for (int i = 0; i < level; ++i)
    {
      stream.print("  ");
    }
  }

  public static BundleContext getBundleContext()
  {
    return bundleContext;
  }

  public static Activator getDefault()
  {
    return plugin;
  }

  /**
   * @author Eike Stepper
   */
  public static final class EarlyStartup implements IStartup
  {
    public void earlyStartup()
    {
    }
  }
}
