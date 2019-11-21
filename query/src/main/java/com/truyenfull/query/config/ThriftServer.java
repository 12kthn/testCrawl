/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.truyenfull.query.config;

import com.truyenfull.lib.IQueryService;
import com.truyenfull.query.service.QueryService;
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
    public ServletRegistrationBean<TServlet> thriftBookServlet(TProtocolFactory protocolFactory, QueryService service) {
        TServlet tServlet = new TServlet(new IQueryService.Processor<>(service), protocolFactory);

        return new ServletRegistrationBean<TServlet>(tServlet, "/api");
    }
}
