/*
 * Copyright (c) 2007, 2011, 2012, 2015, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.io;

import org.eclipse.net4j.util.factory.ProductCreationException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author Eike Stepper
 */
public interface IStreamWrapper
{
  public InputStream wrapInputStream(InputStream in) throws IOException;

  public OutputStream wrapOutputStream(OutputStream out) throws IOException;

  public void finishInputStream(InputStream in) throws IOException;

  public void finishOutputStream(OutputStream out) throws IOException;

  /**
   * @author Esteban Dugueperoux
   * @since 3.6
   */
  public static abstract class Factory extends org.eclipse.net4j.util.factory.Factory
  {
    public static final String PRODUCT_GROUP = "org.eclipse.net4j.streamWrappers"; //$NON-NLS-1$

    public Factory(String type)
    {
      super(PRODUCT_GROUP, type);
    }

    @Override
    public abstract IStreamWrapper create(String description) throws ProductCreationException;
  }
}
