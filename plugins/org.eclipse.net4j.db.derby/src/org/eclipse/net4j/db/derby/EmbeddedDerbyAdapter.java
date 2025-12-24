/*
 * Copyright (c) 2008, 2009, 2011-2013, 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.db.derby;

/**
 * A {@link DerbyAdapter Derby adapter} for <a href="http://db.apache.org/derby/papers/DerbyTut/embedded_intro.html">embedded</a> databases.
 *
 * @author Eike Stepper
 * @since 2.0
 */
public class EmbeddedDerbyAdapter extends DerbyAdapter
{
  public static final String NAME = "derby-embedded"; //$NON-NLS-1$

  public EmbeddedDerbyAdapter()
  {
    super(NAME);
  }

  /**
   * @since 4.3
   */
  protected EmbeddedDerbyAdapter(String name, String version)
  {
    super(name, version);
  }
}
