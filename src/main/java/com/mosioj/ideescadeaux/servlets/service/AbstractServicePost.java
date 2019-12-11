package com.mosioj.ideescadeaux.servlets.service;

import com.mosioj.ideescadeaux.servlets.rootservlet.IdeesCadeauxPostServlet;
import com.mosioj.ideescadeaux.servlets.securitypolicy.root.SecurityPolicy;
import com.mosioj.ideescadeaux.viewhelper.JSonResponseWriter;
import com.mosioj.ideescadeaux.viewhelper.JSonResponseWriter.JSonPair;

public abstract class AbstractServicePost<P extends SecurityPolicy> extends IdeesCadeauxPostServlet<P> {

    // FIXME : 0 remove the entire class when the writer is removed

    protected final JSonResponseWriter writter = new JSonResponseWriter();

    public AbstractServicePost(P policy) {
        super(policy);
    }

    // FIXME : 0 à supprimer
    protected JSonPair makeJSonPair(String key, String value) {
        return writter.makeJSonPair(key, value);
    }
}
