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
package org.eclipse.emf.cdo.transfer.internal.ui;

import org.eclipse.emf.cdo.spi.transfer.FileSystemTransferSystem;
import org.eclipse.emf.cdo.transfer.CDOTransfer;
import org.eclipse.emf.cdo.transfer.CDOTransferMapping;
import org.eclipse.emf.cdo.transfer.CDOTransferType;
import org.eclipse.emf.cdo.transfer.spi.workspace.WorkspaceTransferSystem;
import org.eclipse.emf.cdo.transfer.ui.widgets.TransferComposite;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import java.io.IOException;

/**
 * @author Eike Stepper
 * @since 4.2
 */
public class TransferView extends ViewPart
{
  public static TransferView INSTANCE;

  private Composite parent;

  private TransferComposite transferComposite;

  public TransferView()
  {
  }

  public TransferComposite getTransferComposite()
  {
    return transferComposite;
  }

  public void setTransfer(CDOTransfer transfer)
  {
    if (transferComposite != null && transferComposite.getTransfer() != transfer)
    {
      transferComposite.dispose();
      transferComposite = null;
    }

    if (transferComposite == null && transfer != null)
    {
      transferComposite = new TransferComposite(parent, transfer);
      parent.layout();
    }
  }

  @Override
  public void createPartControl(Composite parent)
  {
    this.parent = parent;

    try
    {
      CDOTransfer transfer = createTransfer();
      setTransfer(transfer);
      INSTANCE = this;
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }
  }

  @Override
  public void dispose()
  {
    INSTANCE = null;
    super.dispose();
  }

  @Override
  public void setFocus()
  {
    if (transferComposite != null)
    {
      transferComposite.setFocus();
    }
  }

  public static CDOTransfer createTransfer() throws IOException
  {
    CDOTransfer transfer = new CDOTransfer(WorkspaceTransferSystem.INSTANCE, FileSystemTransferSystem.INSTANCE);
    transfer.getRootMapping().setRelativePath("C:/develop/transfer");
    transfer.setDefaultTransferType(CDOTransferType.UTF8);

    // Map<String, Object> map =
    // transfer.getSourceResourceSet().getResourceFactoryRegistry().getExtensionToFactoryMap();
    // map.put("ecore", new EcoreResourceFactoryImpl());
    // map.put("genmodel", new EcoreResourceFactoryImpl());

    // CDOTransferMapping mapping = transfer.map("org.eclipse.emf.cdo.tests.model2");
    CDOTransferMapping mapping = transfer.map("org.eclipse.emf");
    mapping.setRelativePath("");
    // mapping.getChild(".classpath").setName("my.classpath");
    // mapping.getChild(".settings").unmap();
    // mapping.getChild("META-INF").setRelativePath("osgi/inf");
    // mapping.getChild("bin").unmap();
    // mapping.getChild("src").unmap();
    //
    // CDOTransferMapping model = mapping.getChild("model");
    // model.getChild("model2.genmodel").setTransferType(CDOTransferType.UTF8);
    // model.getChild("model2.legacy-genmodel").setTransferType(CDOTransferType.BINARY);

    return transfer;
  }
}
