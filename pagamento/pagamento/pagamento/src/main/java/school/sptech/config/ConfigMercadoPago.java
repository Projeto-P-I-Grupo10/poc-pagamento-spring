package school.sptech.config;

import com.mercadopago.MercadoPagoConfig;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
public class ConfigMercadoPago {

    @PostConstruct
    public void init(){
        MercadoPagoConfig.setAccessToken("token");
    }
}