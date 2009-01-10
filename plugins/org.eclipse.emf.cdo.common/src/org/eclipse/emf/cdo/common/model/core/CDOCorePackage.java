/***************************************************************************
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.common.model.core;

import org.eclipse.emf.cdo.common.model.CDOPackage;

/**
 * @author Eike Stepper
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface CDOCorePackage extends CDOPackage
{
  public static final String PACKAGE_URI = "http://www.eclipse.org/emf/CDO/core/1.0.0";

  public static final String NAME = "cdocore";

  public CDOObjectClass getCDOObjectClass();
}
