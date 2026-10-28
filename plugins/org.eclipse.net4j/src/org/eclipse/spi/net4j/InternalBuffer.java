/*
 * Copyright (c) 2008, 2011, 2012, 2018, 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.spi.net4j;

import org.eclipse.net4j.buffer.IBuffer;
import org.eclipse.net4j.buffer.IBufferProvider;

/**
 * Internal buffer contract for storage that can be associated with a provider and terminally disposed.
 * <p>
 * {@link IBuffer#release()} relinquishes the current lease and may return storage to the associated provider for reuse.
 * {@link #dispose()} ends the storage lifetime permanently; a disposed buffer cannot be cleared or reused. External
 * buffers can implement {@link IBuffer} without implementing this internal pooled-storage contract.
 *
 * @author Eike Stepper
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface InternalBuffer extends IBuffer
{
  /**
   * Returns the provider associated with this internal buffer.
   *
   * @return The current provider, or <code>null</code> if no provider is associated.
   */
  @Override
  @SuppressWarnings("deprecation")
  public IBufferProvider getBufferProvider();

  public void setBufferProvider(IBufferProvider bufferProvider);

  /**
   * Permanently disposes this buffer's storage. This is distinct from {@link IBuffer#release()}, which may return a
   * reusable buffer to its provider.
   */
  public void dispose();
}
