/*
 * Copyright (c) 2004-2014 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.explorer.ui.checkouts;

import org.eclipse.emf.cdo.eresource.CDOResourceNode;
import org.eclipse.emf.cdo.explorer.checkouts.CDOCheckoutElement;

/**
 * @author Eike Stepper
 */
public class ResourceGroup extends CDOCheckoutElement
{
  private String name;

  public ResourceGroup(CDOResourceNode delegate)
  {
    super(delegate);
    reset();
  }

  @Override
  public CDOResourceNode getDelegate()
  {
    return (CDOResourceNode)super.getDelegate();
  }

  @Override
  public void reset()
  {
    super.reset();

    String fullName = getDelegate().getName();
    name = fullName.substring(0, fullName.lastIndexOf('.'));
  }

  @Override
  public String toString(Object child)
  {
    String fullName = ((CDOResourceNode)child).getName();
    return fullName.substring(fullName.lastIndexOf('.') + 1);
  }

  @Override
  public String toString()
  {
    return name;
  }
}
