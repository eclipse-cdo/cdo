/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.transfer.spi.ui;

import org.eclipse.emf.cdo.transfer.CDOTransferElement;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

/**
 * A reusable implementation of a {@link ILabelProvider label provider} that delegates to another label provider
 * for the extracted {@link CDOTransferElement#getNativeObject() native object} of an argument object.
 *
 * @author Eike Stepper
 */
public class NativeObjectLabelProvider extends LabelProvider
{
  private ILabelProvider delegate;

  public NativeObjectLabelProvider(ILabelProvider delegate)
  {
    this.delegate = delegate;
  }

  public final ILabelProvider getDelegate()
  {
    return delegate;
  }

  @Override
  public void dispose()
  {
    delegate.dispose();
    delegate = null;
  }

  @Override
  public String getText(Object object)
  {
    if (object instanceof CDOTransferElement)
    {
      CDOTransferElement element = (CDOTransferElement)object;
      Object nativeObject = element.getNativeObject();
      return delegate.getText(nativeObject);
    }

    return super.getText(object);
  }

  @Override
  public Image getImage(Object object)
  {
    if (object instanceof CDOTransferElement)
    {
      CDOTransferElement element = (CDOTransferElement)object;
      Object nativeObject = element.getNativeObject();
      return delegate.getImage(nativeObject);
    }

    return super.getImage(object);
  }
}
