/*
 * Copyright (c) 2016 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.releng.apireports;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;

/**
 * @author Eike Stepper
 */
public class ApiReportsApplication implements IApplication
{
  public Object start(IApplicationContext context) throws Exception
  {
    String baselineName = null;
    String exclusionPatterns = null;

    String[] arguments = (String[])context.getArguments().get(IApplicationContext.APPLICATION_ARGS);
    if (arguments != null)
    {
      for (int i = 0; i < arguments.length; ++i)
      {
        String option = arguments[i];
        if ("-baseline".equals(option))
        {
          baselineName = arguments[++i];
        }
        else if ("-exclusionPatterns".equals(option))
        {
          exclusionPatterns = arguments[++i];
        }
      }
    }

    IStatus result = ApiReportsGenerator.generate(baselineName, exclusionPatterns, new NullProgressMonitor());
    if (!result.isOK())
    {
      Throwable exception = result.getException();
      if (exception instanceof Error)
      {
        throw (Error)exception;
      }

      if (exception instanceof Exception)
      {
        throw (Exception)exception;
      }

      throw new Exception(result.getMessage());
    }

    return null;
  }

  public void stop()
  {
  }
}
