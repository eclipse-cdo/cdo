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
package org.eclipse.emf.cdo.common.model;

import org.eclipse.emf.cdo.common.id.CDOIDMetaRange;

/**
 * TODO Add read(), write(), ...
 * 
 * @author Eike Stepper
 */
public final class CDOPackageInfo
{
  private String packageURI;

  private boolean dynamic;

  private CDOIDMetaRange metaIDRange;

  private String parentURI;

  public CDOPackageInfo(String packageURI, boolean dynamic, CDOIDMetaRange metaIDRange, String parentURI)
  {
    this.packageURI = packageURI;
    this.dynamic = dynamic;
    this.metaIDRange = metaIDRange;
    this.parentURI = parentURI;
  }

  public String getPackageURI()
  {
    return packageURI;
  }

  public boolean isDynamic()
  {
    return dynamic;
  }

  public CDOIDMetaRange getMetaIDRange()
  {
    return metaIDRange;
  }

  public String getParentURI()
  {
    return parentURI;
  }
}
