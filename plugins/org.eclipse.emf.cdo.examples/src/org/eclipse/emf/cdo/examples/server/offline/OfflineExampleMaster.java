/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
 */
public class OfflineExampleMaster extends AbstractOfflineExampleServer
{
  public static final String NAME = "master";

  public static final int PORT = 2036;

  private static final int BROWSER_PORT = 7777;

  public OfflineExampleMaster()
  {
    super(NAME, PORT, BROWSER_PORT);
  }

  @Override
  protected IRepository createRepository(IStore store, Map<String, String> props)
  {
    return CDOServerUtil.createRepository(name, store, props);
  }

  protected void showMenu()
  {
    System.out.println("0 - exit");
    System.out.println("1 - connect repository to network");
    System.out.println("2 - disconnect repository from network");
    System.out.println("3 - dump repository infos");
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
