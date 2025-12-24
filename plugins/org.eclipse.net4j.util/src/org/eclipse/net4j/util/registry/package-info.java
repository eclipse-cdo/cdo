/*
 * Copyright (c) 2011-2013, 2015, 2016, 2023 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */

/**
 * Interfaces and classes for general purpose registries.
 * A registry is like a <code>Map</code> but has some additional features:
 * <ul>
 *   <li>It provides a descriptor framework for registry elements that
 *   	  are to be instantiated lazily
 *   <li>It provides a notification framework that enables clients to
 *       react on events (including the resolution of a lazy descriptor)
 * </ul>
 */
package org.eclipse.net4j.util.registry;
