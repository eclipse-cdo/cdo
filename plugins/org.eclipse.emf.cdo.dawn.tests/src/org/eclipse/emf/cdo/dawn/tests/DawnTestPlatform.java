/*
 * Copyright (c) 2010-2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Martin Fluegge - initial API and implementation
 */
package org.eclipse.emf.cdo.dawn.tests;

import org.eclipse.net4j.util.om.OMPlatform;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Date;

/**
 * @author Martin Fluegge
 */
public class DawnTestPlatform
{
  public static DawnTestPlatform instance = new DawnTestPlatform();

  private String tempTestFolder = "";

  private String resourcesFolderName = "testdata";

  public DawnTestPlatform()
  {
    tempTestFolder = OMPlatform.INSTANCE.getProperty("java.io.tmpdir") + "/dawntests/test_" + new Date().getTime();
  }

  public String getTestFolder()
  {
    return tempTestFolder;
  }

  public File getTestResource(String path) throws URISyntaxException
  {
    File file = new File(getTestResourceURI(path));
    return file;
  }

  public java.net.URI getTestResourceURI(String path) throws URISyntaxException
  {
    String testFolder = getBundlePathForClass(AbstractDawnTest.class);

    String separator = path.startsWith("/") ? "" : "/";

    java.net.URI uri = new java.net.URI(testFolder + "/" + resourcesFolderName + separator + path);
    return uri;
  }

  public String getBundlePathForClass(Class<?> clazz) throws URISyntaxException
  {
    URL resourceURI = clazz.getResource("");
    return resourceURI.toString().substring(0, resourceURI.toString().lastIndexOf("/bin"));
  }
}
