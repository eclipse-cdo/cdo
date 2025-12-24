/*
 * Copyright (c) 2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.lm.client;

import org.eclipse.emf.cdo.explorer.checkouts.CDOCheckout;
import org.eclipse.emf.cdo.lm.Baseline;
import org.eclipse.emf.cdo.lm.internal.client.AssemblyManager;

import org.eclipse.net4j.util.container.IContainer;
import org.eclipse.net4j.util.event.IEvent;

import org.eclipse.emf.ecore.EObject;

import org.eclipse.core.runtime.IProgressMonitor;

import java.util.function.Consumer;

/**
 * @author Eike Stepper
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface IAssemblyManager extends IContainer<IAssemblyDescriptor>
{
  public static final IAssemblyManager INSTANCE = AssemblyManager.INSTANCE;

  public IAssemblyDescriptor createDescriptor(String label, Baseline baseline, IProgressMonitor monitor) throws Exception;

  public IAssemblyDescriptor getDescriptor(CDOCheckout checkout);

  public IAssemblyDescriptor getDescriptor(EObject object);

  public IAssemblyDescriptor[] getDescriptors(Baseline baseline);

  public IAssemblyDescriptor[] getDescriptors();

  public void forEachDescriptor(Consumer<IAssemblyDescriptor> consumer);

  public String getModuleTypeProperty(CDOCheckout checkout);

  /**
   * @author Eike Stepper
   * @noextend This interface is not intended to be extended by clients.
   * @noimplement This interface is not intended to be implemented by clients.
   */
  public interface DescriptorSyncStateEvent extends IEvent
  {
    public IAssemblyDescriptor getDescriptor();

    public IAssemblyDescriptor.UpdateState getOldSyncState();

    public IAssemblyDescriptor.UpdateState getNewSyncState();
  }
}
