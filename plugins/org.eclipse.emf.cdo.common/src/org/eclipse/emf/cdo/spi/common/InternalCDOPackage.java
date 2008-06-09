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
package org.eclipse.emf.cdo.spi.common;

import org.eclipse.emf.cdo.common.id.CDOIDMetaRange;
import org.eclipse.emf.cdo.common.model.CDOClass;
import org.eclipse.emf.cdo.common.model.CDOPackage;
import org.eclipse.emf.cdo.common.model.CDOPackageManager;

import java.util.List;

/**
 * @author Eike Stepper
 */
public interface InternalCDOPackage extends CDOPackage, InternalCDOModelElement
{
  public void setPackageManager(CDOPackageManager packageManager);

  public void setPersistent(boolean persistent);

  public void setMetaIDRange(CDOIDMetaRange metaIDRange);

  public void setEcore(String ecore);

  public void addClass(CDOClass cdoClass);

  public void setClasses(List<CDOClass> classes);
}
