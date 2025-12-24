/*
 * Copyright (c) 2011, 2012, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Martin Fluegge - initial API and implementation
 */
package org.eclipse.emf.cdo.dawn.spi;

import org.eclipse.emf.cdo.view.CDOView;

import java.util.List;
import java.util.Map;

/**
 * The IDawnEditingSupport is the direct connection the the Dawn Runtime. Service Providers must implement this
 * interface to react on repository changes or local data manipulation. For implementation example @see
 * org.eclipse.emf.cdo.dawn.gmf.editors.impl.DawnGMFEditorSupport.
 *
 * @author Martin Fluegge
 * @since 2.0
 */
public interface IDawnEditingSupport
{
  /**
   * Sets the element dirty. Implementations must ensure that all necessary operations a made to represent a dirty
   * state. E.g. display the state in the user interface.
   */
  public void setDirty(boolean dirty);

  /**
   * Returns the dirty state of the UI element.
   *
   * @return true if the specific parts of the model that should be reflected as dirty to the UI are dirty, else
   *         otherwise.
   */
  public boolean isDirty();

  /**
   * Returns the CDO view that is responsible for the data represented be the IDawnEditingSupport.
   */
  public CDOView getView();

  /**
   * Sets the CDO view that is responsible for the data represented be the IDawnEditingSupport.
   */
  public void setView(CDOView view);

  /**
   * Handles all actions that must be executed when the UI element ist closed. E.g. close the related view.
   */
  public void close();

  /**
   * Registers the default listeners which are used to interact with the repository and the user interface.
   */
  public void registerListeners();

  /**
   * Implementations must process all operations that are need to provide a clean rollback. This includes the rollback
   * on the repository site and the refreshing of the user interface.
   *
   * @since 1.0
   */
  public void rollback();

  /**
   * Refreshes the internal components of the IDawnEditor
   *
   * @since 2.0
   */
  public void refresh();

  /**
   * Locks the objects
   *
   * @since 2.0
   */
  public void lockObjects(List<Object> objectsToBeLocked);

  /**
   * @since 2.0
   */
  public void lockObject(Object objectToBeLocked);

  /**
   * Unlocks the objects
   *
   * @since 2.0
   */
  public void unlockObjects(List<Object> objectsToBeLocked);

  /**
   * @since 2.0
   */
  public void unlockObject(Object objectToBeUnlocked);

  /**
   * Implementations must handle all operations which are necessary on remotely locked objects, like disabling them,
   * providing specific markers, like colors or icons, and so on.
   *
   * @since 2.0
   */
  public void handleRemoteLockChanges(Map<Object, DawnState> changedObjects);
}
