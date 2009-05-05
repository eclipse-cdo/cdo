/**
 * Copyright (c) 2004 - 2009 Martin Taal and others. and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Martin Taal - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.server.internal.hibernate;

/**
 * Wraps the EPackage of a CDOPackageUnit.
 * 
 * @author Martin Taal
 */
public class HibernateEPackage
{
  private String nsUri;

  public String getNsUri()
  {
    return nsUri;
  }

  public void setNsUri(String nsUri)
  {
    this.nsUri = nsUri;
  }

  private byte[] ePackageBlob;

  public byte[] getEPackageBlob()
  {
    return ePackageBlob;
  }

  public void setEPackageBlob(byte[] ePackageBlob)
  {
    this.ePackageBlob = ePackageBlob;
  }
}
