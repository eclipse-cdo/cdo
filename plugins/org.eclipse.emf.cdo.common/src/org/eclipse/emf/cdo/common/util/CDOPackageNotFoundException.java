/*
 * Copyright (c) 2015, 2023 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Esteban Dugueperoux - initial API and implementation
 */
package org.eclipse.emf.cdo.common.util;

import org.eclipse.emf.cdo.internal.common.messages.Messages;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EPackage.Registry;

import java.text.MessageFormat;

/**
 * A {@link CDOException} to indicate that an {@link EPackage} is not available in the current {@link Registry}.
 *
 * @author Esteban Dugueperoux
 * @since 4.4
 */
public final class CDOPackageNotFoundException extends CDOException
{
  private static final long serialVersionUID = 1L;

  private final String packageURI;

  public CDOPackageNotFoundException(String packageURI)
  {
    super(MessageFormat.format(Messages.getString("CDOPackageNotFoundException.0"), packageURI));
    this.packageURI = packageURI;
  }

  public String getPackageURI()
  {
    return packageURI;
  }
}
