/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */

import org.eclipse.emf.cdo.common.model.EMFUtil;
import org.eclipse.emf.cdo.eresource.EresourcePackage;
import org.eclipse.emf.cdo.server.CDOServerUtil;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.server.mem.MEMStoreUtil;
import org.eclipse.emf.cdo.tests.model1.Model1Package;

import org.eclipse.net4j.Net4jUtil;
import org.eclipse.net4j.acceptor.IAcceptor;
import org.eclipse.net4j.tcp.TCPUtil;
import org.eclipse.net4j.util.container.ContainerUtil;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.OMPlatform;
import org.eclipse.net4j.util.om.log.PrintLogHandler;
import org.eclipse.net4j.util.om.trace.PrintTraceHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public class TestServer implements Runnable
{
  public static final String REPOSITORY_NAME = "repo1";

  public static final boolean REGISTER_MODEL_ON_SERVER = false;

  public static void main(String[] args)
  {
    EMFUtil.registerPackage(Model1Package.eINSTANCE);
    PrintTraceHandler.CONSOLE.setShortContext(true);
    OMPlatform.INSTANCE.addTraceHandler(PrintTraceHandler.CONSOLE);
    OMPlatform.INSTANCE.addLogHandler(PrintLogHandler.CONSOLE);
    OMPlatform.INSTANCE.setDebugging(true);
    new TestServer().run();
  }

  public void run()
  {
    IManagedContainer container = ContainerUtil.createContainer();
    Net4jUtil.prepareContainer(container);
    TCPUtil.prepareContainer(container);
    CDOServerUtil.prepareContainer(container);
    LifecycleUtil.activate(container);

    IStore store = MEMStoreUtil.createMEMStore();
    Map<String, String> props = new HashMap<String, String>();
    IRepository repository = CDOServerUtil.createRepository(REPOSITORY_NAME, store, props);
    CDOServerUtil.addRepository(container, repository);
    EMFUtil.registerPackage(EresourcePackage.eINSTANCE, repository.getPackageRegistry()); // XXX FAKE!
    if (REGISTER_MODEL_ON_SERVER)
    {
      EMFUtil.registerPackage(Model1Package.eINSTANCE, repository.getPackageRegistry());
    }

    IAcceptor acceptor = (IAcceptor)container.getElement("org.eclipse.net4j.acceptors", "tcp", null);

    try
    {
      runInside();
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }

    LifecycleUtil.deactivate(acceptor);
    LifecycleUtil.deactivate(repository);
    LifecycleUtil.deactivate(container);
  }

  protected void runInside() throws Exception
  {
    System.out.println("Press any key to shutdown");
    while (IOUtil.IN().read() == -1)
    {
      Thread.sleep(200);
    }
  }
}
