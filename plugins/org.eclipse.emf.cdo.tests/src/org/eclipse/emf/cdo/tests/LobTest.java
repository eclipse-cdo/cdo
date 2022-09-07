/*
 * Copyright (c) 2010-2013, 2019, 2022 Eike Stepper (Loehne, Germany) and others.
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
  @Override
  protected void doSetUp() throws Exception
  {
    super.doSetUp();
    skipStoreWithoutLargeObjects();
  }

  private byte[] commitBlob() throws Exception
  {
    try (InputStream inputStream = OM.BUNDLE.getInputStream("backup-tests/Ecore.uml"))
    {
      CDOBlob blob = new CDOBlob(inputStream);

      Image image = getModel3Factory().createImage();
      image.setWidth(320);
      image.setHeight(200);
      image.setData(blob);

      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource(getResourcePath("res"));
      resource.getContents().add(image);
      transaction.commit();

      return blob.getID();
    }
  }

  public void testCommitBlob() throws Exception
  {
    commitBlob();
  }

  public void testReadBlob() throws Exception
  {
    byte[] lobID = commitBlob();
    CDOLobStoreImpl.INSTANCE.getBinaryFile(lobID).delete();

    CDOSession session = openSession();
    CDOView view = session.openView();
    CDOResource resource = view.getResource(getResourcePath("res"));

    Image image = (Image)resource.getContents().get(0);
    assertEquals(320, image.getWidth());
    assertEquals(200, image.getHeight());

    CDOBlob blob = image.getData();

    try (InputStream inputStream = blob.getContents())
    {
      IOUtil.copyBinary(inputStream, System.out);
    }
  }

  private byte[] commitClob() throws Exception
  {
    try (InputStream inputStream = OM.BUNDLE.getInputStream("backup-tests/Ecore.uml"))
    {
      CDOClob clob = new CDOClob(new InputStreamReader(inputStream));

      File file = getModel3Factory().createFile();
      file.setName("Ecore.uml");
      file.setData(clob);

      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource(getResourcePath("res"));
      resource.getContents().add(file);
      transaction.commit();

      return clob.getID();
    }
  }

  public void testCommitClob() throws Exception
  {
    commitClob();
  }

  public void testReadClob() throws Exception
  {
    byte[] lobID = commitClob();
    CDOLobStoreImpl.INSTANCE.getCharacterFile(lobID).delete();

    CDOSession session = openSession();
    CDOView view = session.openView();
    CDOResource resource = view.getResource(getResourcePath("res"));

    File file = (File)resource.getContents().get(0);
    assertEquals("Ecore.uml", file.getName());

    CDOClob clob = file.getData();

    try (Reader reader = clob.getContents())
    {
      IOUtil.copyCharacter(reader, new OutputStreamWriter(System.out));
    }
  }
}
