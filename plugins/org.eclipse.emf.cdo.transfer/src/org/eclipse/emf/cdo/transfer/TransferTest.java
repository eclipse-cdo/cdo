/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.transfer;

import org.eclipse.emf.cdo.spi.transfer.FileSystemTransferSystem;

import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;

import java.io.IOException;
import java.util.Map;

/**
 * @author Eike Stepper
 * @since 4.2
 */
public class TransferTest
{
  public static final CDOTransferSystem FILE_SYSTEM = new FileSystemTransferSystem();

  public static void main(String[] args) throws IOException
  {
    CDOTransfer transfer = createTransfer();
    dump(transfer.getRootMapping());

    Map<String, Object> map = transfer.getTargetResourceSet().getResourceFactoryRegistry().getExtensionToFactoryMap();
    map.put("ecore", new EcoreResourceFactoryImpl());
    map.put("genmodel", new EcoreResourceFactoryImpl());

    transfer.perform();
  }

  public static CDOTransfer createTransfer() throws IOException
  {
    CDOTransfer transfer = new CDOTransfer(FILE_SYSTEM, FILE_SYSTEM);
    transfer.getRootMapping().setRelativePath("C:/develop/transfer");
    transfer.setDefaultTransferType(CDOTransferType.UTF8);

    Map<String, Object> map = transfer.getSourceResourceSet().getResourceFactoryRegistry().getExtensionToFactoryMap();
    map.put("ecore", new EcoreResourceFactoryImpl());
    map.put("genmodel", new EcoreResourceFactoryImpl());

    CDOTransferMapping mapping = transfer.map("C:/develop/git/cdo/plugins/org.eclipse.emf.cdo.tests.model2");
    mapping.setRelativePath("");
    mapping.getChild(".classpath").setName("my.classpath");
    mapping.getChild(".settings").unmap();
    mapping.getChild("META-INF").setRelativePath("osgi/inf");
    mapping.getChild("bin").unmap();
    mapping.getChild("src").unmap();

    CDOTransferMapping model = mapping.getChild("model");
    model.getChild("model2.genmodel").setTransferType(CDOTransferType.UTF8);
    model.getChild("model2.legacy-genmodel").setTransferType(CDOTransferType.BINARY);

    return transfer;
  }

  protected static void dump(CDOTransferMapping mapping)
  {
    System.out.println(mapping + " --> " + mapping.getTransferType() + " --> " + mapping.getStatus());
    for (CDOTransferMapping child : mapping.getChildren())
    {
      dump(child);
    }
  }
}
