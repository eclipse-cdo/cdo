/*
 * Copyright (c) 2004-2014 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.util;

import org.eclipse.emf.cdo.common.util.CDOException;

import org.eclipse.emf.internal.cdo.messages.Messages;

import org.eclipse.emf.ecore.EPackage.Registry;

import java.text.MessageFormat;

/**
 * A {@link CDOException} to indicate that an EPackage is not available in the current {@link Registry}
 *
 * @author Esteban Dugueperoux
 * @since 4.4
 */
public class PackageNotFoundException extends CDOException
{
  private static final long serialVersionUID = 1L;

  private String uri;

  public PackageNotFoundException(String uri)
  {
    super(MessageFormat.format(Messages.getString("CDOSessionImpl.0"), uri));
    this.uri = uri;
  }

  public String uri()
  {
    return uri;
  }

}
