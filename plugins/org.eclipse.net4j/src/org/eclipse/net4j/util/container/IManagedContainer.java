/***************************************************************************
 * Copyright (c) 2004-2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.util.container;

import org.eclipse.net4j.util.factory.IFactory;
import org.eclipse.net4j.util.factory.IFactoryKey;
import org.eclipse.net4j.util.registry.IRegistry;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * @author Eike Stepper
 */
public interface IManagedContainer extends IContainer
{
  public IRegistry<IFactoryKey, IFactory> getFactoryRegistry();

  public List<IElementProcessor> getPostProcessors();

  public void addPostProcessor(IElementProcessor postProcessor);

  public void removePostProcessor(IElementProcessor postProcessor);

  public Object[] getElements(String productGroup);

  public Object[] getElements(String productGroup, String factoryType);

  public Object getElement(String productGroup, String factoryType, String description);

  public Object removeElement(String productGroup, String factoryType, String description);

  public void clearElements();

  public void loadElements(InputStream stream) throws IOException;

  public void saveElements(OutputStream stream) throws IOException;
}
