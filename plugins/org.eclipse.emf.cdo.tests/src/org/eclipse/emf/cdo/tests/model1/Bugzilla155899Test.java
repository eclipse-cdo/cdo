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


import org.eclipse.emf.cdo.client.CDOResource;
import org.eclipse.emf.cdo.client.ResourceManager;

import org.eclipse.emf.ecore.resource.Resource;

import testmodel1.ExtendedNode;
import testmodel1.TreeNode;


/**
 * Bidirectional XRefs are doubled at target side
 * 
 * See https://bugs.eclipse.org/bugs/show_bug.cgi?id=155899
 */
public class Bugzilla155899Test extends AbstractModel1Test
{
  public void testTwoResourcesUniDi() throws Exception
  {
    final String SOURCE_RESOURCE = "/test/source";
    final String TARGET_RESOURCE = "/test/target";
    final String SOURCE = "sourceRoot";
    final String TARGET = "targetRoot";

    { // Execution
      TreeNode target = createNode(TARGET);
      CDOResource targetResource = saveRoot(target, TARGET_RESOURCE);
      ResourceManager resourceManager = targetResource.getResourceManager();
      Resource sourceResource = createResource(SOURCE_RESOURCE, resourceManager);

      TreeNode source = createNode(SOURCE);
      source.getReferences().add(target);
      sourceResource.getContents().add(source);
      resourceManager.commit();
    }

    { // Verification
      TreeNode source = (TreeNode) loadRoot(SOURCE_RESOURCE);
      assertEquals(1, source.getReferences().size());
      assertNode(TARGET, (TreeNode) source.getReferences().get(0));

      TreeNode target = (TreeNode) loadRoot(TARGET_RESOURCE);
      assertEquals(0, target.getReferences().size());
    }
  }

  public void testTwoResourcesBiDi() throws Exception
  {
    final String SOURCE_RESOURCE = "/test/source";
    final String TARGET_RESOURCE = "/test/target";
    final String SOURCE = "sourceRoot";
    final String TARGET = "targetRoot";

    { // Execution
      ExtendedNode target = createExtended(TARGET);
      CDOResource targetResource = saveRoot(target, TARGET_RESOURCE);
      ResourceManager resourceManager = targetResource.getResourceManager();
      Resource sourceResource = createResource(SOURCE_RESOURCE, resourceManager);

      ExtendedNode source = createExtended(SOURCE);
      source.getBidiSource().add(target);
      sourceResource.getContents().add(source);
      resourceManager.commit();
    }

    { // Verification
      ExtendedNode source = (ExtendedNode) loadRoot(SOURCE_RESOURCE);
      assertEquals(1, source.getBidiSource().size());
      assertEquals(0, source.getBidiTarget().size());
      assertNode(TARGET, (ExtendedNode) source.getBidiSource().get(0));

      ExtendedNode target = (ExtendedNode) loadRoot(TARGET_RESOURCE);
      assertEquals(0, target.getBidiSource().size());
      assertEquals(1, target.getBidiTarget().size());
      assertNode(SOURCE, (ExtendedNode) target.getBidiTarget().get(0));
    }
  }

  public void testOneResourceTargetRoot() throws Exception
  {
    final String RESOURCE = "/test/res";
    final String SOURCE = "source";
    final String TARGET = "target";

    { // Execution
      ExtendedNode target = createExtended(TARGET);
      ExtendedNode source = createExtended(SOURCE, target);
      source.getBidiSource().add(target);
      saveRoot(target, RESOURCE);
    }

    { // Verification
      ExtendedNode target = (ExtendedNode) loadRoot(RESOURCE);
      assertEquals(0, target.getBidiSource().size());
      assertEquals(1, target.getBidiTarget().size());

      ExtendedNode source = (ExtendedNode) findChild(SOURCE, target);
      assertEquals(1, source.getBidiSource().size());
      assertEquals(0, source.getBidiTarget().size());
    }
  }

  public void testOneResourceSourceRoot() throws Exception
  {
    final String RESOURCE = "/test/res";
    final String SOURCE = "source";
    final String TARGET = "target";

    { // Execution
      ExtendedNode source = createExtended(SOURCE);
      ExtendedNode target = createExtended(TARGET, source);
      source.getBidiSource().add(target);
      saveRoot(source, RESOURCE);
    }

    { // Verification
      ExtendedNode source = (ExtendedNode) loadRoot(RESOURCE);
      assertEquals(1, source.getBidiSource().size());
      assertEquals(0, source.getBidiTarget().size());

      ExtendedNode target = (ExtendedNode) findChild(TARGET, source);
      assertEquals(0, target.getBidiSource().size());
      assertEquals(1, target.getBidiTarget().size());
    }
  }

  public void testTwoResourcesTwoResMans() throws Exception
  {
    final String SOURCE_RESOURCE = "/test/source";
    final String TARGET_RESOURCE = "/test/target";
    final String SOURCE = "sourceRoot";
    final String TARGET = "targetRoot";

    { // Execution
      ExtendedNode target = createExtended(TARGET);
      saveRoot(target, TARGET_RESOURCE);

      ExtendedNode source = createExtended(SOURCE);
      CDOResource sourceResource = saveRoot(source, SOURCE_RESOURCE);

      ResourceManager resourceManager = sourceResource.getResourceManager();
      Resource targetResource = getResource(TARGET_RESOURCE, resourceManager);
      target = (ExtendedNode) targetResource.getContents().get(0);

      source.getBidiTarget().add(target);
      resourceManager.commit();
    }

    { // Verification
      ExtendedNode source = (ExtendedNode) loadRoot(SOURCE_RESOURCE);
      assertEquals(0, source.getBidiSource().size());
      assertEquals(1, source.getBidiTarget().size());
      assertNode(TARGET, (ExtendedNode) source.getBidiTarget().get(0));

      ExtendedNode target = (ExtendedNode) loadRoot(TARGET_RESOURCE);
      assertEquals(1, target.getBidiSource().size());
      assertEquals(0, target.getBidiTarget().size());
      assertNode(SOURCE, (ExtendedNode) target.getBidiSource().get(0));
    }
  }
}
