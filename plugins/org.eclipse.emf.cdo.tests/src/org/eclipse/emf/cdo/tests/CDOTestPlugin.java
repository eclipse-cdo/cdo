/**
 * <copyright>
 *
 * Copyright (c) 2002-2004 IBM Corporation and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   IBM - Initial API and implementation
 *
 * </copyright>
 *
 * $Id: CDOTestPlugin.java,v 1.1 2006-05-29 23:06:29 nickb Exp $
 */
package org.eclipse.emf.cdo.tests;

import org.eclipse.core.runtime.Plugin;

public class CDOTestPlugin 
extends Plugin
{
    private static CDOTestPlugin instance;
    
    public CDOTestPlugin()
    {
        super();
        instance = this;
    }

    public static CDOTestPlugin getPlugin()
    {
        return instance;
    }
}
