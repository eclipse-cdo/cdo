/*
 * Copyright (c) 2009, 2011, 2012, 2015 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
