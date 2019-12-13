/*
 * Copyright (c) 2007, 2009, 2011, 2012, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.ui;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * @author Eike Stepper
 */
public class DelegatingContentProvider implements IStructuredContentProvider
{
  public static final String NONE = ""; //$NON-NLS-1$

  private IStructuredContentProvider delegate;

  private boolean optional;

  public DelegatingContentProvider(IStructuredContentProvider delegate, boolean optional)
  {
    this.delegate = delegate;
    this.optional = optional;
  }

  public DelegatingContentProvider(IStructuredContentProvider delegate)
  {
    this(delegate, false);
  }

  public IStructuredContentProvider getDelegate()
  {
    return delegate;
  }

  public boolean isOptional()
  {
    return optional;
  }

  @Override
  public void dispose()
  {
    delegate.dispose();
  }

  @Override
  public Object[] getElements(Object inputElement)
  {
    Object[] elements = delegate.getElements(inputElement);
    if (optional)
    {
      Object[] newElements = new Object[elements.length + 1];
      newElements[0] = NONE;
      System.arraycopy(elements, 0, newElements, 1, elements.length);
      elements = newElements;
    }

    return elements;
  }

  @Override
  public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
  {
    delegate.inputChanged(viewer, oldInput, newInput);
  }
}
