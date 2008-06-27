/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Simon McDuff - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.common.query;

import org.eclipse.net4j.util.collection.CloseableIterator;

import java.util.concurrent.Future;

/**
 * @author Simon McDuff
 */
public interface ResultReaderQueue<T> extends CloseableIterator<T>, Future<T>
{
}
