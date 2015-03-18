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

import org.eclipse.net4j.util.ui.handlers.AbstractBaseHandler;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.runtime.IProgressMonitor;

import java.util.Properties;

/**
 * @author Eike Stepper
 */
public abstract class RepositoryCheckoutHandlerQuick extends AbstractBaseHandler<CDORepositoryElement>
{
  private final String type;

  protected RepositoryCheckoutHandlerQuick(String type)
  {
    super(CDORepositoryElement.class, false);
    this.type = type;
  }

  @Override
  protected void doExecute(ExecutionEvent event, IProgressMonitor monitor) throws Exception
  {
    checkout(elements.get(0), type);
  }

  public static void checkout(CDORepositoryElement repositoryElement, String type)
  {
    CDORepository repository = repositoryElement.getRepository();

    Properties properties = new Properties();
    properties.setProperty("type", type);
    properties.setProperty("label", repository.getLabel());
    properties.setProperty("repository", repository.getID());
    properties.setProperty("branchID", Integer.toString(repositoryElement.getBranchID()));
    properties.setProperty("timeStamp", Long.toString(repositoryElement.getTimeStamp()));
    properties.setProperty("readOnly", Boolean.FALSE.toString());
    properties.setProperty("rootID", CDOCheckoutImpl.getCDOIDString(repositoryElement.getObjectID()));

    CDOCheckout checkout = CDOExplorerUtil.getCheckoutManager().addCheckout(properties);
    checkout.open();
  }

  /**
   * @author Eike Stepper
   */
  public static final class Online extends RepositoryCheckoutHandlerQuick
  {
    public Online()
    {
      super(CDOCheckout.TYPE_ONLINE_TRANSACTIONAL);
    }
  }

  /**
   * @author Eike Stepper
   */
  public static final class Offline extends RepositoryCheckoutHandlerQuick
  {
    public Offline()
    {
      super(CDOCheckout.TYPE_OFFLINE);
    }
  }
}
