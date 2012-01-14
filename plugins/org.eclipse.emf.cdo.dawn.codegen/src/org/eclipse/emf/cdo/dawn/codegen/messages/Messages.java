/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Martin Fluegge - initial API and implementation
 */
package org.eclipse.emf.cdo.dawn.codegen.messages;

import org.eclipse.osgi.util.NLS;

/**
 * @author Martin Fluegge
 * @since 1.0
 */
public class Messages extends NLS
{
  private static final String BUNDLE_NAME = "org.eclipse.emf.cdo.dawn.codegen.messages"; //$NON-NLS-1$

  public static String GenerateClientCodeAction_0;

  public static String GenerateEMFFragmentAction_0;
  static
  {
    // initialize resource bundle
    NLS.initializeMessages(BUNDLE_NAME, Messages.class);
  }

  private Messages()
  {
  }
}
