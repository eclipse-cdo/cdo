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

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.transaction.TransactionalCommandStack;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.impl.TransactionChangeRecorder;
import org.eclipse.emf.workspace.WorkspaceEditingDomainFactory;
import org.eclipse.emf.workspace.impl.WorkspaceCommandStackImpl;

import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.gmf.runtime.diagram.core.DiagramEditingDomainFactory;

public class DawnDiagramEditingDomainFactory extends DiagramEditingDomainFactory
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, DawnDiagramEditingDomainFactory.class);

  private static DawnDiagramEditingDomainFactory instance = new DawnDiagramEditingDomainFactory();

  protected static class DawnDiagramEditingDomain extends DiagramEditingDomain
  {
    public DawnDiagramEditingDomain(AdapterFactory adapterFactory, ResourceSet resourceSet)
    {
      super(adapterFactory, resourceSet);
    }

    public DawnDiagramEditingDomain(AdapterFactory adapterFactory, TransactionalCommandStack stack, ResourceSet resourceSet)
    {
      super(adapterFactory, stack, resourceSet);
    }

    public DawnDiagramEditingDomain(AdapterFactory adapterFactory, TransactionalCommandStack stack)
    {
      super(adapterFactory, stack);
    }

    public DawnDiagramEditingDomain(AdapterFactory adapterFactory)
    {
      super(adapterFactory);
    }

    /**
     * override the TransactionChangeRecorder to suppress the write asserts which are not need with CDO
     */
    @Override
    protected TransactionChangeRecorder createChangeRecorder(ResourceSet rset)
    {
      return new DawnTransactionChangeRecorder(this, rset);
    }
  }

  @Override
  public TransactionalEditingDomain createEditingDomain(IOperationHistory history)
  {
    if (TRACER.isEnabled())
    {
      TRACER.trace("Creating DawnTransactionalEditingDomain using DawnDiagramEditingDomainFactory"); //$NON-NLS-1$
    }
    WorkspaceCommandStackImpl stack = new WorkspaceCommandStackImpl(history);

    TransactionalEditingDomain result = new DawnDiagramEditingDomain(new ComposedAdapterFactory(ComposedAdapterFactory.Descriptor.Registry.INSTANCE), stack);

    mapResourceSet(result);

    configure(result);
    return result;
  }

  @Override
  public TransactionalEditingDomain createEditingDomain(ResourceSet rset, IOperationHistory history)
  {
    WorkspaceCommandStackImpl stack = new WorkspaceCommandStackImpl(history);

    TransactionalEditingDomain result = new DawnDiagramEditingDomain(new ComposedAdapterFactory(ComposedAdapterFactory.Descriptor.Registry.INSTANCE), stack,
        rset);

    mapResourceSet(result);
    configure(result);
    return result;
  }

  @Override
  protected void configure(final TransactionalEditingDomain domain)
  {
    super.configure(domain);

    final ResourceSet rset = domain.getResourceSet();
    DawnPathmapManager.removePathMapMananger(rset.eAdapters());
    rset.eAdapters().add(new DawnPathmapManager());
  }

  public static WorkspaceEditingDomainFactory getInstance()
  {
    return instance;
  }
}
