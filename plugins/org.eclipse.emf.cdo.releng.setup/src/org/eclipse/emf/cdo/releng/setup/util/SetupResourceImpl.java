/*
 * Copyright (c) 2013 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.releng.setup.util;

import org.eclipse.emf.cdo.releng.internal.setup.SetupTaskMigrator;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.emf.ecore.xmi.XMLLoad;
import org.eclipse.emf.ecore.xmi.XMLSave;
import org.eclipse.emf.ecore.xmi.impl.SAXXMIHandler;
import org.eclipse.emf.ecore.xmi.impl.XMILoadImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;
import org.eclipse.emf.ecore.xmi.impl.XMISaveImpl;

import org.w3c.dom.Element;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * <!-- begin-user-doc -->
 * The <b>Resource </b> associated with the package.
 * @implements org.eclipse.emf.cdo.releng.setup.util.SetupResource
 * <!-- end-user-doc -->
 * @see org.eclipse.emf.cdo.releng.setup.util.SetupResourceFactoryImpl
 * @generated
 */
public class SetupResourceImpl extends XMIResourceImpl implements SetupResource
{
  private static final String TOOL_VERSION_ATTRIBUTE = "setup:toolVersion";

  private int toolVersion;

  /**
   * Creates an instance of the resource.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param uri the URI of the new resource.
   * @generated
   */
  public SetupResourceImpl(URI uri)
  {
    super(uri);
  }

  public int getToolVersion()
  {
    return toolVersion;
  }

  @Override
  public void doLoad(InputStream inputStream, Map<?, ?> options) throws IOException
  {
    super.doLoad(inputStream, options);
  }

  @Override
  protected XMLSave createXMLSave()
  {
    return new XMISaveImpl(createXMLHelper())
    {
      @Override
      public void addNamespaceDeclarations()
      {
        String toolVersion = Integer.toString(SetupTaskMigrator.TOOL_VERSION);
        if (!toDOM)
        {
          doc.addAttribute(TOOL_VERSION_ATTRIBUTE, toolVersion);
        }
        else
        {
          ((Element)currentNode).setAttribute(TOOL_VERSION_ATTRIBUTE, toolVersion);
        }

        super.addNamespaceDeclarations();
      }
    };
  }

  @Override
  protected XMLLoad createXMLLoad()
  {
    return new XMILoadImpl(createXMLHelper())
    {
      @Override
      protected DefaultHandler makeDefaultHandler()
      {
        return new SAXXMIHandler(resource, helper, options)
        {
          {
            notFeatures.add(TOOL_VERSION_ATTRIBUTE);
          }

          @Override
          protected void processElement(String name, String prefix, String localName)
          {
            boolean isRoot = this.isRoot;

            super.processElement(name, prefix, localName);

            if (localName.equals(XMIResource.XMI_TAG_NAME) || isRoot)
            {
              String value = attribs.getValue(TOOL_VERSION_ATTRIBUTE);
              if (value != null)
              {
                toolVersion = Integer.parseInt(value);
              }
            }
          }
        };
      }
    };
  }
} // SetupResourceImpl
