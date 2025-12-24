/*
 * Copyright (c) 2010-2013, 2019, 2022, 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.common.lob.CDOBlob;
import org.eclipse.emf.cdo.common.lob.CDOClob;
import org.eclipse.emf.cdo.common.lob.CDOLobHandler;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.server.ILobCleanup;
import org.eclipse.emf.cdo.server.ILobCleanup.LobCleanupNotSupportedException;
import org.eclipse.emf.cdo.server.IStoreAccessor;
import org.eclipse.emf.cdo.server.StoreThreadLocal;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.spi.common.CDOLobStoreImpl;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.spi.server.InternalSession;
import org.eclipse.emf.cdo.tests.bundle.OM;
import org.eclipse.emf.cdo.tests.model3.File;
import org.eclipse.emf.cdo.tests.model3.Image;
import org.eclipse.emf.cdo.tests.model3.MultiLob;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.io.IOUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.concurrent.atomic.AtomicInteger;

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

  @CleanRepositoriesBefore(reason = "Large object counting")
  public void testCleanupBlobs() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("res"));

    for (int i = 0; i < 10; i++)
    {
      Image image = getModel3Factory().createImage();
      image.setWidth(320);
      image.setHeight(200);
      image.setData(new CDOBlob(("Very big blob number " + i).getBytes()));

      resource.getContents().add(image);
    }

    transaction.commit();
    assertEquals(10, countLobs(session));

    for (int i = 0; i < 5; i++)
    {
      Image image = (Image)resource.getContents().get(i);
      image.setData(new CDOBlob(("Even much bigger blob number " + i).getBytes()));
    }

    transaction.commit();
    cleanupLobs(session, 15, 5);
  }

  @CleanRepositoriesBefore(reason = "Large object counting")
  public void testCleanupClobs() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("res"));

    for (int i = 0; i < 10; i++)
    {
      File file = getModel3Factory().createFile();
      file.setName("Unnamed");
      file.setData(new CDOClob("Very big clob number " + i));

      resource.getContents().add(file);
    }

    transaction.commit();
    assertEquals(10, countLobs(session));

    for (int i = 0; i < 5; i++)
    {
      File file = (File)resource.getContents().get(i);
      file.setData(new CDOClob("Even much bigger clob number " + i));
    }

    transaction.commit();
    cleanupLobs(session, 15, 5);
  }

  @CleanRepositoriesBefore(reason = "Large object counting")
  public void testCleanupMultiLobs() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("res"));

    // Add 80 Lobs.
    for (int i = 0; i < 10; i++)
    {
      MultiLob multi = getModel3Factory().createMultiLob();
      multi.setName("Multi");

      for (int j = 0; j < 4; j++)
      {
        multi.getBlobs().add(new CDOBlob(("Very big blob number " + i + "/" + j).getBytes()));
        multi.getClobs().add(new CDOClob("Very big clob number " + i + "/" + j));
      }

      resource.getContents().add(multi);
    }

    transaction.commit();
    assertEquals(80, countLobs(session));

    // Replace 20 Lobs.
    for (int i = 0; i < 5; i++)
    {
      MultiLob multi = (MultiLob)resource.getContents().get(i);

      for (int j = 0; j < 2; j++)
      {
        multi.getBlobs().set(j, new CDOBlob(("Even much bigger blob number " + i + "/" + j).getBytes()));
        multi.getClobs().set(j, new CDOClob("Even much bigger clob number " + i + "/" + j));
      }
    }

    transaction.commit();
    cleanupLobs(session, 80 + 20, 20);
  }

  private final void cleanupLobs(CDOSession session, int originalLobs, int deletedLobs) throws Exception
  {
    assertEquals(originalLobs, countLobs(session));
    InternalRepository repository = getRepository();
    int deleted;

    try
    {
      deleted = repository.cleanupLobs(false);
    }
    catch (LobCleanupNotSupportedException ex)
    {
      deleted = -1;
    }

    if (repository.getStore() instanceof ILobCleanup)
    {
      if (repository.isSupportingAudits())
      {
        assertEquals(0, deleted);
        assertEquals(originalLobs, countLobs(session));
      }
      else
      {
        assertEquals(deletedLobs, deleted);
        assertEquals(originalLobs - deletedLobs, countLobs(session));
      }
    }
    else
    {
      assertEquals(-1, deleted);
    }
  }

  private int countLobs(CDOSession session) throws Exception
  {
    InternalSession serverSession = serverSession(session);

    return StoreThreadLocal.wrap(serverSession, () -> {
      AtomicInteger count = new AtomicInteger();

      try
      {
        IStoreAccessor reader = serverSession.getRepository().getStore().getReader(serverSession);
        reader.handleLobs(0, Long.MAX_VALUE, new CDOLobHandler()
        {
          @Override
          public Writer handleClob(byte[] id, long size) throws IOException
          {
            count.incrementAndGet();
            return null;
          }

          @Override
          public OutputStream handleBlob(byte[] id, long size) throws IOException
          {
            count.incrementAndGet();
            return null;
          }
        });
      }
      catch (IOException ex)
      {
        throw new RuntimeException(ex);
      }

      return count.get();
    }).call();
  }
}
