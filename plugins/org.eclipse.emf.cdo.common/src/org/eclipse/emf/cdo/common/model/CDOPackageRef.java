/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.emf.cdo.common.model;

import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.util.CDOPackageNotFoundException;

import org.eclipse.net4j.util.ObjectUtil;

import org.eclipse.emf.ecore.EPackage;

import java.io.IOException;
import java.text.MessageFormat;

/**
 * A symbolic reference to an {@link EPackage}, identified by its namespace URI.
 *
 * @author Eike Stepper
 * @since 4.29
 */
public final class CDOPackageRef implements CDOModelElementRef<EPackage>
{
  private static final long serialVersionUID = 1L;

  private final String packageURI;

  /**
   * Creates a reference from an EMF package without consulting a registry.
   *
   * @param ePackage the package whose namespace URI identifies the reference
   */
  public CDOPackageRef(EPackage ePackage)
  {
    this(ePackage == null ? null : ePackage.getNsURI());
  }

  /**
   * Creates a reference from a namespace URI.
   *
   * @param packageURI the package namespace URI
   */
  public CDOPackageRef(String packageURI)
  {
    if (packageURI == null)
    {
      throw new IllegalArgumentException("Package URI must not be null"); //$NON-NLS-1$
    }

    this.packageURI = packageURI;
  }

  /**
   * Reads a symbolic package reference. Reading does not resolve the package.
   *
   * @param in the data input
   * @throws IOException if the URI cannot be read
   */
  public CDOPackageRef(CDODataInput in) throws IOException
  {
    this(in.readCDOPackageURI());
  }

  /**
   * Writes only the symbolic package URI.
   *
   * @param out the data output
   * @throws IOException if the URI cannot be written
   */
  public void write(CDODataOutput out) throws IOException
  {
    out.writeCDOPackageURI(packageURI);
  }

  /**
   * Returns the package namespace URI.
   *
   * @return the package namespace URI
   */
  public String getPackageURI()
  {
    return packageURI;
  }

  @Override
  public EPackage resolve(EPackage.Registry packageRegistry)
  {
    EPackage ePackage = packageRegistry.getEPackage(packageURI);
    if (ePackage == null)
    {
      throw new CDOPackageNotFoundException(packageURI);
    }

    return ePackage;
  }

  @Override
  public int hashCode()
  {
    return packageURI.hashCode();
  }

  @Override
  public boolean equals(Object obj)
  {
    if (obj == this)
    {
      return true;
    }

    if (obj != null && obj.getClass() == CDOPackageRef.class)
    {
      CDOPackageRef that = (CDOPackageRef)obj;
      return ObjectUtil.equals(packageURI, that.packageURI);
    }

    return false;
  }

  @Override
  public String toString()
  {
    return MessageFormat.format("CDOPackageRef[{0}]", packageURI); //$NON-NLS-1$
  }
}
