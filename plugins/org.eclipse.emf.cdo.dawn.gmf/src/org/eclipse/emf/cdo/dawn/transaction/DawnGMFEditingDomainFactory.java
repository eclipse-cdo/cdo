/*
 * Copyright (c) 2011, 2012, 2015, 2016 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.dawn.transaction;

import org.eclipse.emf.cdo.dawn.gmf.resources.DawnPathmapManager;
import org.eclipse.emf.cdo.internal.dawn.bundle.OM;

import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.workspace.WorkspaceEditingDomainFactory;
import org.eclipse.emf.workspace.impl.WorkspaceCommandStackImpl;

import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.gmf.runtime.emf.core.GMFEditingDomainFactory;

public class DawnGMFEditingDomainFactory extends GMFEditingDomainFactory
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, DawnGMFEditingDomainFactory.class);

  private static DawnGMFEditingDomainFactory instance = new DawnGMFEditingDomainFactory();

  @Override
  public TransactionalEditingDomain createEditingDomain()
  {
    TransactionalEditingDomain result = super.createEditingDomain();
    configure(result);
    return result;
  }

  @Override
  public TransactionalEditingDomain createEditingDomain(ResourceSet rset)
  {
    TransactionalEditingDomain result = super.createEditingDomain(rset);
    configure(result);
    return result;
  }

  /**
   * overrides WorkspaceEditingDomainFactory method
   */
  @Override
  public synchronized TransactionalEditingDomain createEditingDomain(IOperationHistory history)
  {
    WorkspaceCommandStackImpl stack = new WorkspaceCommandStackImpl(history);
    stack.setResourceUndoContextPolicy(getResourceUndoContextPolicy());

    TransactionalEditingDomain result = new DawnTransactionalEditingDomainImpl(new ComposedAdapterFactory(ComposedAdapterFactory.Descriptor.Registry.INSTANCE),
        stack);

    mapResourceSet(result);
    // configures as the GMFEditingDomainFactory would do
    configure(result);

    return result;
  }

  /**
   * overrides WorkspaceEditingDomainFactory method
   */
  @Override
  public synchronized TransactionalEditingDomain createEditingDomain(ResourceSet rset, IOperationHistory history)
  {
    if (TRACER.isEnabled())
    {
      TRACER.trace("Creating DawnTransactionalEditingDomain using DawnDiagramEditingDomainFactory"); //$NON-NLS-1$
    }
    WorkspaceCommandStackImpl stack = new WorkspaceCommandStackImpl(history);
    stack.setResourceUndoContextPolicy(getResourceUndoContextPolicy());

    TransactionalEditingDomain result = new DawnTransactionalEditingDomainImpl(new ComposedAdapterFactory(ComposedAdapterFactory.Descriptor.Registry.INSTANCE),
        stack, rset);

    mapResourceSet(result);

    // configures as the GMFEditingDomainFactory would do
    configure(result);

    return result;
  }

  public static WorkspaceEditingDomainFactory getInstance()
  {
    return instance;
  }

  @Override
  protected void configure(final TransactionalEditingDomain domain)
  {
    super.configure(domain);

    final ResourceSet rset = domain.getResourceSet();
    DawnPathmapManager.removePathMapMananger(rset.eAdapters());
    rset.eAdapters().add(new DawnPathmapManager());
  }
}
