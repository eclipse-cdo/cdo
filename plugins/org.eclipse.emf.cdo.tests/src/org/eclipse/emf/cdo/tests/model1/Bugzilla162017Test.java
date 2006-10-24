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


import testmodel1.ExtendedNode;


/**
 * transmit object change using incorrect EClass for attributes.
 * 
 * When an attribute is changed in an EClass with a super class, and that 
 * attribute happens to be in the super class definition, the changes transmitted 
 * to the CDO server include the super class EClass, not the class that was modified.
 *
 * To temporarily fix this I added an new argument to the rememberAttributeChange() 
 * method. I added the EClass returned from the classInfo.getEClass() method. 
 * In this way rememberAttributeChange() does not depend of finding the EClass 
 * from the feature itself. A patch follows.
 * 
 * See https://bugs.eclipse.org/bugs/show_bug.cgi?id=162017
 */
public class Bugzilla162017Test extends AbstractModel1Test
{
  public void testChangeSuperClassAttribute() throws Exception
  {
    {
      ExtendedNode node = createExtended("extended");
      node.setBooleanFeature(true);
      node.setIntFeature(4711);
      node.setStringFeature("tree node feature");
      node.setStringFeature2("extended node feature");
      saveRoot(node, "/test/res");
    }

    {
      ExtendedNode node = (ExtendedNode) loadRoot("/test/res");
      assertEquals(true, node.isBooleanFeature());
      assertEquals(4711, node.getIntFeature());
      assertEquals("tree node feature", node.getStringFeature());
      assertEquals("extended node feature", node.getStringFeature2());

      node.setStringFeature("changed tree node feature");
      node.eResource().save(null);
    }

    {
      ExtendedNode node = (ExtendedNode) loadRoot("/test/res");
      assertEquals(true, node.isBooleanFeature());
      assertEquals(4711, node.getIntFeature());
      assertEquals("changed tree node feature", node.getStringFeature());
      assertEquals("extended node feature", node.getStringFeature2());
    }
  }
}
