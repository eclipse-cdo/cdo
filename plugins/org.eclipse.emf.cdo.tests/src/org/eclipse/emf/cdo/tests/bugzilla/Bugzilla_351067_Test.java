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
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.bundle.OM;
import org.eclipse.emf.cdo.tests.model3.Image;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.net4j.util.io.IOUtil;

import java.io.InputStream;

/**
 * @author Egidijus Vaisnora
 */
public class Bugzilla_351067_Test extends AbstractCDOTest
{
  public void testCommitBlob() throws Exception
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

      transaction.commit();
    }
    finally
    {
      IOUtil.close(inputStream);
    }

    try
    {
      inputStream = OM.BUNDLE.getInputStream("backup-tests/Ecore.uml");
      CDOBlob blob = new CDOBlob(inputStream);

      Image image = getModel3Factory().createImage();
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
}
