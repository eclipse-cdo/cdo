/*
 * Copyright (c) 2011, 2012, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.common.lob.CDOBlob;
import org.eclipse.emf.cdo.common.lob.CDOClob;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.bundle.OM;
import org.eclipse.emf.cdo.tests.model3.File;
import org.eclipse.emf.cdo.tests.model3.Image;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.net4j.util.io.IOUtil;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author Egidijus Vaisnora
 */
public class Bugzilla_351096_Test extends AbstractCDOTest
{
  public void testCommit2Blob() throws Exception
  {
    skipStoreWithoutLargeObjects();

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("res"));

    InputStream inputStream = null;

    try
    {
      inputStream = OM.BUNDLE.getInputStream("backup-tests/Ecore.uml");
      CDOBlob blob = new CDOBlob(inputStream);

      Image image = getModel3Factory().createImage();
      image.setWidth(320);
      image.setHeight(200);
      image.setData(blob);

      resource.getContents().add(image);

      InputStream inputStream2 = new ByteArrayInputStream("Just another stream".getBytes());
      blob = new CDOBlob(inputStream2);

      image = getModel3Factory().createImage();
      image.setWidth(320);
      image.setHeight(200);
      image.setData(blob);

      resource.getContents().add(image);

      transaction.commit();
    }
    finally
    {
      IOUtil.close(inputStream);
    }
  }

  public void testCommit2Clob() throws Exception
  {
    skipStoreWithoutLargeObjects();

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("res"));

    InputStream inputStream = null;

    try
    {
      inputStream = OM.BUNDLE.getInputStream("backup-tests/Ecore.uml");
      CDOClob clob = new CDOClob(new InputStreamReader(inputStream));
      File file = getModel3Factory().createFile();
      file.setName("Ecore.uml");
      file.setData(clob);

      resource.getContents().add(file);

      InputStream inputStream2 = new ByteArrayInputStream("Just another stream".getBytes());
      clob = new CDOClob(new InputStreamReader(inputStream2));
      file = getModel3Factory().createFile();
      file.setName("xxx.txt");
      file.setData(clob);

      resource.getContents().add(file);

      transaction.commit();
    }
    finally
    {
      IOUtil.close(inputStream);
    }
  }
}
