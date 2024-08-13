package com.example.demo.client;

import com.example.demo.wsdl.GetFxRatesForCurrency;
import com.example.demo.wsdl.GetFxRatesForCurrencyResponse;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import org.springframework.ws.soap.client.core.SoapActionCallback;

public class FxRatesClient extends WebServiceGatewaySupport {

    public GetFxRatesForCurrencyResponse getFxRatesForCurrency(String tp, String ccy, String dtFrom, String dtTo) {
        GetFxRatesForCurrency request = new GetFxRatesForCurrency();
        request.setTp(tp);
        request.setCcy(ccy);
        request.setDtFrom(dtFrom);
        request.setDtTo(dtTo);

        return (GetFxRatesForCurrencyResponse) getWebServiceTemplate().marshalSendAndReceive(
                "https://www.lb.lt/webservices/FxRates/FxRates.asmx",
                request,
                new SoapActionCallback("http://www.lb.lt/WebServices/FxRates/getFxRatesForCurrency")
        );
    }
}
