/*
 * Copyright (c) 2009, 2011, 2012, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *
 *  Initial Publication:
 *    Eclipse Magazin - http://www.eclipse-magazin.de
 */
package org.gastro.server;

import org.eclipse.emf.cdo.server.IRepository;

import org.eclipse.net4j.util.om.OSGiApplication;

import org.gastro.internal.server.OM;

/**
 * @author Eike Stepper
 */
public class GastroServer extends OSGiApplication
{
  public static final String ID = OM.BUNDLE_ID + ".app";

  public GastroServer()
  {
    super(ID);
  }

  public static IRepository getRepository()
  {
    return OM.repository;
  }
}
