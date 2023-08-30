/*
 * Copyright (c) 2015, 2020-2023 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.explorer;

import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.INotifier;
import org.eclipse.net4j.util.registry.IRegistry;

import org.eclipse.emf.common.notify.Adapter;

import org.eclipse.core.runtime.IAdaptable;

import java.io.File;
import java.util.Properties;
import java.util.Set;

/**
 * A CDO server independent representation of a repository.
 *
 * @author Eike Stepper
 * @since 4.4
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface CDOExplorerElement extends INotifier, IAdaptable, Adapter, Comparable<CDOExplorerElement>
{
  public String getID();

  public String getType();

  public String getLabel();

  public void setLabel(String label);

  public String getDescription();

  public void setDescription(String description);

  /**
   * @since 4.13
   */
  public boolean hasKeyword(String keyword);

  /**
   * @since 4.13
   */
  public Set<String> getKeywords();

  /**
   * @since 4.13
   */
  public boolean setKeywords(Set<String> keywords);

  /**
   * @since 4.13
   */
  public boolean addKeyword(String keyword);

  /**
   * @since 4.13
   */
  public boolean removeKeyword(String keyword);

  /**
   * @since 4.11
   */
  public String getError();

  /**
   * @since 4.8
   */
  public File getFolder();

  /**
   * @since 4.5
   */
  public File getStateFolder(String path);

  /**
   * @since 4.7
   */
  public File getStateFolder(String path, boolean createOnDemand);

  /**
   * @since 4.7
   */
  public Properties getProperties();

  /**
   * @since 4.13
   */
  public IRegistry<String, Object> getTransientProperties();

  public void delete(boolean deleteContents);

  /**
   * @author Eike Stepper
   * @since 4.12
   * @noextend This interface is not intended to be extended by clients.
   * @noimplement This interface is not intended to be implemented by clients.
   */
  public interface StateChangedEvent extends IEvent
  {
    @Override
    public CDOExplorerElement getSource();
  }
}
