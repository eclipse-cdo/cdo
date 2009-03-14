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
package org.eclipse.emf.cdo.common.model;

import org.eclipse.emf.ecore.EPackage;

/**
 * @author Eike Stepper
 */
public interface CDOPackageUnit extends Comparable<CDOPackageUnit>
{
  public CDOPackageRegistry getPackageRegistry();

  public String getID();

  public State getState();

  public Type getType();

  public Type getOriginalType();

  public long getTimeStamp();

  public CDOPackageInfo getTopLevelPackageInfo();

  public CDOPackageInfo getPackageInfo(String packageURI);

  public CDOPackageInfo[] getPackageInfos();

  public EPackage[] getEPackages(boolean loadOnDemand);

  public boolean isSystem();

  /**
   * @author Eike Stepper
   */
  public enum State
  {
    NEW, LOADED, PROXY, DISPOSED
  }

  /**
   * @author Eike Stepper
   */
  public enum Type
  {
    NATIVE, LEGACY, DYNAMIC, UNKNOWN;

    public boolean isGenerated()
    {
      checkNotUnknown();
      return this == NATIVE || this == LEGACY;
    }

    public void checkNotUnknown()
    {
      if (this == UNKNOWN)
      {
        throw new IllegalStateException("Package unit type is unknown");
      }
    }
  }
}
