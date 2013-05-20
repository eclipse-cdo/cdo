/*
 * Copyright (c) 2010-2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Martin Fluegge - initial API and implementation
 */
package org.eclipse.emf.cdo.dawn.resources.impl;

import org.eclipse.emf.cdo.dawn.resources.DawnWrapperResource;
import org.eclipse.emf.cdo.eresource.impl.CDOResourceImpl;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.xmi.DOMHandler;
import org.eclipse.emf.ecore.xmi.DOMHelper;
import org.eclipse.emf.ecore.xml.type.AnyType;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

/**
 * This class wrapper CDOResource and combines it with a XMLResource to avoid problems with all frameworks which are
 * internally using an XMLResource
 * 
 * @author Martin Fluegge
 */
public class DawnWrapperResourceImpl extends CDOResourceImpl implements DawnWrapperResource
{

  public DawnWrapperResourceImpl(URI uri)
  {
    super(uri);
  }

  @Override
  public void setRoot(boolean root)
  {
    super.setRoot(root);
  }

  @Override
  protected void setExisting(boolean existing)
  {
    super.setExisting(existing);

  }

  /************ XML STUFF BEGIN ********************/

  public DOMHelper getDOMHelper()
  {
    throw new UnsupportedOperationException();
  }

  public Map<Object, Object> getDefaultLoadOptions()
  {
    throw new UnsupportedOperationException();
  }

  public Map<Object, Object> getDefaultSaveOptions()
  {
    throw new UnsupportedOperationException();
  }

  public Map<EObject, AnyType> getEObjectToExtensionMap()
  {

    throw new UnsupportedOperationException();
  }

  @Deprecated
  public Map<EObject, String> getEObjectToIDMap()
  {
    throw new UnsupportedOperationException();
  }

  public String getEncoding()
  {
    throw new UnsupportedOperationException();
  }

  public String getID(EObject eObject)
  {
    return getURIFragment(eObject);
  }

  @Deprecated
  public Map<String, EObject> getIDToEObjectMap()
  {
    throw new UnsupportedOperationException();
  }

  public String getPublicId()
  {
    throw new UnsupportedOperationException();
  }

  public String getSystemId()
  {
    throw new UnsupportedOperationException();
  }

  public String getXMLVersion()
  {
    throw new UnsupportedOperationException();
  }

  public void load(Node node, Map<?, ?> options) throws IOException
  {
    throw new UnsupportedOperationException();
  }

  public void load(InputSource inputSource, Map<?, ?> options) throws IOException
  {
    throw new UnsupportedOperationException();
  }

  public void save(Writer writer, Map<?, ?> options) throws IOException
  {
    throw new UnsupportedOperationException();
  }

  public Document save(Document document, Map<?, ?> options, DOMHandler handler)
  {
    throw new UnsupportedOperationException();
  }

  public void setDoctypeInfo(String publicId, String systemId)
  {
    throw new UnsupportedOperationException();
  }

  public void setEncoding(String encoding)
  {
    throw new UnsupportedOperationException();
  }

  public void setID(EObject eObject, String id)
  {
    throw new UnsupportedOperationException();
  }

  public void setUseZip(boolean useZip)
  {
    throw new UnsupportedOperationException();
  }

  public void setXMLVersion(String version)
  {
    throw new UnsupportedOperationException();
  }

  public boolean useZip()
  {
    return false;
  }

  /************ XML STUFF END ********************/
  @Override
  public String toString()
  {

    return "DawnWrapperResource (" + super.toString() + ")";
  }
}
