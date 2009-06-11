/***************************************************************************
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Andre Dietisheim - initial API and implementation
 *    Eike Stepper - maintenance
 **************************************************************************/
package org.eclipse.emf.cdo.common.internal.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.MessageFormat;

import org.eclipse.emf.cdo.common.model.CDOModelConstants;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.net4j.db.DBException;
import org.eclipse.net4j.util.CheckUtil;

/**
 * @author Andre Dietisheim
 */
public class DBRevisionCacheUtil {

	/**
	 * Asserts the given {@link CDORevision} is <tt>null</tt>.
	 * 
	 * @param cdoRevision
	 *            the cdo revision
	 * @param message
	 *            the message to use when throwing the {@link DBException}
	 * @throws DBException
	 *             if the given CDORevision's not <tt>null</tt>
	 */
	public static void assertIsNull(CDORevision cdoRevision, String message) {
		if (cdoRevision != null) {
			throw new DBException(message);
		}
	}

	/**
	 * Gets the name of a revision of a CDOResourceNode.
	 * 
	 * @param revision
	 *            the revision
	 * 
	 * @return the resource node name
	 */
	// TODO: this should be refactored and put in a place, that's more generic
	// than this class. The same snippet's used in LRURevisionCache and
	// MemRevisionCache
	public static String getResourceNodeName(CDORevision revision) {
		CheckUtil.checkArg(revision.isResourceNode(),
				"the revision is not a resource node!");
		EStructuralFeature feature = revision.getEClass()
				.getEStructuralFeature(
						CDOModelConstants.RESOURCE_NODE_NAME_ATTRIBUTE);
		return (String) ((InternalCDORevision) revision).getValue(feature);
	}
}