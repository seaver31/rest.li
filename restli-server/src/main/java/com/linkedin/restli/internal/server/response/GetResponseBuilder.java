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

/**
 * $Id: $
 */

package com.linkedin.restli.internal.server.response;


import com.linkedin.data.DataMap;
import com.linkedin.data.template.RecordTemplate;
import com.linkedin.r2.message.rest.RestRequest;
import com.linkedin.restli.common.HttpStatus;
import com.linkedin.restli.internal.server.RoutingResult;
import com.linkedin.restli.internal.server.methods.AnyRecord;
import com.linkedin.restli.internal.server.util.RestUtils;
import com.linkedin.restli.server.GetResult;
import com.linkedin.restli.server.ResourceContext;

import com.linkedin.restli.server.RestLiResponseData;
import java.net.HttpCookie;
import java.util.List;
import java.util.Map;


public class GetResponseBuilder implements RestLiResponseBuilder<RestLiResponseData<GetResponseEnvelope>>
{
  @Override
  public PartialRestResponse buildResponse(RoutingResult routingResult, RestLiResponseData<GetResponseEnvelope> responseData)
  {
    return new PartialRestResponse.Builder()
        .headers(responseData.getHeaders())
        .cookies(responseData.getCookies())
        .status(responseData.getResponseEnvelope().getStatus())
        .entity(responseData.getResponseEnvelope().getRecord())
        .build();
  }

  @Override
  public RestLiResponseData<GetResponseEnvelope> buildRestLiResponseData(RestRequest request,
                                                        RoutingResult routingResult,
                                                        Object result,
                                                        Map<String, String> headers,
                                                        List<HttpCookie> cookies)
  {
    final RecordTemplate record;
    final HttpStatus status;
    if (result instanceof GetResult)
    {
      final GetResult<?> getResult = (GetResult<?>) result;
      record = getResult.getValue();
      status = getResult.getStatus();
    }
    else
    {
      record = (RecordTemplate) result;
      status = HttpStatus.S_200_OK;
    }
    final ResourceContext resourceContext = routingResult.getContext();
    final DataMap data = RestUtils.projectFields(record.data(), resourceContext.getProjectionMode(),
                                                 resourceContext.getProjectionMask());

    return new RestLiResponseDataImpl<>(new GetResponseEnvelope(status, new AnyRecord(data)), headers, cookies);
  }
}