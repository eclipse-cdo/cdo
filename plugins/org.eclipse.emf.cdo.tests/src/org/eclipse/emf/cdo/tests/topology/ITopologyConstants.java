/***************************************************************************
 * Copyright (c) 2004, 2005, 2006 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.tests.topology;


public interface ITopologyConstants
{
  public static final String CDO_TEST_MODE_KEY = "cdo.test.mode";

  public static final String CLIENT_SEPARATED_SERVER_MODE = "Client-Separated-Server";

  public static final String CLIENT_SERVER_MODE = "Client-Server";

  public static final String CLIENT_MODE = "Client";

  public static final String EMBEDDED_MODE = "Embedded";

  public static final String[] ALL_MODES = { //
    CLIENT_SEPARATED_SERVER_MODE, // 0
        CLIENT_SERVER_MODE, // 1
        CLIENT_MODE, // 2
        EMBEDDED_MODE // 3
    };

  public static final String[] SELF_CONTAINED_MODES = { //
    CLIENT_SEPARATED_SERVER_MODE, // 0
        CLIENT_SERVER_MODE, // 1
        EMBEDDED_MODE // 2
    };

  public static final String DEFAULT_MODE = EMBEDDED_MODE;
}
