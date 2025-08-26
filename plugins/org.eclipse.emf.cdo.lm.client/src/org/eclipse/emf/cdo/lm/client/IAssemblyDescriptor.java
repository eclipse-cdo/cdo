/*
 * Copyright (c) 2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.lm.client;

import org.eclipse.emf.cdo.explorer.checkouts.CDOCheckout;
import org.eclipse.emf.cdo.lm.Baseline;
import org.eclipse.emf.cdo.lm.assembly.Assembly;
import org.eclipse.emf.cdo.lm.assembly.AssemblyModule;

import org.eclipse.net4j.util.container.IContainer;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.properties.IPropertiesContainer;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Eike Stepper
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface IAssemblyDescriptor extends IPropertiesContainer, IContainer<AssemblyModule>
{
  public String getName();

  public CDOCheckout getCheckout();

  public Baseline getBaseline();

  public Baseline getBaseline(AssemblyModule module);

  public Assembly getAssembly();

  public String getModuleName();

  public AssemblyModule getModule(String name);

  public AssemblyModule[] getModules(boolean withNewModules);

  public ISystemDescriptor getSystemDescriptor();

  public boolean hasUpdatesAvailable();

  public UpdateState getUpdateState();

  public Updates getAvailableUpdates();

  public List<String> getResolutionErrors();

  public void update() throws Exception;

  /**
   * @author Eike Stepper
   */
  public enum UpdateState
  {
    NoUpdatesAvailable, UpdatesAvailable
  }

  /**
   * @author Eike Stepper
   * @noextend This interface is not intended to be extended by clients.
   * @noimplement This interface is not intended to be implemented by clients.
   */
  public interface Updates
  {
    public boolean isEmpty();

    public Map<String, AssemblyModule> getAdditions();

    public Map<String, AssemblyModule> getModifications();

    public Set<String> getRemovals();
  }

  /**
   * @author Eike Stepper
   * @noextend This interface is not intended to be extended by clients.
   * @noimplement This interface is not intended to be implemented by clients.
   */
  public interface NameChangedEvent extends IEvent
  {
    public IAssemblyDescriptor getDescriptor();

    public String getOldName();

    public String getNewName();
  }

  /**
   * @author Eike Stepper
   * @noextend This interface is not intended to be extended by clients.
   * @noimplement This interface is not intended to be implemented by clients.
   */
  public interface UpdateStateChangedEvent extends IEvent
  {
    public IAssemblyDescriptor getDescriptor();

    public UpdateState getOldUpdateState();

    public UpdateState getNewUpdateState();
  }

  /**
   * @author Eike Stepper
   * @noextend This interface is not intended to be extended by clients.
   * @noimplement This interface is not intended to be implemented by clients.
   */
  public interface AvailableUpdatesChangedEvent extends IEvent
  {
    public IAssemblyDescriptor getDescriptor();

    public Updates getOldAvailableUpdates();

    public Updates getNewAvailableUpdates();
  }
}
