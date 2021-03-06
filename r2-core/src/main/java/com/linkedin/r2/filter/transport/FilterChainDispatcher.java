/*
   Copyright (c) 2012 LinkedIn Corp.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/

/* $Id$ */
package com.linkedin.r2.filter.transport;


import com.linkedin.r2.filter.FilterChain;
import com.linkedin.r2.message.RequestContext;
import com.linkedin.r2.message.rest.RestRequest;
import com.linkedin.r2.message.rest.RestResponse;
import com.linkedin.r2.message.stream.StreamRequest;
import com.linkedin.r2.message.stream.StreamResponse;
import com.linkedin.r2.transport.common.bridge.common.TransportCallback;
import com.linkedin.r2.transport.common.bridge.server.TransportDispatcher;

import java.util.Map;

/**
 * {@link TransportDispatcher} adapter which composes a {@link TransportDispatcher} and a
 * {@link FilterChain}.
 *
 * @author Chris Pettitt
 * @version $Revision$
 */
public class FilterChainDispatcher implements TransportDispatcher
{
  private final FilterChain _filters;

  /**
  * Construct a new instance by composing the specified {@link TransportDispatcher} and
  * {@link FilterChain}.
  *
  * @param dispatcher the {@link TransportDispatcher} to be composed.
  * @param filters the {@link FilterChain} to be composed.
  */
  public FilterChainDispatcher(TransportDispatcher dispatcher,
                               FilterChain filters)
  {
    ServerQueryTunnelFilter serverQueryTunnelFilter = new ServerQueryTunnelFilter();
    ResponseFilter responseFilter = new ResponseFilter();
    DispatcherRequestFilter dispatcherRequestFilter = new DispatcherRequestFilter(dispatcher);
    _filters = filters
            .addFirstRest(serverQueryTunnelFilter)
            .addFirstRest(responseFilter)
            .addLastRest(dispatcherRequestFilter)
            .addFirst(serverQueryTunnelFilter)
            .addFirst(responseFilter)
            .addLast(dispatcherRequestFilter);
  }

  @Override
  public void handleRestRequest(RestRequest req, Map<String, String> wireAttrs,
                                       RequestContext requestContext, TransportCallback<RestResponse> callback)
  {
    ResponseFilter.registerCallback(callback, requestContext);
    _filters.onRestRequest(req, requestContext, wireAttrs);
  }

  @Override
  public void handleStreamRequest(StreamRequest req, Map<String, String> wireAttrs,
                                RequestContext requestContext,
                                TransportCallback<StreamResponse> callback)
  {
    ResponseFilter.registerCallback(callback, requestContext);
    _filters.onStreamRequest(req, requestContext, wireAttrs);
  }
}
