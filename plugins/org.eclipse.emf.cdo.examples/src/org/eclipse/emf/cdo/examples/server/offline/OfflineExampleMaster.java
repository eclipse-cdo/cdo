/*
 * Copyright (c) 2011, 2012, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.examples.server.offline;

import org.eclipse.emf.cdo.server.CDOServerUtil;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.IStore;

import java.util.Map;

/**
 * @author Eike Stepper
 * @author Martin Fluegge
 * @since 4.0
 */
public class OfflineExampleMaster extends AbstractOfflineExampleServer
{
  private static final int BROWSER_PORT = 7777;

  public OfflineExampleMaster()
  {
    super(OfflineExampleUtil.MASTER_NAME, OfflineExampleUtil.MASTER_PORT, BROWSER_PORT);
  }

  @Override
  protected IRepository createRepository(IStore store, Map<String, String> props)
  {
    return CDOServerUtil.createRepository(name, store, props);
  }

  public static void main(String[] args) throws Exception
  {
    System.out.println("Master repository starting...");
    OfflineExampleMaster example = new OfflineExampleMaster();
    example.init();
    example.run();
    example.done();
  }
}
