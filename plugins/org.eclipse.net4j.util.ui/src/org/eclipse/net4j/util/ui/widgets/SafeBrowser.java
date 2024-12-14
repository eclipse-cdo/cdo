/*
 * Copyright (c) 2024 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.ui.widgets;

import org.eclipse.net4j.internal.util.bundle.OM;
import org.eclipse.net4j.util.ConsumerWithException;
import org.eclipse.net4j.util.RunnableWithException;
import org.eclipse.net4j.util.om.OMPlatform;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.widgets.Composite;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Eike Stepper
 * @since 3.19
 */
public class SafeBrowser extends Browser
{
  private static final int STYLE = OMPlatform.INSTANCE.getProperty("org.eclipse.net4j.util.ui.widgets.SafeBrowser.STYLE", SWT.EDGE);

  private boolean documentAvailable;

  private List<RunnableWithException> deferredScripts;

  public SafeBrowser(Composite parent)
  {
    super(parent, STYLE);

    addProgressListener(new ProgressListener()
    {
      @Override
      public void changed(ProgressEvent event)
      {
        documentAvailable = false;
      }

      @Override
      public void completed(ProgressEvent event)
      {
        documentAvailable = true;

        List<RunnableWithException> runnables;
        synchronized (SafeBrowser.this)
        {
          runnables = deferredScripts;
          deferredScripts = null;
        }

        if (runnables != null)
        {
          for (RunnableWithException runnable : runnables)
          {
            try
            {
              runnable.run();
            }
            catch (Exception ex)
            {
              OM.LOG.error(ex);
            }
          }
        }
      }
    });
  }

  public boolean isDocumentAvailable()
  {
    return documentAvailable;
  }

  @Override
  public Object evaluate(String script, boolean trusted) throws SWTException
  {
    checkDocumentAvailable(script);
    return internalEvaluate(script, trusted);
  }

  public void evaluateSafe(String script, boolean trusted, ConsumerWithException<Object, SWTException> resultHandler) throws SWTException
  {
    RunnableWithException runnable = () -> {
      Object result = internalEvaluate(script, trusted);
      resultHandler.accept(result);
    };

    if (documentAvailable)
    {
      try
      {
        runnable.run();
      }
      catch (Exception ex)
      {
        OM.LOG.error(ex);
      }
    }
    else
    {
      deferScript(runnable);
    }
  }

  /**
   * @since 3.20
   */
  public int evaluateInt(String script)
  {
    Object result = evaluate(script);
    if (result instanceof Number)
    {
      return ((Number)result).intValue();
    }

    return 0;
  }

  @Override
  public boolean execute(String script)
  {
    checkDocumentAvailable(script);
    return internalExecute(script);
  }

  public boolean executeSafe(String script)
  {
    if (documentAvailable)
    {
      return internalExecute(script);
    }

    deferScript(() -> internalExecute(script));
    return false;
  }

  @Override
  protected void checkSubclass()
  {
    // Allow subclassing.
  }

  private Object internalEvaluate(String script, boolean trusted) throws SWTException
  {
    return super.evaluate(script, trusted);
  }

  private boolean internalExecute(String script)
  {
    return super.execute(script);
  }

  private void deferScript(RunnableWithException runnable)
  {
    synchronized (this)
    {
      if (deferredScripts == null)
      {
        deferredScripts = new ArrayList<>();
      }

      deferredScripts.add(runnable);
    }
  }

  private void checkDocumentAvailable(String script)
  {
    if (!documentAvailable)
    {
      throw new DocumentUnavailableException();
    }
  }

  /**
   * @author Eike Stepper
   */
  public static final class DocumentUnavailableException extends IllegalStateException
  {
    private static final long serialVersionUID = 1L;

    public DocumentUnavailableException()
    {
    }
  }
}
