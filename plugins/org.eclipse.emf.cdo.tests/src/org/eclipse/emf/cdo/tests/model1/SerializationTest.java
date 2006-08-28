/***************************************************************************
 * Copyright (c) 2004, 2005, 2006 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.tests.model1;


import org.eclipse.emf.cdo.client.CDOPersistable;
import org.eclipse.emf.cdo.client.CDOResource;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

import org.eclipse.net4j.util.IOHelper;

import testmodel1.TreeNode;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;


public class SerializationTest extends AbstractModel1Test
{
  public void testExport() throws Exception
  {
    final String RESOURCE = "/test/res";
    final String ROOT = "root";
    final String[] CHILDREN = { "a", "b", "c"};

    final File FILE = new File("testExport.testmodel1");
    final String CONTENT = "<?xml version=\"1.0\" encoding=\"ASCII\"?>\n"
        + "<testmodel1:TreeNode xmi:version=\"2.0\" xmlns:xmi=\"http://www.omg.org/XMI\" xmlns:testmodel1=\"http://www.eclipse.org/emf/cdo/2006/TestModel1\" stringFeature=\"root\">\n" //
        + "  <children stringFeature=\"a\"/>\n" // 
        + "  <children stringFeature=\"b\"/>\n" //
        + "  <children stringFeature=\"c\"/>\n" //
        + "</testmodel1:TreeNode>\n";

    try
    {
      { // Execution
        TreeNode root = createNode(ROOT);
        createChildren(CHILDREN, root);
        saveRoot(root, RESOURCE);

        TreeNode loaded = (TreeNode) loadRoot(RESOURCE);
        preLoadResource((CDOResource) loaded.eResource());
        EObject copied = EcoreUtil.copy(loaded);

        ResourceSet resourceSet = createXMIResourceSet();
        URI uri = URI.createFileURI(FILE.getAbsolutePath());
        Resource xmiResource = resourceSet.createResource(uri);
        xmiResource.getContents().add(copied);
        xmiResource.save(new HashMap());
      }

      { // Verification
        String content = IOHelper.readFully(FILE).replaceAll("\r", "");
        assertEquals(CONTENT, content);
      }
    }
    finally
    {
      FILE.delete();
    }
  }

  public void testExportThreeLevels() throws Exception
  {
    final String RESOURCE = "/test/res";
    final String ROOT = "root";
    final String[] CHILDREN = { "a", "b", "c"};

    final File FILE = new File("testExport.testmodel1");
    final String CONTENT = "<?xml version=\"1.0\" encoding=\"ASCII\"?>\n"
        + "<testmodel1:TreeNode xmi:version=\"2.0\" xmlns:xmi=\"http://www.omg.org/XMI\" xmlns:testmodel1=\"http://www.eclipse.org/emf/cdo/2006/TestModel1\" stringFeature=\"root\">\n" //
        + "  <children stringFeature=\"a\">\n" // 
        + "    <children stringFeature=\"a\"/>\n" //
        + "    <children stringFeature=\"b\"/>\n" //
        + "    <children stringFeature=\"c\"/>\n" //
        + "  </children>\n" // 
        + "  <children stringFeature=\"b\">\n" // 
        + "    <children stringFeature=\"a\"/>\n" //
        + "    <children stringFeature=\"b\"/>\n" //
        + "    <children stringFeature=\"c\"/>\n" //
        + "  </children>\n" // 
        + "  <children stringFeature=\"c\">\n" // 
        + "    <children stringFeature=\"a\"/>\n" //
        + "    <children stringFeature=\"b\"/>\n" //
        + "    <children stringFeature=\"c\"/>\n" //
        + "  </children>\n" // 
        + "</testmodel1:TreeNode>\n";

    try
    {
      { // Execution
        TreeNode root = createNode(ROOT);
        TreeNode[] children = createChildren(CHILDREN, root);
        for (int i = 0; i < children.length; i++)
          createChildren(CHILDREN, children[i]);
        saveRoot(root, RESOURCE);

        TreeNode loaded = (TreeNode) loadRoot(RESOURCE);
        preLoadResource((CDOResource) loaded.eResource());
        EObject copied = EcoreUtil.copy(loaded);

        ResourceSet resourceSet = createXMIResourceSet();
        URI uri = URI.createFileURI(FILE.getAbsolutePath());
        Resource xmiResource = resourceSet.createResource(uri);
        xmiResource.getContents().add(copied);
        xmiResource.save(new HashMap());
      }

      { // Verification
        String content = IOHelper.readFully(FILE).replaceAll("\r", "");
        assertEquals(CONTENT, content);
      }
    }
    finally
    {
      FILE.delete();
    }
  }

  public void testImport() throws Exception
  {
    final String RESOURCE = "/test/res";
    final String ROOT = "root";
    final String[] CHILDREN = { "a", "b", "c"};

    final File FILE = new File("testExport.testmodel1");
    final String CONTENT = "<?xml version=\"1.0\" encoding=\"ASCII\"?>\n"
        + "<testmodel1:TreeNode xmi:version=\"2.0\" xmlns:xmi=\"http://www.omg.org/XMI\" xmlns:testmodel1=\"http://www.eclipse.org/emf/cdo/2006/TestModel1\" stringFeature=\"root\">\n"
        + "  <children stringFeature=\"a\"/>\n" + "  <children stringFeature=\"b\"/>\n"
        + "  <children stringFeature=\"c\"/>\n" + "</testmodel1:TreeNode>\n";

    try
    {
      { // Execution
        saveFile(FILE, CONTENT);

        ResourceSet resourceSet = createXMIResourceSet();
        URI uri = URI.createFileURI(FILE.getAbsolutePath());
        Resource xmiResource = resourceSet.getResource(uri, true);
        TreeNode root = (TreeNode) xmiResource.getContents().get(0);

        EObject copied = EcoreUtil.copy(root);
        saveRoot(copied, RESOURCE);
      }

      { // Verification
        TreeNode root = (TreeNode) loadRoot(RESOURCE);
        assertNode(ROOT, root);

        EList children = root.getChildren();
        assertNode(CHILDREN[0], (TreeNode) children.get(0));
        assertNode(CHILDREN[1], (TreeNode) children.get(1));
        assertNode(CHILDREN[2], (TreeNode) children.get(2));
      }
    }
    finally
    {
      FILE.delete();
    }
  }

  protected ResourceSetImpl createXMIResourceSet()
  {
    ResourceSetImpl resourceSet = new ResourceSetImpl();
    Map map = resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap();
    map.put("testmodel1", new XMIResourceFactoryImpl());
    return resourceSet;
  }

  protected void saveFile(final File file, String content) throws IOException
  {
    InputStream is = null;
    OutputStream os = null;

    try
    {
      is = new ByteArrayInputStream(content.getBytes());
      os = new FileOutputStream(file);
      IOHelper.copy(is, os);
    }
    finally
    {
      IOHelper.close(is);
      IOHelper.close(os);
    }
  }

  protected void preLoadResource(CDOResource cdoResource)
  {
    for (Iterator it = EcoreUtil.getAllContents(cdoResource, true); it.hasNext();)
    {
      CDOPersistable persistable = (CDOPersistable) it.next();
      persistable.cdoLoad();
    }
  }

  @Deprecated
  protected void assertFileContent(String content, File file) throws IOException
  {
    String[] lines = content.split("\n");
    FileInputStream stream = null;

    try
    {
      stream = new FileInputStream(file);
      BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

      for (int i = 0; i < lines.length; i++)
      {
        String expectedLine = lines[i];
        String fileLine = reader.readLine();
        assertEquals(expectedLine, fileLine);
      }
    }
    finally
    {
      IOHelper.close(stream);
    }
  }
}
