/*
 * RHQ Management Platform
 * Copyright (C) 2005-2012 Red Hat, Inc.
 * All rights reserved.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation version 2 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */

package org.rhq.enterprise.server.naming.context;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.NoInitialContextException;
import javax.naming.NotContextException;
import javax.naming.directory.SearchControls;
import javax.naming.event.EventDirContext;
import javax.naming.event.NamingListener;

/**
 * 
 *
 * @author Lukas Krejci
 */
public class URLPreferringEventDirContextDecorator extends URLPreferringDirContextDecorator implements EventDirContext {

    private static final long serialVersionUID = 1L;

    public URLPreferringEventDirContextDecorator() {
        super(null);
    }
    
    public URLPreferringEventDirContextDecorator(EventDirContext ctx) {
        super(ctx);
    }
    
    @Override
    protected EventDirContext checkAndCast(Context ctx) throws NamingException {
        if (!(ctx instanceof EventDirContext)) {
            if (ctx == null) {
                throw new NoInitialContextException();
            } else {
                throw new NotContextException(
                    "Not an instance of EventDirContext");
            }
        }
        
        return (EventDirContext) ctx;
    }
    
    @Override
    protected EventDirContext getOriginal() throws NamingException {
        return checkAndCast(super.getOriginal());
    }

    public void addNamingListener(Name target, int scope, NamingListener l) throws NamingException {
        getOriginal().addNamingListener(target, scope, l);
    }

    public void addNamingListener(String target, int scope, NamingListener l) throws NamingException {
        getOriginal().addNamingListener(target, scope, l);
    }

    public void removeNamingListener(NamingListener l) throws NamingException {
        getOriginal().removeNamingListener(l);
    }

    public boolean targetMustExist() throws NamingException {
        return getOriginal().targetMustExist();
    }
    
    public void addNamingListener(Name target, String filter, SearchControls ctls, NamingListener l)
        throws NamingException {
        getOriginal().addNamingListener(target, filter, ctls, l);
    }

    public void addNamingListener(String target, String filter, SearchControls ctls, NamingListener l)
        throws NamingException {
        getOriginal().addNamingListener(target, filter, ctls, l);
    }

    public void
        addNamingListener(Name target, String filter, Object[] filterArgs, SearchControls ctls, NamingListener l)
            throws NamingException {
        getOriginal().addNamingListener(target, filter, filterArgs, ctls, l);
    }

    public void addNamingListener(String target, String filter, Object[] filterArgs, SearchControls ctls,
        NamingListener l) throws NamingException {
        getOriginal().addNamingListener(target, filter, filterArgs, ctls, l);
    }

}
