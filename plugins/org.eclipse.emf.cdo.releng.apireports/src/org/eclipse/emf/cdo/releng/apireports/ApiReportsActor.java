/*
 * Copyright (c) 2012, 2013, 2015, 2016 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.releng.apireports;

import org.eclipse.buckminster.core.actor.AbstractActor;
import org.eclipse.buckminster.core.actor.IActionContext;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;

import java.util.Map;

/**
 * @author Eike Stepper
 */
public class ApiReportsActor extends AbstractActor
{
  public ApiReportsActor()
  {
  }

  @Override
  protected IStatus internalPerform(IActionContext context, IProgressMonitor monitor) throws CoreException
  {
    Map<String, ? extends Object> properties = context.getAction().getActorProperties();
    String baselineName = (String)properties.get("baseline");
    String exclusionPatterns = (String)properties.get("exclude");

    return ApiReportsGenerator.generate(baselineName, exclusionPatterns, monitor);
  }
}
