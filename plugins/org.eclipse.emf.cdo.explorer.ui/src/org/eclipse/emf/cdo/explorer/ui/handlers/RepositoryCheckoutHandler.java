/*
 * Copyright (c) 2009-2015 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.explorer.ui.handlers;

import org.eclipse.emf.cdo.explorer.CDOExplorerUtil;
import org.eclipse.emf.cdo.explorer.checkouts.CDOCheckout;
import org.eclipse.emf.cdo.explorer.repositories.CDORepository;
import org.eclipse.emf.cdo.explorer.repositories.CDORepositoryElement;
import org.eclipse.emf.cdo.internal.explorer.checkouts.CDOCheckoutImpl;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.runtime.IProgressMonitor;

import java.util.Properties;

/**
 * @author Eike Stepper
 */
public class RepositoryCheckoutHandler extends AbstractBaseHandler<CDORepositoryElement>
{
  public RepositoryCheckoutHandler()
  {
    super(CDORepositoryElement.class, null);
  }

  @Override
  protected void doExecute(ExecutionEvent event, IProgressMonitor progressMonitor) throws Exception
  {
    for (CDORepositoryElement repositoryElement : elements)
    {
      CDORepository repository = repositoryElement.getRepository();

      Properties properties = new Properties();
      properties.put("type", "online");
      properties.put("label", repository.getLabel());
      properties.put("repository", repository.getID());
      properties.put("branchPath", repositoryElement.getBranchPath());
      properties.put("timeStamp", Long.toString(repositoryElement.getTimeStamp()));
      properties.put("readOnly", Boolean.TRUE.toString());
      properties.put("rootID", CDOCheckoutImpl.getCDOIDString(repositoryElement.getObjectID()));

      CDOCheckout checkout = CDOExplorerUtil.getCheckoutManager().addCheckout(properties);
      checkout.open();
    }
  }
}
