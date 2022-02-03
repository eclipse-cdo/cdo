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
package org.eclipse.net4j.util.container;

import org.eclipse.net4j.util.io.IORuntimeException;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * An abstract implementation of a {@link Container container}.
 *
 * @since 3.18
 * @author Eike Stepper
 */
public abstract class PersistableContainer<E> extends ModifiableContainer<E> implements IContainer.Persistable<E>
{
  private Persistence<E> persistence;

  public PersistableContainer(Class<E> componentType)
  {
    super(componentType);
  }

  @Override
  public final Persistence<E> getPersistence()
  {
    return persistence;
  }

  /**
   * @since 3.5
   */
  @Override
  public final void setPersistence(Persistence<E> persistence)
  {
    this.persistence = persistence;
  }

  public boolean isSavedWhenModified()
  {
    return true;
  }

  @Override
  public synchronized void load() throws IORuntimeException
  {
    if (persistence != null)
    {
      Collection<E> elements = persistence.loadElements();
      clear();
      addAllElements(elements);
    }
  }

  /**
   * @since 3.5
   */
  @Override
  public synchronized void save() throws IORuntimeException
  {
    if (persistence != null)
    {
      List<E> elements = Arrays.asList(getElements());
      persistence.saveElements(elements);
    }
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();
    load();
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    if (!isSavedWhenModified())
    {
      save();
    }

    super.doDeactivate();
  }

  @Override
  protected void containerModified()
  {
    if (isSavedWhenModified())
    {
      save();
    }
  }
}
