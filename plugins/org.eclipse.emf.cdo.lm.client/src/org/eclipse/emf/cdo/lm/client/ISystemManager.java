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

import org.eclipse.emf.cdo.common.branch.CDOBranchRef;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.explorer.repositories.CDORepository;
import org.eclipse.emf.cdo.lm.Baseline;
import org.eclipse.emf.cdo.lm.Module;
import org.eclipse.emf.cdo.lm.Stream;
import org.eclipse.emf.cdo.lm.System;
import org.eclipse.emf.cdo.lm.internal.client.SystemManager;

import org.eclipse.net4j.util.concurrent.TimeoutRuntimeException;
import org.eclipse.net4j.util.container.IContainer;
import org.eclipse.net4j.util.event.IEvent;

import org.eclipse.emf.ecore.EObject;

import java.util.function.Consumer;

/**
 * @author Eike Stepper
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface ISystemManager extends IContainer<ISystemDescriptor>
{
  public static final ISystemManager INSTANCE = SystemManager.INSTANCE;

  public void refresh();

  public ISystemDescriptor getDescriptor(CDORepository repository);

  public ISystemDescriptor getDescriptor(String systemName);

  public ISystemDescriptor getDescriptor(String systemName, long timeout) throws TimeoutRuntimeException;

  public ISystemDescriptor getDescriptor(EObject object);

  public ISystemDescriptor[] getDescriptors();

  public void forEachDescriptor(Consumer<ISystemDescriptor> consumer);

  public String getModuleName(EObject object);

  /**
   * @author Eike Stepper
   * @noextend This interface is not intended to be extended by clients.
   * @noimplement This interface is not intended to be implemented by clients.
   */
  public interface SystemEvent extends IEvent
  {
    public ISystemDescriptor getSystemDescriptor();

    public System getSystem();
  }

  /**
   * @author Eike Stepper
   * @noextend This interface is not intended to be extended by clients.
   * @noimplement This interface is not intended to be implemented by clients.
   */
  public interface DescriptorStateEvent extends SystemEvent
  {
    public boolean isOpen();
  }

  /**
   * @author Eike Stepper
   * @noextend This interface is not intended to be extended by clients.
   * @noimplement This interface is not intended to be implemented by clients.
   */
  public interface ModuleCreatedEvent extends SystemEvent
  {
    public Module getNewModule();
  }

  /**
   * @author Eike Stepper
   * @noextend This interface is not intended to be extended by clients.
   * @noimplement This interface is not intended to be implemented by clients.
   */
  public interface ModuleDeletedEvent extends SystemEvent
  {
    public CDOID getDeletedModuleID();
  }

  /**
   * @author Eike Stepper
   * @noextend This interface is not intended to be extended by clients.
   * @noimplement This interface is not intended to be implemented by clients.
   */
  public interface BaselineCreatedEvent extends SystemEvent
  {
    public Baseline getNewBaseline();
  }

  /**
   * @author Eike Stepper
   * @noextend This interface is not intended to be extended by clients.
   * @noimplement This interface is not intended to be implemented by clients.
   */
  public interface StreamBranchChangedEvent extends SystemEvent
  {
    public Stream getStream();

    public CDOBranchRef getNewBranch();
  }
}
