/*
 * Copyright (c) 2018, 2023 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.trace;

import org.eclipse.net4j.buffer.BufferState;
import org.eclipse.net4j.trace.Element.BufferElement;
import org.eclipse.net4j.util.factory.ProductCreationException;

import java.util.Map;

/**
 * @author Eike Stepper
 */
public interface Listener
{
  public void methodCalled(Element caller, String callingMethod, Element callee, String calledMethod);

  public void elementCreated(Element element);

  public void ownerChanged(BufferElement element, Element oldOwner, Element newOwner);

  public void threadChanged(BufferElement element, Element oldThread, Element newThread);

  public void stateChanged(BufferElement element, BufferState oldState, BufferState newState);

  public void positionChanged(BufferElement element, int oldPosition, int newPosition);

  public void limitChanged(BufferElement element, int oldLimit, int newLimit);

  public void eosChanged(BufferElement element, boolean newEOS);

  public void ccamChanged(BufferElement element, boolean newCCAM);

  /**
   * @author Eike Stepper
   */
  public static abstract class Factory extends org.eclipse.net4j.util.factory.PropertiesFactory
  {
    public static final String PRODUCT_GROUP = "org.eclipse.net4j.trace.listeners"; //$NON-NLS-1$

    public Factory(String type)
    {
      super(PRODUCT_GROUP, type);
    }

    @Override
    protected abstract Listener create(Map<String, String> properties) throws ProductCreationException;
  }
}
