/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.common;

import org.eclipse.emf.cdo.common.revision.CDORevision;

import org.eclipse.net4j.util.collection.Closeable;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public interface CDOCommonView extends Closeable
{
  public static final long UNSPECIFIED_DATE = CDORevision.UNSPECIFIED_DATE;

  public int getViewID();

  public Type getViewType();

  public CDOCommonSession getSession();

  public long getTimeStamp();

  /**
   * @author Eike Stepper
   */
  public enum Type
  {
    TRANSACTION, READONLY, AUDIT
  }
}
