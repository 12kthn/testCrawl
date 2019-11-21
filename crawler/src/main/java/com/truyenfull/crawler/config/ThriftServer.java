/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.truyenfull.crawler.config;

import com.truyenfull.crawler.service.CrawlerService;
import com.truyenfull.lib.ICrawlerService;
import com.truyenfull.lib.IQueryService;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.server.TServlet;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ThriftServer {

    @Bean
    public TProtocolFactory tProtocolFactory() {
        return new TBinaryProtocol.Factory();
    }

    @Bean
    public ServletRegistrationBean<TServlet> thriftBookServlet(TProtocolFactory protocolFactory, CrawlerService service) {
        TServlet tServlet = new TServlet(new ICrawlerService.Processor<>(service), protocolFactory);

        return new ServletRegistrationBean<TServlet>(tServlet, "/api");
    }
}
