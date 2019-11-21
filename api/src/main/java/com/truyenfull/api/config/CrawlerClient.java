package com.truyenfull.api.config;

import com.truyenfull.lib.ICrawlerService;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.transport.THttpClient;
import org.apache.thrift.transport.TTransportException;
import org.springframework.stereotype.Component;

@Component
public class CrawlerClient extends ICrawlerService.Client{

	public CrawlerClient(TProtocolFactory tProtocolFactory) throws TTransportException {
        super(tProtocolFactory.getProtocol(new THttpClient("http://localhost:8082/api")));
    }

}
