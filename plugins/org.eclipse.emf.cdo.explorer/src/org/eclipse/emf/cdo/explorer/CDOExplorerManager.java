/*
 * Copyright (c) 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.explorer;

import org.eclipse.emf.cdo.explorer.checkouts.CDOCheckoutManager;
import org.eclipse.emf.cdo.explorer.repositories.CDORepositoryManager;

import org.eclipse.net4j.util.container.IContainer;
import org.eclipse.net4j.util.event.IEvent;

import org.eclipse.core.runtime.IAdaptable;

/**
 * A common base interface for {@link CDORepositoryManager repository managers} and
 * {@link CDOCheckoutManager checkout managers}.
 *
 * @author Eike Stepper
 * @since 4.4
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface CDOExplorerManager<T extends CDOExplorerElement> extends IContainer<T>, IAdaptable
{
  /**
   * @since 4.5
   */
  public String getUniqueLabel(String label);

  /**
   * An {@link IEvent event} fired from {@link CDOExplorerManager explorer managers} when their
   * {@link #getChangedElements() elements} have changed.
   *
   * @author Eike Stepper
   */
  public interface ElementsChangedEvent extends IEvent
  {
    @Override
    public CDOExplorerManager<?> getSource();

    public ElementsChangedEvent.StructuralImpact getStructuralImpact();

    public Object[] getChangedElements();

    /**
     * Enumerates the possible {@link ElementsChangedEvent#getStructuralImpact() structural impacts}
     * that {@link ElementsChangedEvent#getChangedElements() element changes} can have.
     *
     * @author Eike Stepper
     */
    public static enum StructuralImpact
    {
      NONE, ELEMENT, PARENT
    }
  }
}
