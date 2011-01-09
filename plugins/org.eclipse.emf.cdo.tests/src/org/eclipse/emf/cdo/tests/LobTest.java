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
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.common.lob.CDOBlob;
import org.eclipse.emf.cdo.common.lob.CDOClob;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.spi.common.CDOLobStoreImpl;
import org.eclipse.emf.cdo.tests.bundle.OM;
import org.eclipse.emf.cdo.tests.model3.File;
import org.eclipse.emf.cdo.tests.model3.Image;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.io.IOUtil;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;

/**
 * @author Eike Stepper
 */
public class LobTest extends AbstractCDOTest
{
  public void testCommitBlob() throws Exception
  {
    InputStream inputStream = null;

    try
    {
      inputStream = OM.BUNDLE.getInputStream("copyright.txt");
      CDOBlob blob = new CDOBlob(inputStream);

      Image image = getModel3Factory().createImage();
      image.setWidth(320);
      image.setHeight(200);
      image.setData(blob);

      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource("res");
      resource.getContents().add(image);

      transaction.commit();
    }
    finally
    {
      IOUtil.close(inputStream);
    }
  }

  public void testReadBlob() throws Exception
  {
    testCommitBlob();
    new java.io.File(CDOLobStoreImpl.INSTANCE.getFolder(), "0a596b8789ffbd6340081279755475e7a3c85674.blob").delete();

    CDOSession session = openSession();
    CDOView view = session.openView();
    CDOResource resource = view.getResource("res");

    Image image = (Image)resource.getContents().get(0);
    assertEquals(320, image.getWidth());
    assertEquals(200, image.getHeight());

    CDOBlob blob = image.getData();
    InputStream inputStream = blob.getContents();

    try
    {
      IOUtil.copyBinary(inputStream, System.out);
    }
    finally
    {
      IOUtil.close(inputStream);
    }
  }

  public void testCommitClob() throws Exception
  {
    InputStream inputStream = null;

    try
    {
      inputStream = OM.BUNDLE.getInputStream("copyright.txt");
      CDOClob clob = new CDOClob(new InputStreamReader(inputStream));

      File file = getModel3Factory().createFile();
      file.setName("copyright.txt");
      file.setData(clob);

      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource("res");
      resource.getContents().add(file);

      transaction.commit();
    }
    finally
    {
      IOUtil.close(inputStream);
    }
  }

  public void testReadClob() throws Exception
  {
    testCommitClob();
    new java.io.File(CDOLobStoreImpl.INSTANCE.getFolder(), "5ef5685c7036c7fc8cafac1d54b91b4e06d9de4e.clob").delete();

    CDOSession session = openSession();
    CDOView view = session.openView();
    CDOResource resource = view.getResource("res");

    File file = (File)resource.getContents().get(0);
    assertEquals("copyright.txt", file.getName());

    CDOClob clob = file.getData();
    Reader reader = clob.getContents();

    try
    {
      IOUtil.copyCharacter(reader, new OutputStreamWriter(System.out));
    }
    finally
    {
      IOUtil.close(reader);
    }
  }
}
