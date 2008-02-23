/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Martin Taal - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.server.hibernate.teneo;

import org.eclipse.emf.cdo.server.hibernate.IHibernateMappingProvider;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.emf.teneo.extension.ExtensionManager;
import org.eclipse.emf.teneo.extension.ExtensionManagerFactory;
import org.eclipse.emf.teneo.extension.ExtensionUtil;
import org.eclipse.emf.teneo.hibernate.mapper.MappingContext;
import org.eclipse.emf.teneo.hibernate.mapper.MappingUtil;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

/**
 * Handles different cases of input modes: the modelObjects can be a collection of String (containing an ecore model) or
 * EPackage objects.
 * 
 * @author Martin Taal
 */
public class TeneoHibernateMappingProvider implements IHibernateMappingProvider
{

  // the passed modelObjects collection is defined as a collection of Objects
  // to prevent binary dependency on emf.
  public String provideMapping(Collection<Object> modelObjects, Properties properties)
  {
    // TODO: handle nested package structures
    final List<EPackage> epacks = new ArrayList<EPackage>();
    final ResourceSet rs = new ResourceSetImpl();
    rs.getResourceFactoryRegistry().getExtensionToFactoryMap().put("ecore", new EcoreResourceFactoryImpl());
    for (Object o : modelObjects)
    {
      if (o instanceof EPackage)
      {
        epacks.addAll(resolveSubPackages((EPackage)o));
      }
      else
      { // assume a String read from it
        final String ecoreStr = (String)o;
        // this assumes that the (default) encoding is the same on both the client and
        // server
        final ByteArrayInputStream bis = new ByteArrayInputStream(ecoreStr.getBytes());
        // fool the resourceset by passing a fake uri
        final URI epackageURI = URI.createURI("epackage.ecore");
        final Resource resource = rs.createResource(epackageURI);
        try
        {
          resource.load(bis, Collections.EMPTY_MAP);

          // now the toplevel content should be EPackage
          for (Object contentObject : resource.getContents())
          {
            epacks.addAll(resolveSubPackages((EPackage)contentObject));
          }
        }
        catch (Exception e)
        {
          throw new CDOTeneoException("Exception when loading: " + ecoreStr, e);
        }
      }
    }

    // translate the list of EPackages to an array
    final EPackage[] ePackageArray = epacks.toArray(new EPackage[epacks.size()]);

    // register the custom cdo extensions
    final ExtensionManager extensionManager = ExtensionManagerFactory.getInstance().create();
    registerCDOExtensions(extensionManager);

    // and go for it!
    return MappingUtil.generateMapping(ePackageArray, properties, extensionManager);
  }

  public void registerCDOExtensions(ExtensionManager extensionManager)
  {
    MappingUtil.registerHbExtensions(extensionManager);
    extensionManager.registerExtension(ExtensionUtil.createExtension(MappingContext.class, CDOMappingContext.class,
        false));
  }

  protected List<EPackage> resolveSubPackages(EPackage epack)
  {
    final List<EPackage> epacks = new ArrayList<EPackage>();
    epacks.add(epack);
    for (EPackage subEPackage : epack.getESubpackages())
    {
      epacks.addAll(resolveSubPackages(subEPackage));
    }
    return epacks;
  }

}
