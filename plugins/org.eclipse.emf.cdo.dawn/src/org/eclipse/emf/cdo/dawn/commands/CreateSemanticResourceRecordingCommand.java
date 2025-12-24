/*
 * Copyright (c) 2010-2012, 2015, 2016 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Martin Fluegge - initial API and implementation
 */
package org.eclipse.emf.cdo.dawn.commands;

import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;

/**
 * @author Martin Fluegge
 */
public class CreateSemanticResourceRecordingCommand extends RecordingCommand
{

  private final CDOTransaction cdoTransaction;

  private Resource resource;

  private final String resourcePath;

  public CreateSemanticResourceRecordingCommand(TransactionalEditingDomain editingDomain, CDOTransaction cdoTransaction, String resourcePath)
  {
    super(editingDomain);
    this.cdoTransaction = cdoTransaction;
    this.resourcePath = resourcePath;
  }

  @Override
  protected void doExecute()
  {
    resource = cdoTransaction.getOrCreateResource(resourcePath.replace("cdo:", ""));
  }

  public Resource getResource()
  {
    return resource;
  }
}
