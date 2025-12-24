/*
 * Copyright (c) 2017, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
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
  @Override
  public Object start(IApplicationContext context) throws Exception
  {
    String reportFileName = null;
    String baselineName = null;
    String exclusionPatterns = null;

    String[] arguments = (String[])context.getArguments().get(IApplicationContext.APPLICATION_ARGS);
    if (arguments != null)
    {
      for (int i = 0; i < arguments.length; ++i)
      {
        String option = arguments[i];
        if ("-reportFile".equals(option))
        {
          reportFileName = arguments[++i];
        }
        else if ("-baseline".equals(option))
        {
          baselineName = arguments[++i];
        }
        else if ("-exclusionPatterns".equals(option))
        {
          exclusionPatterns = arguments[++i];
        }
      }
    }

    IStatus result = ApiReportsGenerator.generate(reportFileName, baselineName, exclusionPatterns,
        new NullProgressMonitor());
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

  @Override
  public void stop()
  {
  }
}
