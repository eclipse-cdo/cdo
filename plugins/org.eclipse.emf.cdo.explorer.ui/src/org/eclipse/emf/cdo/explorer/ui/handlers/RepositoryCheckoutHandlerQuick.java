/*
 * Copyright (c) 2015, 2016, 2020 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.explorer.ui.handlers;

import org.eclipse.emf.cdo.explorer.CDOExplorerUtil;
import org.eclipse.emf.cdo.explorer.checkouts.CDOCheckout;
import org.eclipse.emf.cdo.explorer.repositories.CDORepository;
import org.eclipse.emf.cdo.explorer.repositories.CDORepositoryElement;
import org.eclipse.emf.cdo.explorer.ui.checkouts.wizards.CheckoutWizard;
import org.eclipse.emf.cdo.internal.explorer.checkouts.CDOCheckoutImpl;

import org.eclipse.net4j.util.AdapterUtil;
import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.ui.handlers.AbstractBaseHandler;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.runtime.IProgressMonitor;

import java.util.Properties;

/**
 * @author Eike Stepper
 */
public class RepositoryCheckoutHandlerQuick extends AbstractBaseHandler<CDORepositoryElement>
{
  public RepositoryCheckoutHandlerQuick()
  {
    super(CDORepositoryElement.class, false);
  }

  @Override
  protected void doExecute(ExecutionEvent event, IProgressMonitor monitor) throws Exception
  {
    checkout(elements.get(0), CDOCheckout.TYPE_ONLINE_TRANSACTIONAL);
  }

  public static CDOCheckout checkout(CDORepositoryElement repositoryElement, String type)
  {
    CDORepository repository = repositoryElement.getRepository();
    String readOnly = (CDOCheckout.TYPE_ONLINE_HISTORICAL.equals(type) ? Boolean.TRUE : Boolean.FALSE).toString();
    String label = StringUtil.capAll(type.replace('-', ' ')) + " Checkout";

    Properties properties = new Properties();
    properties.setProperty(CDOCheckoutImpl.PROP_TYPE, type);
    properties.setProperty(CDOCheckoutImpl.PROP_LABEL, CDOExplorerUtil.getCheckoutManager().getUniqueLabel(label));
    properties.setProperty(CDOCheckoutImpl.PROP_REPOSITORY, repository.getID());
    properties.setProperty(CDOCheckoutImpl.PROP_BRANCH_ID, Integer.toString(repositoryElement.getBranchID()));
    properties.setProperty(CDOCheckoutImpl.PROP_TIME_STAMP, Long.toString(repositoryElement.getTimeStamp()));
    properties.setProperty(CDOCheckoutImpl.PROP_READ_ONLY, readOnly);
    properties.setProperty(CDOCheckoutImpl.PROP_ROOT_ID, CDOExplorerUtil.getCDOIDString(repositoryElement.getObjectID()));

    CDOCheckout checkout = CDOExplorerUtil.getCheckoutManager().addCheckout(properties);
    checkout.open();

    CheckoutWizard.showInProjectExplorer(checkout);
    return checkout;
  }

  public static CDOCheckout checkout(CDORepository repository, String type)
  {
    CDORepositoryElement repositoryElement = AdapterUtil.adapt(repository, CDORepositoryElement.class);
    return checkout(repositoryElement, type);
  }
}
