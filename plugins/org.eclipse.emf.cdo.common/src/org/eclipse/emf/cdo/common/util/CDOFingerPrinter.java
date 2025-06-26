/*
 * Copyright (c) 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.common.util;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevisionProvider;

import org.eclipse.net4j.util.factory.ProductCreationException;

/**
 * @author Eike Stepper
 * @since 4.26
 */
public interface CDOFingerPrinter
{
  public String getParam();

  public FingerPrint createFingerPrint(CDORevisionProvider revisionProvider, CDOID rootID);

  /**
   * @author Eike Stepper
   */
  public static final class FingerPrint
  {
    private final String value;

    private final long count;

    private final String param;

    public FingerPrint(String value, long count, String param)
    {
      this.param = param;
      this.value = value;
      this.count = count;
    }

    public String getValue()
    {
      return value;
    }

    public long getCount()
    {
      return count;
    }

    public String getParam()
    {
      return param;
    }
  }

  /**
   * @author Eike Stepper
   * @since 1.2
   */
  public static abstract class Factory extends org.eclipse.net4j.util.factory.Factory
  {
    public static final String PRODUCT_GROUP = "org.eclipse.emf.cdo.common.util.fingerPrinters"; //$NON-NLS-1$

    protected Factory(String type)
    {
      super(PRODUCT_GROUP, type);
    }

    @Override
    public abstract CDOFingerPrinter create(String param) throws ProductCreationException;
  }
}
