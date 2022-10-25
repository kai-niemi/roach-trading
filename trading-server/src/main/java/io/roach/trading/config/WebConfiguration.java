package io.roach.trading.config;

import java.time.format.DateTimeFormatter;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.format.Formatter;
import org.springframework.format.FormatterRegistry;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.UriTemplate;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.hateoas.mediatype.hal.CurieProvider;
import org.springframework.hateoas.mediatype.hal.DefaultCurieProvider;
import org.springframework.hateoas.mediatype.hal.forms.HalFormsConfiguration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import io.roach.trading.api.support.Money;
import io.roach.trading.domain.home.LinkRelations;

@Configuration
@EnableHypermediaSupport(type = {
        EnableHypermediaSupport.HypermediaType.HAL_FORMS, EnableHypermediaSupport.HypermediaType.HAL})
@EnableSpringDataWebSupport
@EnableWebMvc
public class WebConfiguration implements WebMvcConfigurer {
    public static LocalDateTimeSerializer ISO_DATETIME_SERIALIZER
            = new LocalDateTimeSerializer(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        JavaTimeModule module = new JavaTimeModule();
        module.addSerializer(ISO_DATETIME_SERIALIZER);
        return new ObjectMapper()
                .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                .setSerializationInclusion(JsonInclude.Include.NON_EMPTY)
                .registerModule(module);
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(String.class, Currency.class, Currency::getInstance);
        registry.addConverter(String.class, Money.class, Money::of);

        registry.addFormatter(new Formatter<Money>() {
            @Override
            public String print(Money object, Locale locale) {
                return object.toString();
            }

            @Override
            public Money parse(String text, Locale locale) {
                return Money.of(text);
            }
        });
    }

    @Bean
    public HalFormsConfiguration halFormsConfiguration() {
        return new HalFormsConfiguration();
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        if (!registry.hasMappingForPattern("/webjars/**")) {
            registry.addResourceHandler("/webjars/**")
                    .addResourceLocations("classpath:/META-INF/resources/webjars/");
        }
        if (!registry.hasMappingForPattern("/**")) {
            registry.addResourceHandler("/**")
                    .addResourceLocations("classpath:/META-INF/resources/",
                            "classpath:/resources/",
                            "classpath:/static/",
                            "classpath:/public/");
        }
        if (!registry.hasMappingForPattern("/browser/**")) {
            registry.addResourceHandler("/browser/**").addResourceLocations(
                    "/webjars/hal-explorer/1.1.0/");
        }
    }

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer
                .favorParameter(false)
                .defaultContentType(
                        MediaTypes.HAL_FORMS_JSON,
                        MediaTypes.HAL_JSON,
//                        MediaTypes.VND_ERROR_JSON,
                        MediaType.APPLICATION_JSON,
                        MediaType.APPLICATION_PROBLEM_JSON,
                        MediaType.ALL);
    }


    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(new FormHttpMessageConverter());
    }

    @Bean
    public CurieProvider defaultCurieProvider() {
        String uri = ServletUriComponentsBuilder.newInstance()
                .scheme("http")
                .host("localhost")
                .pathSegment("rels", "{rel}.html")
                .build().toUriString();
        return new DefaultCurieProvider(LinkRelations.CURIE_NAMESPACE, UriTemplate.of(uri));
    }
}
