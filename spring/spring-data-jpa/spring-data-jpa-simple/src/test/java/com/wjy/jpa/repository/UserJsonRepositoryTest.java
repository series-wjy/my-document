package com.wjy.jpa.repository;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.wjy.jpa.model.UserJson;
import org.assertj.core.util.Maps;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.util.StringUtils;

import javax.transaction.Transactional;
import java.io.IOException;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@DataJpaTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserJsonRepositoryTest {

    @Autowired
    private UserJsonRepository userJsonRepository;

    @BeforeAll
    @Rollback(false)
    @Transactional
    void init() {
        UserJson user = UserJson.builder()
                .name("jackxx").createDate(Instant.now()).updateDate(new Date()).sex("men").email("123456@126.com").build();
        userJsonRepository.saveAndFlush(user);
    }

    /**
     * 测试用User关联关系操作
     */
    @Test
    @Rollback(false)
    public void testUserJson() throws JsonProcessingException {
        UserJson userJson = userJsonRepository.findById(132L).get();
        userJson.setOther(Maps.newHashMap("address","shanghai"));

        ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new ParameterNamesModule())
                .registerModule(new Jdk8Module())
                .registerModule(new JavaTimeModule());
        //empty beans不需要报错，没有就是没有了
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS,false);
        //遇到不可识别字段的时候不要报错，因为前端传进来的字段不可信，可以不要影响正常业务逻辑
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);
        //遇到不可以识别的枚举的时候，为了保证服务的强壮性，建议也不要关心未知的，甚至给个默认的，特别是微服务大家的枚举值随时在变，但是老的服务是不需要跟着一起变的
        objectMapper.configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL,true);
        objectMapper.configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE,true);
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.setDateFormat(new StdDateFormat());

        String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(userJson);
        objectMapper.readValue(json, UserJson.class);
        System.out.println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(userJson));
    }

    /**
     * 自定义 module 返回 ISO 格式的标准时间
     * @throws JsonProcessingException
     */
    @Test
    @Rollback(false)
    public void testUserJsonIso() throws JsonProcessingException {
        UserJson userJson = userJsonRepository.findById(1L).get();
        userJson.setOther(Maps.newHashMap("address","shanghai"));
        //自定义 myInstant解析序列化和反序列化DateTimeFormatter.ISO_ZONED_DATE_TIME这种格式
        SimpleModule myInstant = new SimpleModule("instant", Version.unknownVersion())
                .addSerializer(java.time.Instant.class, new JsonSerializer<Instant>() {
                    @Override
                    public void serialize(java.time.Instant instant,
                                          JsonGenerator jsonGenerator,
                                          SerializerProvider serializerProvider)
                            throws IOException {
                        if (instant == null) {
                            jsonGenerator.writeNull();
                        } else {
                            jsonGenerator.writeObject(instant.toString());
                        }
                    }
                })
                .addDeserializer(Instant.class, new JsonDeserializer<Instant>() {
                    @Override
                    public Instant deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
                        Instant result = null;
                        String text = jsonParser.getText();
                        if (!StringUtils.isEmpty(text)) {
                            result = ZonedDateTime.parse(text, DateTimeFormatter.ISO_ZONED_DATE_TIME).toInstant();
                        }
                        return result;
                    }
                });

        ObjectMapper objectMapper = new ObjectMapper();
        //注册自定义的module
        objectMapper.registerModule(myInstant);
        String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(userJson);
        System.out.println(json);
    }

}
