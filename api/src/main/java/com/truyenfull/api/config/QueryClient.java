package com.truyenfull.api.config;

import com.truyenfull.lib.IQueryService;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.transport.THttpClient;
import org.apache.thrift.transport.TTransportException;
import org.springframework.stereotype.Component;

@Component
public class QueryClient extends IQueryService.Client {

    public QueryClient(TProtocolFactory tProtocolFactory) throws TTransportException {
        super(tProtocolFactory.getProtocol(new THttpClient("http://localhost:8081/api")));
    }
}
