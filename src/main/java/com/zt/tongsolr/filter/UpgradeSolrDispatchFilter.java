package com.zt.tongsolr.filter;

import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.request.SolrRequestHandler;
import org.apache.solr.response.SolrQueryResponse;
import org.apache.solr.servlet.SolrDispatchFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class UpgradeSolrDispatchFilter extends SolrDispatchFilter {
    public static final String CONTEXT_SERVLET_REQUEST = "servletRequest";
    public static final String CONTEXT_SERVLET_RESPONSE = "servletResponse";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {
        request.setAttribute("response", response);
        super.doFilter(request, response, chain);
    }

    @Override
    protected void execute(HttpServletRequest servletRequest, SolrRequestHandler handler, SolrQueryRequest solrRequest,
            SolrQueryResponse solrResponse) {
        Object servletResponse = servletRequest.getAttribute("response");
        servletRequest.removeAttribute("response");
        solrRequest.getContext().put(CONTEXT_SERVLET_REQUEST, servletRequest);
        solrRequest.getContext().put(CONTEXT_SERVLET_RESPONSE, servletResponse);
        super.execute(servletRequest, handler, solrRequest, solrResponse);
    }
}
