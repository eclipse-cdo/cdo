/***************************************************************************
 * Copyright (c) 2004, 2005, 2006 Eike Stepper, Fuggerstr. 39, 10777 Berlin, Germany.
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

import java.io.File;


public class SerializationTest extends AbstractModel1Test
{
  public void testExport() throws Exception
  {
    final String RESOURCE = "/test/res";
    final String ROOT = "root";
    final String[] CHILDREN = { "a", "b", "c"};

    final File FILE = new File("testExport.testmodel1");
    final String CONTENT = "<?xml version=\"1.0\" encoding=\"ASCII\"?>\r\n"
        + "<testmodel1:TreeNode xmi:version=\"2.0\" xmlns:xmi=\"http://www.omg.org/XMI\" xmlns:testmodel1=\"http://www.eclipse.org/emf/cdo/2006/TestModel1\" stringFeature=\"root\">\r\n"
        + "  <children stringFeature=\"a\"/>\r\n" + "  <children stringFeature=\"b\"/>\r\n"
        + "  <children stringFeature=\"c\"/>\r\n" + "</testmodel1:TreeNode>\r\n";

    try
    {
      { // Execution
        TreeNode root = createNode(ROOT);
        createChildren(CHILDREN, root);
        saveRoot(root, RESOURCE);

        TreeNode loaded = (TreeNode) loadRoot(RESOURCE);
        preLoadResource((CDOResource) loaded.eResource());
        EObject copied = EcoreUtil.copy(loaded);

        ResourceSet resourceSet = new ResourceSetImpl();
        Map map = resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap();
        map.put("testmodel1", new XMIResourceFactoryImpl());

        URI uri = URI.createFileURI(FILE.getAbsolutePath());
        Resource xmiResource = resourceSet.createResource(uri);
        xmiResource.getContents().add(copied);
        xmiResource.save(new HashMap());
      }

      { // Verification
        String content = IOHelper.readFully(FILE);
        assertEquals(CONTENT, content);
      }
    }
    finally
    {
      FILE.delete();
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
}
