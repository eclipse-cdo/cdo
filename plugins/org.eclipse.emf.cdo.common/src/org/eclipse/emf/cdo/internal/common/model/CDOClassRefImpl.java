/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.internal.common.model;

import org.eclipse.emf.cdo.common.model.CDOClass;
import org.eclipse.emf.cdo.common.model.CDOClassRef;
import org.eclipse.emf.cdo.common.model.CDOPackage;
import org.eclipse.emf.cdo.common.model.CDOPackageManager;

import org.eclipse.net4j.util.io.ExtendedDataInput;
import org.eclipse.net4j.util.io.ExtendedDataOutput;

import java.io.IOException;
import java.text.MessageFormat;

/**
 * TODO Optimize transfer of CDOClassRef instances
 * 
 * @author Eike Stepper
 */
public final class CDOClassRefImpl implements CDOClassRef
{
  private String packageURI;

  private int classifierID;

  public CDOClassRefImpl()
  {
  }

  public CDOClassRefImpl(String packageURI, int classifierID)
  {
    this.packageURI = packageURI;
    this.classifierID = classifierID;
  }

  public CDOClassRefImpl(ExtendedDataInput in, String defaultURI) throws IOException
  {
    // TODO Optimize transfer of URIs
    packageURI = in.readString();
    if (packageURI == null)
    {
      packageURI = defaultURI;
    }

    classifierID = in.readInt();
  }

  public void write(ExtendedDataOutput out, String defaultURI) throws IOException
  {
    // TODO Optimize transfer of URIs
    out.writeString(packageURI.equals(defaultURI) ? null : packageURI);
    out.writeInt(classifierID);
  }

  public String getPackageURI()
  {
    return packageURI;
  }

  public int getClassifierID()
  {
    return classifierID;
  }

  public CDOClass resolve(CDOPackageManager packageManager)
  {
    CDOPackage cdoPackage = packageManager.lookupPackage(packageURI);
    if (cdoPackage != null)
    {
      return cdoPackage.lookupClass(classifierID);
    }

    return null;
  }

  @Override
  public String toString()
  {
    return MessageFormat.format("CDOClassRef({0}, {1})", packageURI, classifierID);
  }
}
