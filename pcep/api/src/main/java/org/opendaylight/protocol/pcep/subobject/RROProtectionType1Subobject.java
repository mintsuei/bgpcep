/*
 * Copyright (c) 2013 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.protocol.pcep.subobject;


public class RROProtectionType1Subobject extends RROProtectionSubobject {

    private final boolean secondary;

    private final byte linkFlags;

    /**
     * @param secondary
     * @param linkFlags
     */
    public RROProtectionType1Subobject(boolean secondary, byte linkFlags) {
	super();
	this.secondary = secondary;
	this.linkFlags = linkFlags;
    }

    /**
     * @return the secondary
     */
    public boolean isSecondary() {
	return this.secondary;
    }

    /**
     * @return the linkFlags
     */
    public byte getLinkFlags() {
	return this.linkFlags;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + this.linkFlags;
	result = prime * result + (this.secondary ? 1231 : 1237);
	return result;
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (this.getClass() != obj.getClass())
	    return false;
	final RROProtectionType1Subobject other = (RROProtectionType1Subobject) obj;
	if (this.linkFlags != other.linkFlags)
	    return false;
	if (this.secondary != other.secondary)
	    return false;
	return true;
    }

    @Override
    public String toString() {
	final StringBuilder builder = new StringBuilder();
	builder.append("ProtectionType1Subobject [secondary=");
	builder.append(this.secondary);
	builder.append(", linkFlags=");
	builder.append(this.linkFlags);
	builder.append("]");
	return builder.toString();
    }

}